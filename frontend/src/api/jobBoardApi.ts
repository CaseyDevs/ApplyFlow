import type { Invitation } from "../types/Invitation";
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
        throw new Error(error.message || `Failed to fetch job boards: ${response.status}`)
    }

    return response.json();
}

export async function getJobBoardById(jobBoardId: number): Promise<JobBoardResponse> {
    const response = await fetch(`${BASE_URL}/api/v1/job-boards/${jobBoardId}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || `Failed to fetch job board: ${error.status}`)
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

export async function updateJobBoard(request: JobBoardRequest, jobBoardId: number): Promise<JobBoardResponse> {
    const response = await fetch(`${BASE_URL}/api/v1/job-boards/${jobBoardId}`, {
        method: "PUT",
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

export async function deleteJobBoard(jobBoardId: number): Promise<void> {
    const response = await fetch(`${BASE_URL}/api/v1/job-boards/${jobBoardId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || `Failed to delete job board: ${response.status}`)
    }

    return;
}

export async function addJobBoardMember(jobBoardId: number, userEmail: string): Promise<void> {
    const response = await fetch(`${BASE_URL}/api/v1/job-boards/${jobBoardId}/members`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify({ email: userEmail })
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || `Failed to add job board member: ${response.status}`)
    }

    return;
}

export async function addApplicationToJobBoard(jobBoardId: number, applicationId: number): Promise<void> {
    const response = await fetch(`${BASE_URL}/api/v1/job-boards/${jobBoardId}/applications/${applicationId}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || `Failed to add application to job board: ${response.status}`)
    }

    return;
}

export async function removeApplicationFromJobBoard(jobBoardId: number, applicationId: number): Promise<void> {
    const response = await fetch(`${BASE_URL}/api/v1/job-boards/${jobBoardId}/applications/${applicationId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || `Failed to remove application from job board: ${response.status}`)
    }

    return;
}

export async function leaveJobBoard(jobBoardId: number): Promise<void> {
    const response = await fetch (`${BASE_URL}/api/v1/job-boards/${jobBoardId}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || `There was an error when attempting to leave this job board, please try again later: ${response.status}`);
    }
    
    return;
}

export async function getJobBoardInvitation(jobBoardId: number, token: string): Promise<Invitation> {
    const response = await fetch (`${BASE_URL}/api/v1/job-boards/${jobBoardId}/invitation?token=${encodeURIComponent(token)}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || `There was an error gathering your invitation details: ${response.status}`);
    }

    return response.json();
}

export async function acceptJobBoardInvitation(jobBoardId: number, token: string): Promise<void> {
    const response = await fetch (`${BASE_URL}/api/v1/job-boards/${jobBoardId}/invitation/accept?token=${encodeURIComponent(token)}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || `There was an error joining this job board: ${response.status}`);
        }
    
    return;
}

export async function rejectJobBoardInvitation(jobBoardId: number, token: string): Promise<void> {
    const response = await fetch (`${BASE_URL}/api/v1/job-boards/${jobBoardId}/invitation?token=${encodeURIComponent(token)}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

        if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || `There was an error rejecting this invitation: ${response.status}`);
    }
    
    return;
}