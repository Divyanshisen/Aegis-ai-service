def decide(verification_result: dict) -> dict:
    if not verification_result.get("verified"):
        return {
            "decision": "HOLD_FOR_MORE_DATA",
            "confidence": verification_result.get("confidence", 0.0),
            "reason": "Available intelligence is not yet reliable enough to act on.",
        }

    recommendation = verification_result["recommendation"]
    base_confidence = verification_result["confidence"]
    types_checked = verification_result.get("types_checked", [])

    # Decision-stage confidence gets a small boost once verification has passed —
    # reflects the extra certainty of having cross-checked the data (matches PDF
    # example: verify 0.91 -> decision 0.94ish)
    final_confidence = round(min(base_confidence + 0.03, 1.0), 2)

    if "SHELTER" in types_checked:
        reason = f"{recommendation}: shelter capacity has been verified."
    elif "WEATHER" in types_checked:
        reason = f"{recommendation}: weather conditions have been verified."
    elif "ROAD_STATUS" in types_checked:
        reason = f"{recommendation} has verified road accessibility."
    else:
        reason = f"{recommendation} has been verified."

    return {
        "decision": f"USE_{recommendation.upper().replace(' ', '_')}",
        "confidence": final_confidence,
        "reason": reason,
    }