import type { Invitation } from "../types/Invitation";
import type { JobBoardRequest, JobBoardResponse } from "../types/JobBoard";
import type { Page } from "../types/Application";
import type { JobBoardApplicationRequest, JobBoardApplicationResponse } from "../types/JobBoardApplication";

const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
const API_BASE = `${BASE_URL}/api/v1`;

const DEFAULT_HEADERS = {
    "Content-Type": "application/json"
};

const DEFAULT_OPTIONS = {
    credentials: "include" as const,
    headers: DEFAULT_HEADERS
};

// Helper function to handle API responses consistently
async function apiRequest<T>(
    url: string,
    options: RequestInit = {},
    errorMessage: string = "API request failed"
): Promise<T> {
    const response = await fetch(url, {
        ...DEFAULT_OPTIONS,
        ...options,
        headers: {
            ...DEFAULT_HEADERS,
            ...(options.headers as Record<string, string> | undefined)
        }
    });

    if (!response.ok) {
        const rawBody = await response.text();
        let parsedMessage: string | null = null;

        if (rawBody) {
            try {
                const parsed = JSON.parse(rawBody) as { message?: string };
                parsedMessage = parsed.message ?? null;
            } catch {
                parsedMessage = rawBody;
            }
        }

        throw new Error(parsedMessage || `${errorMessage}: ${response.status}`);
    }

    // Handle 204 No Content and other empty responses
    if (response.status === 204 || response.headers.get('content-length') === '0') {
        return undefined as T;
    }

    return response.json();
}

export async function getJobBoards(): Promise<Page<JobBoardResponse>> {
    return apiRequest(`${API_BASE}/job-boards`, { method: "GET" }, "Failed to fetch job boards");
}

export async function getJobBoardById(jobBoardId: number): Promise<JobBoardResponse> {
    return apiRequest(`${API_BASE}/job-boards/${jobBoardId}`, { method: "GET" }, "Failed to fetch job board");
}

export async function createJobBoard(request: JobBoardRequest): Promise<JobBoardResponse> {
    return apiRequest(
        `${API_BASE}/job-boards`,
        { method: "POST", body: JSON.stringify(request) },
        "Failed to create job board"
    );
}

export async function updateJobBoard(request: JobBoardRequest, jobBoardId: number): Promise<JobBoardResponse> {
    return apiRequest(
        `${API_BASE}/job-boards/${jobBoardId}`,
        { method: "PUT", body: JSON.stringify(request) },
        "Failed to update job board"
    );
}

export async function deleteJobBoard(jobBoardId: number): Promise<void> {
    await apiRequest(
        `${API_BASE}/job-boards/${jobBoardId}`,
        { method: "DELETE" },
        "Failed to delete job board"
    );
}

export async function addJobBoardMember(jobBoardId: number, userEmail: string): Promise<void> {
    await apiRequest(
        `${API_BASE}/job-boards/${jobBoardId}/members`,
        { method: "POST", body: JSON.stringify({ email: userEmail }) },
        "Failed to add job board member"
    );
}

export async function addApplicationToJobBoard(jobBoardId: number, applicationId: number): Promise<void> {
    await apiRequest(
        `${API_BASE}/job-boards/${jobBoardId}/applications/${applicationId}`,
        { method: "POST" },
        "Failed to add application to job board"
    );
}

export async function removeApplicationFromJobBoard(jobBoardId: number, applicationId: number): Promise<void> {
    await apiRequest(
        `${API_BASE}/job-boards/${jobBoardId}/applications/${applicationId}`,
        { method: "DELETE" },
        "Failed to remove application from job board"
    );
}

export async function updateApplicationStatus(jobBoardId: number, jobBoardApplicationId: number, status: string): Promise<void> {
    await apiRequest(
        `${API_BASE}/job-boards/${jobBoardId}/applications/${jobBoardApplicationId}/status`,
        { method: "PUT", body: JSON.stringify({ status }) },
        "Failed to update application status"
    );
}

export async function leaveJobBoard(jobBoardId: number): Promise<void> {
    await apiRequest(
        `${API_BASE}/job-boards/${jobBoardId}`,
        { method: "POST" },
        "Failed to leave job board"
    );
}

export async function getJobBoardInvitation(jobBoardId: number, token: string): Promise<Invitation> {
    return apiRequest(
        `${API_BASE}/job-boards/${jobBoardId}/invitation?token=${encodeURIComponent(token)}`,
        { method: "GET" },
        "Failed to fetch invitation"
    );
}

export async function acceptJobBoardInvitation(jobBoardId: number, token: string): Promise<void> {
    await apiRequest(
        `${API_BASE}/job-boards/${jobBoardId}/invitation/accept?token=${encodeURIComponent(token)}`,
        { method: "POST" },
        "Failed to accept invitation"
    );
}

export async function rejectJobBoardInvitation(jobBoardId: number, token: string): Promise<void> {
    await apiRequest(
        `${API_BASE}/job-boards/${jobBoardId}/invitation?token=${encodeURIComponent(token)}`,
        { method: "DELETE" },
        "Failed to reject invitation"
    );
}

export async function getAllJobBoardApplications(request: JobBoardApplicationRequest): Promise<JobBoardApplicationResponse> {
    return apiRequest(
        `${API_BASE}/job-boards/${request.jobBoardId}/applications}`,
        { method: "GET" },
        "Failed to fetch job board applications"
    );
}

export async function getJobBoardApplicationById(request: JobBoardApplicationRequest): Promise<JobBoardApplicationResponse> {
    return apiRequest(
        `${API_BASE}/job-boards/${request.jobBoardId}/applications/${request.applicationId}`,
        { method: "GET" },
        "Failed to fetch job board application"
    );
}
