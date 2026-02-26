import type { User } from "../../types/User";

export async function getCurrentUser(): Promise<User | null> {
    const response = await fetch("http://localhost:8080/api/auth/me", {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
    });

    return await response.json(); // throws user not found excpetion if not logged in
}