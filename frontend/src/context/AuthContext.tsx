import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import type { User } from "../types/User";
import { getCurrentUser } from "../api/auth/me";

type AuthContextType = {
    user: User | null;
    loading: boolean;
    refreshUser: () => Promise<void>;
}

// user info will be stored in context
const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState<boolean>(false);

    const refreshUser = async () => {
        setLoading(true);

        try {
            const currentUser = await getCurrentUser();
            setUser(currentUser);
        } catch (err) {
            setUser(null);
        } finally {
            setLoading(false);
        }
    };

    // refresh user on mount
    useEffect(() => {
        refreshUser();
    }, []);

    return (
        <AuthContext.Provider value={{ user, loading, refreshUser }}>
            {children}
        </AuthContext.Provider>
    )
}

export function useAuth() {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error("useAuth must be used withing an AuthProvider");
    
    return ctx;
}