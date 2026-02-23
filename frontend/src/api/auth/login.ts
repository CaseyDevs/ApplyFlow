export async function loginUser(credentials: { email: string; password: string }): Promise<{ token: string }> {
    const response = await fetch("http://localhost:8080/api/v1/auth/token", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(credentials),
    });

    if (!response.ok) {
        throw new Error(`Failed to login user: ${response.status}`);
    }

    return response.json();
}
