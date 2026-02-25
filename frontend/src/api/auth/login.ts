export async function loginUser(payload: { email: string; password: string }): Promise< boolean > {
    const response = await fetch("http://localhost:8080/api/auth/login", {
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
