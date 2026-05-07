import { useState } from "react";
import { loginUser } from "../api/auth/login";
import { useAuth } from "../context/AuthContext";
import { Link, useNavigate } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import styles from "./Auth.module.css";

export default function LoginPage() {
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const { refreshUser } = useAuth();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState(false);

    const handleSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError(null);
        setSuccess(false);

        try {
            await loginUser({ email, password }); // set HttpOnly cookie
            await refreshUser(); // update on client side
            await queryClient.invalidateQueries({ queryKey: ["job-boards"] });
            setSuccess(true);
            navigate("/job-boards");
        } catch (err: any) {
            setError(err.message);
        }
    };

    return (
        <div className={styles.container}>
            <div className={styles.card}>
                <div className={styles.header}>
                    <h1>Welcome Back</h1>
                    <p>Sign in to your ApplyFlow account</p>
                </div>

                <form className={styles.form} onSubmit={handleSubmit}>
                    <div className={styles.formGroup}>
                        <label htmlFor="email" className={styles.label}>Email Address</label>
                        <input
                            className={styles.input}
                            type="email"
                            id="email"
                            name="email"
                            placeholder="you@example.com"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>

                    <div className={styles.formGroup}>
                        <label htmlFor="password" className={styles.label}>Password</label>
                        <input
                            className={styles.input}
                            type="password"
                            id="password"
                            name="password"
                            placeholder="••••••••"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>

                    {error && <div className={styles.error}>{error}</div>}
                    {success && <div className={styles.success}>Login successful!</div>}

                    <button type="submit" className={styles.button}>
                        Sign In
                    </button>
                </form>

                <div className={styles.footer}>
                    Don't have an account? <Link to="/register">Sign up here</Link>
                </div>
            </div>
        </div>
    );
}
