from app.tools import road_tool, weather_tool, shelter_tool

EMERGENCY_REQUIREMENTS = {
    "FLOOD": ["ROAD_STATUS", "WEATHER"],
    "FIRE": ["ROAD_STATUS", "SHELTER"],
    "EARTHQUAKE": ["ROAD_STATUS", "SHELTER", "WEATHER"],
}
DEFAULT_REQUIREMENTS = ["ROAD_STATUS"]

def plan(emergency_type: str, location: str, request_text: str) -> dict:
    required = EMERGENCY_REQUIREMENTS.get(emergency_type.upper(), DEFAULT_REQUIREMENTS)

    gathered = []
    for intel_type in required:
        if intel_type == "ROAD_STATUS":
            gathered.append(road_tool.get_road_status(location))
        elif intel_type == "WEATHER":
            gathered.append(weather_tool.get_weather(location))
        elif intel_type == "SHELTER":
            gathered.append(shelter_tool.get_shelter_capacity(location))

    avg_confidence = sum(i["confidence"] for i in gathered) / len(gathered) if gathered else 0.0

    return {
        "requiredIntelligence": required,
        "gathered": gathered,
        "confidence": round(avg_confidence, 2),
    }