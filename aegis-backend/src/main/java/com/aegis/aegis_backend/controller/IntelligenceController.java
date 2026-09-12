package com.aegis.aegis_backend.controller;

import com.aegis.aegis_backend.client.FacilitatorClient;
import com.aegis.aegis_backend.dto.*;
import com.aegis.aegis_backend.entity.Intelligence;
import com.aegis.aegis_backend.entity.Mission;
import com.aegis.aegis_backend.entity.Payment;
import com.aegis.aegis_backend.service.AiIntegrationService;
import com.aegis.aegis_backend.service.IntelligenceService;
import com.aegis.aegis_backend.service.MissionService;
import com.aegis.aegis_backend.service.PaymentService;
import com.aegis.aegis_backend.util.X402Codec;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/missions/{missionId}/intelligence")
public class IntelligenceController {

    private final IntelligenceService intelligenceService;
    private final AiIntegrationService aiIntegrationService;
    private final MissionService missionService;
    private final PaymentService paymentService;
    private final FacilitatorClient facilitatorClient;
    private final X402Codec x402Codec;

    public IntelligenceController(
            IntelligenceService intelligenceService,
            AiIntegrationService aiIntegrationService,
            MissionService missionService,
            PaymentService paymentService,
            FacilitatorClient facilitatorClient,
            X402Codec x402Codec) {

        this.intelligenceService = intelligenceService;
        this.aiIntegrationService = aiIntegrationService;
        this.missionService = missionService;
        this.paymentService = paymentService;
        this.facilitatorClient = facilitatorClient;
        this.x402Codec = x402Codec;
    }

    // Phase 2: Basic intelligence CRUD

    @PostMapping
    public ResponseEntity<IntelligenceResponse> addIntelligence(
            @PathVariable Long missionId,
            @Valid @RequestBody CreateIntelligenceRequest request) {

        Intelligence intel =
                intelligenceService.addIntelligence(missionId, request);

        return new ResponseEntity<>(
                IntelligenceResponse.fromEntity(intel),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<IntelligenceResponse>> getIntelligenceForMission(
            @PathVariable Long missionId) {

        List<IntelligenceResponse> responses =
                intelligenceService.getIntelligenceForMission(missionId)
                        .stream()
                        .map(IntelligenceResponse::fromEntity)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    // Phase 3: AI integration

    @PostMapping("/requirements")
    public ResponseEntity<AiAnalyzeResponse> determineRequirements(
            @PathVariable Long missionId) {

        return ResponseEntity.ok(
                aiIntegrationService.determineRequiredIntelligence(missionId)
        );
    }

    @PostMapping("/evaluate")
    public ResponseEntity<AiVerifyResponse> evaluateIntelligence(
            @PathVariable Long missionId) {

        return ResponseEntity.ok(
                aiIntegrationService.verifyGatheredIntelligence(missionId)
        );
    }

    // Phase 4: Paid intelligence (HTTP 402)

    @GetMapping("/paid")
    public ResponseEntity<?> getPaidIntelligence(
            @PathVariable Long missionId,
            @RequestHeader(
                    value = "PAYMENT-SIGNATURE",
                    required = false
            ) String paymentSignatureHeader) {

        Mission mission =
                missionService.getMissionById(missionId);

        Map<String, Object> requirements =
                paymentService.buildPaymentRequirements(mission);

        // ---------------------------------------------------------
        // STEP 1: No payment yet -> issue x402 payment challenge
        // ---------------------------------------------------------

        if (paymentSignatureHeader == null) {

            paymentService.getOrCreatePendingPayment(mission);

            String encodedRequirements =
                    x402Codec.encode(
                            Map.of(
                                    "x402Version", 2,
                                    "accepts", java.util.List.of(requirements)
                            )
                    );

            return ResponseEntity
                    .status(HttpStatus.PAYMENT_REQUIRED)
                    .header(
                            "PAYMENT-REQUIRED",
                            encodedRequirements
                    )
                    .body(
                            Map.of(
                                    "x402Version",
                                    2,
                                    "accepts",
                                    java.util.List.of(requirements),
                                    "message",
                                    "Payment required for fresh intelligence on mission "
                                            + missionId
                            )
                    );
        }

        // ---------------------------------------------------------
        // STEP 2: Payment signature received
        // ---------------------------------------------------------

        Payment payment =
                paymentService.getOrCreatePendingPayment(mission);

        Map<String, Object> paymentPayload =
                x402Codec.decode(paymentSignatureHeader);

        // ---------------------------------------------------------
        // STEP 3: Verify payment with facilitator
        // ---------------------------------------------------------

        Map<String, Object> verifyResult =
                facilitatorClient.verify(
                        paymentPayload,
                        requirements
                );

        boolean isValid =
                Boolean.TRUE.equals(verifyResult.get("isValid"))
                        || Boolean.TRUE.equals(verifyResult.get("valid"))
                        || Boolean.TRUE.equals(verifyResult.get("success"));

        if (!isValid) {

            paymentService.markFailed(
                    payment,
                    verifyResult.toString()
            );

            return ResponseEntity
                    .status(HttpStatus.PAYMENT_REQUIRED)
                    .body(
                            Map.of(
                                    "error",
                                    "Payment verification failed",
                                    "details",
                                    verifyResult
                            )
                    );
        }

        // ---------------------------------------------------------
        // STEP 4: Settle payment
        // ---------------------------------------------------------

        Map<String, Object> settleResult =
                facilitatorClient.settle(
                        paymentPayload,
                        requirements
                );

        Object transactionId =
                settleResult.get("transaction");

        boolean settled =
                Boolean.TRUE.equals(settleResult.get("success"))
                        || Boolean.TRUE.equals(settleResult.get("isValid"))
                        || transactionId != null;

        if (!settled) {

            paymentService.markFailed(
                    payment,
                    settleResult.toString()
            );

            return ResponseEntity
                    .status(HttpStatus.PAYMENT_REQUIRED)
                    .body(
                            Map.of(
                                    "error",
                                    "Settlement failed",
                                    "details",
                                    settleResult
                            )
                    );
        }

        // ---------------------------------------------------------
        // STEP 5: Record successful payment
        // ---------------------------------------------------------

        paymentService.markSettled(
                payment,
                String.valueOf(transactionId),
                settleResult.toString()
        );

        paymentService.attemptOnChainVerification(payment);

        // ---------------------------------------------------------
        // STEP 6: Payment succeeded -> request fresh intelligence
        // ---------------------------------------------------------

        AiFreshIntelligenceResponse freshResponse =
                aiIntegrationService.requestFreshIntelligence(missionId);

        // ---------------------------------------------------------
        // STEP 7: Return settlement + intelligence status
        // ---------------------------------------------------------

        String encodedSettlement =
                x402Codec.encode(settleResult);

        return ResponseEntity
                .ok()
                .header(
                        "PAYMENT-RESPONSE",
                        encodedSettlement
                )
                .body(
                        Map.of(
                                "missionId",
                                missionId,
                                "paymentId",
                                payment.getId(),
                                "transactionId",
                                transactionId,
                                "freshIntelligenceStatus",
                                freshResponse.getStatus(),
                                "message",
                                "Payment settled — fresh intelligence requested"
                        )
                );
    }
}