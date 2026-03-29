#!/usr/bin/env python3

import json
import os
import urllib.request
from datetime import datetime
from pathlib import Path


DEADLINES = {
    "refs/heads/ejercicio-01": datetime.fromisoformat("2026-04-06T12:00:00-03:00"),
    "refs/heads/ejercicio-02": datetime.fromisoformat("2026-04-27T12:00:00-03:00"),
    "refs/heads/ejercicio-03": datetime.fromisoformat("2026-05-18T12:00:00-03:00"),
    "refs/heads/ejercicio-04": datetime.fromisoformat("2026-06-08T12:00:00-03:00"),
}


def parse_iso8601(value: str) -> datetime:
    return datetime.fromisoformat(value.replace("Z", "+00:00"))


def fetch_run_created_at() -> datetime:
    repository = os.environ["GITHUB_REPOSITORY"]
    run_id = os.environ["GITHUB_RUN_ID"]
    token = os.environ["GITHUB_TOKEN"]
    url = f"https://api.github.com/repos/{repository}/actions/runs/{run_id}"
    request = urllib.request.Request(
        url,
        headers={
            "Accept": "application/vnd.github+json",
            "Authorization": f"Bearer {token}",
            "X-GitHub-Api-Version": "2022-11-28",
        },
    )

    with urllib.request.urlopen(request) as response:
        payload = json.load(response)

    return parse_iso8601(payload["created_at"])


def main() -> None:
    event_name = os.environ.get("GITHUB_EVENT_NAME", "")
    ref = os.environ.get("GITHUB_REF", "")

    if event_name != "push":
        return

    if ref == "refs/heads/main":
        return

    if ref not in DEADLINES:
        raise SystemExit(f"unexpected ref for exercise workflow: {ref}")

    received_at = fetch_run_created_at()
    deadline = DEADLINES[ref]
    if received_at > deadline:
        raise SystemExit(
            "deadline exceeded: "
            f"push received at {received_at.isoformat()} and deadline is {deadline.isoformat()}"
        )

    event_path = Path(os.environ["GITHUB_EVENT_PATH"])
    payload = json.loads(event_path.read_text())
    if payload.get("ref") != ref:
        raise SystemExit(f"push must target {ref}")


if __name__ == "__main__":
    main()
