"""
Standalone test for AEGIS AI Service agent pipeline.
Run this directly with Python — NO server/uvicorn/backend needed.
It imports planner.py, verifier.py, decision.py directly and calls their
pure functions, bypassing FastAPI, routes, and HTTP entirely.

Usage (from your ai-service root folder, venv activated):
    python test_pipeline.py
"""

from app.agent import planner, verifier, decision


def run_scenario(label, emergency_type, location, request_text):
    print("=" * 60)
    print(f"SCENARIO: {label}")
    print(f"  emergency_type = {emergency_type!r}")
    print(f"  location       = {location!r}")
    print(f"  request_text   = {request_text!r}")
    print("-" * 60)

    # STEP 1: Planner
    plan_result = planner.plan(emergency_type, location, request_text)
    print("PLANNER OUTPUT:")
    print(f"  requiredIntelligence = {plan_result['requiredIntelligence']}")
    print(f"  confidence           = {plan_result['confidence']}")
    print(f"  gathered (raw tool data) = {plan_result['gathered']}")

    # STEP 2: Verifier (takes the 'gathered' list straight from planner)
    verify_result = verifier.verify(plan_result["gathered"])
    print("\nVERIFIER OUTPUT:")
    print(f"  verified       = {verify_result['verified']}")
    print(f"  confidence     = {verify_result['confidence']}")
    print(f"  recommendation = {verify_result['recommendation']}")
    print(f"  types_checked  = {verify_result.get('types_checked')}")

    # STEP 3: Decision
    decision_result = decision.decide(verify_result)
    print("\nDECISION OUTPUT:")
    print(f"  decision   = {decision_result['decision']}")
    print(f"  confidence = {decision_result['confidence']}")
    print(f"  reason     = {decision_result['reason']}")
    print("=" * 60 + "\n")

    return plan_result, verify_result, decision_result


def test_verify_decision_against_pdf_example():
    """
    Tests /ai/verify and /ai/decision DIRECTLY using the exact example
    from the AEGIS PDF (section 5 & 6) — bypasses planner/tools entirely,
    so this works even if road_tool/weather_tool/shelter_tool aren't ready.
    """
    print("#" * 60)
    print("PDF SPEC CROSS-CHECK — verify + decision only")
    print("#" * 60)

    # From PDF section 5: POST /ai/verify request
    pdf_intelligence_input = [
        {"type": "ROAD_STATUS", "confidence": 0.91},
    ]

    verify_result = verifier.verify(pdf_intelligence_input)
    print("VERIFY OUTPUT:", verify_result)
    print("PDF EXPECTED:  {'verified': True, 'confidence': 0.91, 'recommendation': 'Route A'}")

    decision_result = decision.decide(verify_result)
    print("\nDECISION OUTPUT:", decision_result)
    print("PDF EXPECTED:   {'decision': 'USE_ROUTE_A', 'confidence': ~0.94, "
          "'reason': 'Route A has verified road accessibility.'}")
    print("(after verifier.py/decision.py fix, decision confidence should now be")
    print(" boosted above verify's 0.91 — closer to PDF's ~0.94 example)")
    print("=" * 60 + "\n")


if __name__ == "__main__":
    # PDF-exact cross-check first (no tool dependency)
    test_verify_decision_against_pdf_example()

    # Test 1: Flood — request_text mentions road+shelter
    run_scenario(
        "FLOOD, asks for road+shelter",
        emergency_type="flood",
        location="Sidhi, Madhya Pradesh",
        request_text="check road and shelter status",
    )

    # PDF section 4 exact example
    run_scenario(
        "PDF EXAMPLE — FLOOD in Gwalior, ambulance route",
        emergency_type="FLOOD",
        location="Gwalior",
        request_text="Find safest ambulance route",
    )

    # Test 2: Earthquake — request_text asks for shelter ONLY
    run_scenario(
        "EARTHQUAKE, asks for shelter only",
        emergency_type="earthquake",
        location="Delhi",
        request_text="check shelter availability only",
    )

    # Test 3: Unknown emergency type -> should fall back to DEFAULT_REQUIREMENTS
    run_scenario(
        "UNKNOWN TYPE (tests default fallback)",
        emergency_type="cyclone",
        location="Chennai",
        request_text="anything",
    )

    # Test 4: Fire — check if SHELTER shows up but not WEATHER (per EMERGENCY_REQUIREMENTS)
    run_scenario(
        "FIRE",
        emergency_type="fire",
        location="Mumbai",
        request_text="need shelter info",
    )