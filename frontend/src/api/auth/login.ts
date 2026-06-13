export async function loginUser(payload: { email: string; password: string }): Promise< boolean > {
    const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
    const response = await fetch(`${BASE_URL}/api/auth/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(payload),
    });

    if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.message ?? `Failed to login user: ${response.status}`);
    }

    return response.ok;
}
