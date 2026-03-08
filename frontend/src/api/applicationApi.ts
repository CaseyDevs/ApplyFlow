import type { Application, ApplicationRequest, Page } from "../types/Application";

const BASE_URL = "http://localhost:8080";

export async function getApplications(page = 0, size = 10): Promise<Page<Application>> {
    const response = await fetch(`${BASE_URL}/api/v1/applications?page=${page}&size=${size}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || `Failed to fetch applications: ${response.status}`);
    }

    return response.json();
}

export async function createApplication(application: ApplicationRequest): Promise<Application> {
    const response = await fetch(`${BASE_URL}/api/v1/applications`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(application)
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || `Failed to create application: ${response.status}`);
    }

    return response.json();
}

export async function deleteApplication(applicationId: number): Promise<void> {
    const response = await fetch(`${BASE_URL}/api/v1/applications/${applicationId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include"
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || `Failed to delete application: ${response.status}`);
    }

    return;
}
