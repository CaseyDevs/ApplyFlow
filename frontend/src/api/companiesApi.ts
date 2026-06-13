import type { Page } from "../types/Application";
import type { CompanyRequest, CompanyResponse } from "../types/Company";

const BASE_URL = "http://localhost:8080";

export async function getAllCompanies(): Promise<Page<CompanyResponse>> {
    const response = await fetch(`${BASE_URL}/api/v1/companies`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || `Failed to fetch companies: ${response.status}`);
    }

    return response.json();
}

export async function getCompanyById(companyId: number): Promise<CompanyResponse> {
    const response = await fetch(`${BASE_URL}/api/v1/companies/${companyId}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || `Failed to fetch companies: ${response.status}`);
    }

    return response.json();
}

export async function createCompany(company: CompanyRequest): Promise<CompanyResponse> {
    const response = await fetch(`${BASE_URL}/api/v1/companies`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(company)
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || `Failed to create company: ${response.status}`);
    }

    return response.json();
}

export async function updateCompany({ companyId, request }: { companyId: number, request: CompanyRequest}) {
    const response = await fetch(`${BASE_URL}/api/v1/companies/${companyId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(request)
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || `Failed to update company: ${response.status}`);
    }

    return response.json();
}

// admin only
export async function deleteCompany(companyId: number) {
    const response = await fetch(`${BASE_URL}/api/v1/companies/${companyId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || `Failed to delete company: ${response.status}`);
    }

    return response.json();
}