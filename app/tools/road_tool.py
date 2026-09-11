from datetime import datetime, timedelta

def get_road_status(location: str) -> dict:
    return {
        "type": "ROAD_STATUS",
        "location": location,
        "status": "PARTIALLY_BLOCKED",
        "confidence": 0.4,
        "last_updated": (datetime.utcnow() - timedelta(hours=6)).isoformat(),
        "source": "MOCK_ROAD_PROVIDER",
    }

def get_fresh_road_status(location: str) -> dict:
    return {
        "type": "ROAD_STATUS",
        "location": location,
        "status": "CLEAR",
        "confidence": 0.91,
        "last_updated": datetime.utcnow().isoformat(),
        "source": "MOCK_ROAD_PROVIDER_FRESH",
    }