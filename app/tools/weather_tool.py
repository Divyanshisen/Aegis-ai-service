from datetime import datetime

def get_weather(location: str) -> dict:
    return {
        "type": "WEATHER",
        "location": location,
        "condition": "HEAVY_RAIN",
        "confidence": 0.75,
        "last_updated": datetime.utcnow().isoformat(),
        "source": "MOCK_WEATHER_PROVIDER",
    }