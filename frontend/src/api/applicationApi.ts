import type { Application, ApplicationRequest, Page, UpdateApplicationRequest } from "../types/Application";

const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

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

export async function getAllApplications(): Promise<Application[]> {
    const response = await fetch(`${BASE_URL}/api/v1/applications?page=0&size=1000`, {
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

    const page: Page<Application> = await response.json();
    return page.content ?? [];
}

export async function getApplicationById(applicationId: number): Promise<Application> {
    const response = await fetch(`${BASE_URL}/api/v1/applications/${applicationId}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || `Failed to fetch application: ${response.status}`);
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

export async function updateApplication(applicationId: number, request: UpdateApplicationRequest) {
    const response = await fetch(`${BASE_URL}/api/v1/applications/${applicationId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(request)
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || `Failed to update application: ${response.status}`)
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
