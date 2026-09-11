AEGIS — Backend (Spring Boot) Integration Handoff
From: AI/Agent service (Python/FastAPI) — tested & PDF-spec-compliant
To: Backend developer (Spring Boot)
AI service base URL (local dev): `http://localhost:8000`
---
1. Architecture (already agreed, from PDF)
```
Spring Boot  --HTTP-->  Python/FastAPI (AI service)  -->  Structured JSON
     |                                                          |
     +----------------------------------------------------------+
     |
     v
   React (frontend)
```
Spring Boot sends mission context to the AI service, gets back a structured
decision, and forwards it to the frontend. Spring Boot never talks to the
tools (road/weather/shelter) directly — only to the AI service's 4 endpoints.
---
2. Endpoints to call (all POST, all JSON)
2.1 `POST /ai/analyze`
Call this first, when a mission is created/needs analysis.
Request:
```json
{
  "missionId": 1,
  "emergencyType": "FLOOD",
  "location": "Gwalior",
  "request": "Find safest ambulance route"
}
```
Response:
```json
{
  "requiredIntelligence": ["ROAD_STATUS", "WEATHER"],
  "confidence": 0.57,
  "needsFreshData": true,
  "reason": "Available information is outdated or incomplete"
}
```
If `needsFreshData` is `true` → go to section 2.4 (fresh-intelligence /
x402 flow) before proceeding.
If `needsFreshData` is `false` → proceed to `/ai/verify`.
2.2 `POST /ai/verify`
Call once you have intelligence items to verify (from your own data sources,
or after the fresh-intelligence step).
Request:
```json
{
  "missionId": 1,
  "intelligence": [
    {"type": "ROAD_STATUS", "confidence": 0.91}
  ]
}
```
Response:
```json
{
  "verified": true,
  "confidence": 0.91,
  "recommendation": "Route A"
}
```
2.3 `POST /ai/decision`
Call after verify — only needs the mission ID (AI service keeps mission state
internally in memory).
Request:
```json
{"missionId": 1}
```
Response:
```json
{
  "decision": "USE_ROUTE_A",
  "confidence": 0.94,
  "reason": "Route A has verified road accessibility."
}
```
Forward this response to the frontend — this is the final structured
recommendation.
2.4 `POST /ai/request-fresh-intelligence` (x402 boundary)
Call this when `/ai/analyze` returned `needsFreshData: true`.
Request:
```json
{"missionId": 1}
```
Response (current — stub only):
```json
{"missionId": 1, "status": "PENDING_PAYMENT"}
```
⚠️ This is currently a stub. The AI service intentionally does NOT fake a
payment or fabricate fresh data — per the PDF spec: "Never create fake
blockchain transactions or fake payment-success responses."
What the backend/payment side needs to build:
On receiving `PENDING_PAYMENT`, trigger the real x402/GoPlausible/Algorand
payment flow.
Once payment succeeds, fetch fresh intelligence (from whichever real data
source is being paid for).
Feed that fresh intelligence into `/ai/verify` (section 2.2) as normal,
then call `/ai/decision`.
If payment fails or times out, surface a clear "fresh data unavailable"
state to the frontend — do not silently proceed as if verified.
---
3. What Spring Boot needs to implement
[ ] HTTP client to call the AI service (`RestTemplate` or `WebClient`),
base URL configurable via `application.properties`
(e.g. `ai.service.base-url=http://localhost:8000`)
[ ] Call `/ai/analyze` when a mission is created or (re)analyzed
[ ] Branch on `needsFreshData`:
`false` → go straight to `/ai/verify` → `/ai/decision`
`true` → call `/ai/request-fresh-intelligence` → handle real payment flow
→ then `/ai/verify` → `/ai/decision`
[ ] Forward the final `/ai/decision` response to the React frontend
[ ] Error handling: if AI service returns `422` (validation error) or is
unreachable (connection refused/timeout), don't crash — return a
graceful error/fallback state to the frontend instead
[ ] Mission ID consistency: the same `missionId` must be used across
`analyze` → `verify` → `decision` calls, since the AI service tracks
state per mission internally
---
4. Notes
All 4 endpoints are already built, tested, and cross-checked against the
PDF spec on the AI-service side.
Mock data is currently used for road/weather/shelter (clearly labeled
`MOCK_ROAD_PROVIDER` etc. in responses) — swapping these for real data
sources doesn't change the API contract above.
Full API docs with a live "try it out" UI are available at
`http://localhost:8000/docs` whenever the AI service is running.