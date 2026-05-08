import { useNavigate } from "react-router-dom";
import styles from "./Auth.module.css";
import { useEffect } from "react";

export default function EmailVerifiedSuccessPage() {
    const navigate = useNavigate();

    // redirect to login
    useEffect(() => {
        setTimeout(() => {
            navigate("/login");
        }, 3000);
    }, [navigate]);

    return (
        <div className={styles.container}>
            <div className={styles.card}>
                <div className={styles.header}>
                    <div className="successIcon" style={{
                        fontSize: "48px",
                        marginBottom: "var(--space-4)",
                        animation: "scaleIn 0.6s ease-out",
                        color: "green"
                    }}>
                        ✓
                    </div>
                    <h1 style={{color: "green"}}>Email Verified!</h1>
                    <p>Your email has been successfully verified. You can now log in to your ApplyFlow account.</p>
                </div>

                <div style={{
                    textAlign: "center",
                    marginTop: "var(--space-8)",
                    fontWeight: "bold"
                }}>
                    Redirecting to login...
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