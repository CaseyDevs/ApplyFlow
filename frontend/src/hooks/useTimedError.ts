import { useState, useEffect } from "react";

export function useTimedError(duration = 3000) {
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (error) {
            const timer = setTimeout(() => setError(null), duration);
            return () => clearTimeout(timer);
        }
    }, [error, duration]);

    return [error, setError] as const;
}