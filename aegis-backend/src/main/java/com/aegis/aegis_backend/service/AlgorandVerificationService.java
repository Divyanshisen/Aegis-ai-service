package com.aegis.aegis_backend.service;


import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import org.springframework.stereotype.Service;

@Service
public class AlgorandVerificationService {

    private final AlgodClient algodClient;

    public AlgorandVerificationService(AlgodClient algodClient) {
        this.algodClient = algodClient;
    }

    public static class OnChainResult {
        public final boolean confirmed;
        public final Long confirmedRound;

        public OnChainResult(boolean confirmed, Long confirmedRound) {
            this.confirmed = confirmed;
            this.confirmedRound = confirmedRound;
        }
    }

    public OnChainResult verifyTransaction(String txId) {
        try {
            Response<PendingTransactionResponse> response =
                    algodClient.PendingTransactionInformation(txId).execute();

            if (!response.isSuccessful() || response.body() == null) {
                return new OnChainResult(false, null);
            }

            PendingTransactionResponse pending = response.body();
            Long confirmedRound = pending.confirmedRound;

            // confirmedRound > 0 means the transaction is actually in a finalized block
            boolean confirmed = confirmedRound != null && confirmedRound > 0;
            return new OnChainResult(confirmed, confirmed ? confirmedRound : null);

        } catch (Exception e) {
            // Node unreachable, or txId not found (yet) — not a hard failure,
            // just means "can't confirm right now," which the caller can retry.
            return new OnChainResult(false, null);
        }
    }
}