import type { Page } from "../types/Application";
import type { Company } from "../types/Company";

const BASE_URL = "http://localhost:8080";

export async function getAllCompanies(): Promise<Page<Company>> {
    const response = await fetch(`${BASE_URL}/api/v1/companies`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    if (!response.ok) throw new Error("Failed to fetch companies" + `${response.status}`);

    return response.json();
}

export async function createCompany(company: Company): Promise<Company> {
    const response = await fetch(`${BASE_URL}/api/v1/companies`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(company)
    });

    if (!response.ok) throw new Error("Failed to fetch companies" + `${response.status}`);

    return response.json();
}