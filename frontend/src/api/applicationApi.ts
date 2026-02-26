import type { Application, Page } from "../types/Application";

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
        throw new Error(`Failed to fetch applications: ${response.status}`);
    }

    return response.json();
}
