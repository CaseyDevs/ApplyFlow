export async function logoutUser(): Promise<void> {
    const response = await fetch("http://localhost:8080/api/auth/logout", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
    });
    
    if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.message ?? `Failed to logout user: ${response.status}`);
    }
}