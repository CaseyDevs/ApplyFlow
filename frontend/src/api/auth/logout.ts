export async function logoutUser(): Promise<void> {
    const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
    const response = await fetch(`${BASE_URL}/api/auth/logout`, {
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