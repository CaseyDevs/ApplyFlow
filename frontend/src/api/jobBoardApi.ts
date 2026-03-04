import type { JobBoardRequest } from "../types/JobBoard";

const BASE_URL = "http://localhost:8080";

export async function getJobBoards() {
    const response = await fetch(`${BASE_URL}/job-boards`, {
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

export async function createJobBoard(request: JobBoardRequest) {
    const response = await fetch(`${BASE_URL}/job-boards`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(request)
    });

    if (!response.ok) {
        const error = await response.json();
        throw new error(error.message || `Failed to create job board: ${response.status}`)
    }

    return response.json();
}