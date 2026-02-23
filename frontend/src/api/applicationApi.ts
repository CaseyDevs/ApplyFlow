import type { Application } from "../types/Application";

export function getApplication(): Promise<Application[]> {
    return fetch('/api/v1/applications', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json());
}
