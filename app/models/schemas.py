from pydantic import BaseModel
from typing import List

class AnalyzeRequest(BaseModel):
    missionId: int
    emergencyType: str
    location: str
    request: str

class IntelligenceItem(BaseModel):
    type: str
    confidence: float

class VerifyRequest(BaseModel):
    missionId: int
    intelligence: List[IntelligenceItem]

class DecisionRequest(BaseModel):
    missionId: int