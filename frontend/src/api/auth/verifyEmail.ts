export async function verifyEmail(token: string): Promise<boolean> {
    const response = await fetch("http://localhost:8080/api/auth/verify?token=" + token, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ token }),
    });

    return response.ok;
}
