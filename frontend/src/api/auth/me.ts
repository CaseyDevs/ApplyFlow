import type { User } from "../../types/User";

export async function getCurrentUser(): Promise<User | null> {
    const response = await fetch("http://localhost:8080/api/auth/me", {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
    });

    if (response.status === 401 || response.status === 404) {
        return null;
    }

    if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.message ?? `Failed to fetch current user: ${response.status}`);
    }

    return await response.json();
}