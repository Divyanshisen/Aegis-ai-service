from datetime import datetime

def get_shelter_capacity(location: str) -> dict:
    return {
        "type": "SHELTER",
        "location": location,
        "capacity_available": True,
        "confidence": 0.8,
        "last_updated": datetime.utcnow().isoformat(),
        "source": "MOCK_SHELTER_PROVIDER",
    }