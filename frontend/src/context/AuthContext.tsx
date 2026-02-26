import { createContext, useContext } from "react";
import type { User } from "../types/User";

type AuthContextType = {
    user: User | null;
    loading: boolean;
    refreshUser: () => Promise<void>;
}

// user info will be stored in context
const AuthContext = createContext<AuthContextType | null>(null);

export function useAuth() {
    const ctx = useContext(AuthContext);
    
    if (!ctx) {
        throw new Error("useAuth must be used withing an AuthProvider");
    }

    return ctx;
}