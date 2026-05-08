import { useState } from "react";
import { registerUser } from "../api/auth/register";
import { Link, useNavigate } from "react-router-dom";
import styles from "./Auth.module.css";
import { useTimedError } from "../hooks/useTimedError";

export default function RegisterPage() {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useTimedError(3000);
    const [success, setSuccess] = useState(false);

    const handleSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError(null);
        setSuccess(false);

        try {
            await registerUser({ name, email, password });
            setSuccess(true);
            // Clear form
            setName("");
            setEmail("");
            setPassword("");
        } catch (err: any) {
            setError(err.message);
        }
    };

    return (
        <div className={styles.container}>
            <div className={styles.card}>
                <div className={styles.header}>
                    <h1>Create Account</h1>
                    <p>Join ApplyFlow and start managing your job search</p>
                </div>

                <form className={styles.form} onSubmit={handleSubmit}>
                    <div className={styles.formGroup}>
                        <label htmlFor="name" className={styles.label}>Full Name</label>
                        <input
                            className={styles.input}
                            type="text"
                            id="name"
                            name="name"
                            placeholder="John Doe"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required
                        />
                    </div>

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
                    {success && (
                        <div className={styles.success}>
                            Registration successful! Please verify your email.
                            <p>If you can't see the email, be sure to check the spam folder :)</p>
                        </div>
                    )}

                    <button type="submit" className={styles.button}>
                        Create Account
                    </button>
                </form>

                <div className={styles.footer}>
                    Already have an account? <Link to="/login">Sign in here</Link>
                </div>
            </div>
        </div>
    );
}
