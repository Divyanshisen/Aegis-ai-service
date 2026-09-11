from app.agent import planner, verifier
from app.agent.decision import decide
from app.config.settings import CONFIDENCE_THRESHOLD, MISSION_STORE

def analyze(mission_id: int, emergency_type: str, location: str, request_text: str) -> dict:
    plan_result = planner.plan(emergency_type, location, request_text)
    needs_fresh_data = plan_result["confidence"] < CONFIDENCE_THRESHOLD
    reason = "Available information is outdated or incomplete" if needs_fresh_data else "Available information is sufficient"

    MISSION_STORE[mission_id] = {
        "location": location,
        "emergencyType": emergency_type,
        "requiredIntelligence": plan_result["requiredIntelligence"],
        "lastIntelligence": plan_result["gathered"],
        "lastVerification": None,
    }

    return {
        "requiredIntelligence": plan_result["requiredIntelligence"],
        "confidence": plan_result["confidence"],
        "needsFreshData": needs_fresh_data,
        "reason": reason,
    }

def verify(mission_id: int, intelligence_items: list) -> dict:
    result = verifier.verify([item.model_dump() for item in intelligence_items])
    if mission_id in MISSION_STORE:
        MISSION_STORE[mission_id]["lastVerification"] = result
    else:
        MISSION_STORE[mission_id] = {"lastVerification": result}
    return result

def decision(mission_id: int) -> dict:
    mission = MISSION_STORE.get(mission_id)
    if not mission or not mission.get("lastVerification"):
        return {
            "decision": "NO_VERIFIED_DATA",
            "confidence": 0.0,
            "reason": "No verification has been run for this mission yet.",
        }
    return decide(mission["lastVerification"])

def request_fresh_intelligence(mission_id: int) -> dict:
    # x402 boundary — real payment integration teammate yahan connect karega
    return {"missionId": mission_id, "status": "PENDING_PAYMENT"}