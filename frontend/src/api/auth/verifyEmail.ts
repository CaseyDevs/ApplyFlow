export async function verifyEmail(token: string): Promise<boolean> {
    const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
    const response = await fetch(`${BASE_URL}/api/auth/verify?token=` + token, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ token }),
    });

    return response.ok;
}
