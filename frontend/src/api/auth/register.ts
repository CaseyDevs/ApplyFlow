export async function registerUser(userData: { name: string; email: string; password: string }): Promise<void> {
    const response = await fetch("http://localhost:8080/api/auth/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(userData),
    });

    if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.message ?? `Failed to register user: ${response.status}`);
    }
}
