from fastapi import FastAPI
from app.api.agent_routes import router as agent_router

app = FastAPI(title="AEGIS AI Service")
app.include_router(agent_router)

@app.get("/")
def health_check():
    return {"status": "AI service is running"}