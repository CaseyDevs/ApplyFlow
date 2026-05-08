import { useNavigate } from "react-router-dom";
import styles from "./Auth.module.css";
import { useEffect } from "react";

export default function EmailVerifiedFailurePage() {
    const navigate = useNavigate();

    // redirect to register
    useEffect(() => {
        setTimeout(() => {
            navigate("/register");
        }, 4000);
    }, [navigate]);

    return (
        <div className={styles.container}>
            <div className={styles.card}>
                <div className={styles.header}>
                    <div className="errorIcon" style={{
                        fontSize: "48px",
                        marginBottom: "var(--space-4)",
                        animation: "scaleIn 0.6s ease-out",
                        color: "var(--color-error, #dc2626)"
                    }}>
                        ✕
                    </div>
                    <h1 style={{color: "var(--color-error, #dc2626)"}}>Verification Failed</h1>
                    <p>We could not verify your email address. The link may be invalid or expired.</p>
                </div>

                <div style={{
                    textAlign: "center",
                    marginTop: "var(--space-8)",
                    fontWeight: "bold"
                }}>
                    Redirecting to registration...
                </div>
            </div>

            <style>{`
                @keyframes scaleIn {
                    from {
                        transform: scale(0);
                        opacity: 0;
                    }
                    to {
                        transform: scale(1);
                        opacity: 1;
                    }
                }
            `}</style>
        </div>
    )
}
