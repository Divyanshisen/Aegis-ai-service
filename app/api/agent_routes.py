from fastapi import APIRouter
from app.models.schemas import AnalyzeRequest, VerifyRequest, DecisionRequest
from app.models.responses import AnalyzeResponse, VerifyResponse, DecisionResponse
from app.agent import agent

router = APIRouter(prefix="/ai", tags=["AI Agent"])

@router.post("/analyze", response_model=AnalyzeResponse)
def analyze(req: AnalyzeRequest):
    result = agent.analyze(req.missionId, req.emergencyType, req.location, req.request)
    return AnalyzeResponse(**result)

@router.post("/verify", response_model=VerifyResponse)
def verify(req: VerifyRequest):
    result = agent.verify(req.missionId, req.intelligence)
    return VerifyResponse(**result)

@router.post("/decision", response_model=DecisionResponse)
def decision(req: DecisionRequest):
    result = agent.decision(req.missionId)
    return DecisionResponse(**result)

# x402 boundary — exposes the "request fresh/paid intelligence" step from the
# PDF workflow diagram. Reuses DecisionRequest since it's the same {missionId}
# shape. Real payment integration (x402/GoPlausible/Algorand) is NOT done here —
# this only returns a PENDING_PAYMENT stub, as instructed in the PDF (section 8):
# never fake a payment-success response. The backend/payment teammate wires the
# real flow behind agent.request_fresh_intelligence().
@router.post("/request-fresh-intelligence")
def request_fresh_intelligence(req: DecisionRequest):
    return agent.request_fresh_intelligence(req.missionId)