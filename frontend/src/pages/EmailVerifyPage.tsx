import { useEffect, useRef } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import styles from "./Auth.module.css";
import { verifyEmail } from "../api/auth/verifyEmail";

export default function EmailVerifyPage() {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const token = searchParams.get("token");
    const verifiedRef = useRef(false);

    useEffect(() => {
        const doVerify = async () => {
            // Prevent verification from running twice (React StrictMode guardrail)
            if (verifiedRef.current) {
                return;
            }
            verifiedRef.current = true;

            if (!token) {
                setTimeout(() => {
                    navigate("/email-verified-failure");
                }, 1500);
                return;
            }

            const success = await verifyEmail(token);
            setTimeout(() => {
                if (success) {
                    navigate("/email-verified-success");
                } else {
                    navigate("/email-verified-failure");
                }
            }, 2000);
        };
        doVerify();
    }, [navigate, token]);

    return (
        <div className={styles.container}>
            <div className={styles.card}>
                <div className={styles.header}>
                    <div style={{
                                fontSize: "48px",
                                marginBottom: "var(--space-4)",
                                animation: "spin 1s linear infinite"
                            }}>
                                ⏳
                    </div>
                    <h1>Verifying Email</h1>
                    <p>Please wait while we verify your email address...</p>
                </div>
            </div>

            <style>{`
                @keyframes spin {
                    from {
                        transform: rotate(0deg);
                    }
                    to {
                        transform: rotate(360deg);
                    }
                }
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