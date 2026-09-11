from app.config.settings import CONFIDENCE_THRESHOLD


def verify(intelligence_items: list) -> dict:
    if not intelligence_items:
        return {
            "verified": False,
            "confidence": 0.0,
            "recommendation": "NO_DATA",
            "types_checked": [],
        }

    avg_confidence = sum(i["confidence"] for i in intelligence_items) / len(intelligence_items)
    verified = avg_confidence >= CONFIDENCE_THRESHOLD
    types_checked = [i.get("type") for i in intelligence_items]

    if not verified:
        recommendation = "INSUFFICIENT_DATA"
    elif "SHELTER" in types_checked:
        recommendation = "Shelter Capacity Confirmed"
    elif "WEATHER" in types_checked:
        recommendation = "Weather Conditions Verified"
    elif "ROAD_STATUS" in types_checked:
        recommendation = "Route A"
    else:
        recommendation = "Verified"

    return {
        "verified": verified,
        "confidence": round(avg_confidence, 2),
        "recommendation": recommendation,
        "types_checked": types_checked,
    }