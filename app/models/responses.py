from pydantic import BaseModel
from typing import List

class AnalyzeResponse(BaseModel):
    requiredIntelligence: List[str]
    confidence: float
    needsFreshData: bool
    reason: str

class VerifyResponse(BaseModel):
    verified: bool
    confidence: float
    recommendation: str

class DecisionResponse(BaseModel):
    decision: str
    confidence: float
    reason: str