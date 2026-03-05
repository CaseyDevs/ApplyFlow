import type { JobBoardRequest, JobBoardResponse } from "../types/JobBoard";

const BASE_URL = "http://localhost:8080";

export async function getJobBoards() {
    const response = await fetch(`${BASE_URL}/api/v1/job-boards`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || `Failed to fetch job-boards: ${response.status}`)
    }

    return response.json();
}

export async function createJobBoard(request: JobBoardRequest): Promise<JobBoardResponse> {
    const response = await fetch(`${BASE_URL}/api/v1/job-boards`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(request)
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || `Failed to create job board: ${response.status}`)
    }

    return response.json();
}