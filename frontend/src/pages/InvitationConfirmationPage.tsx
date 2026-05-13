import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { useNavigate, useSearchParams, useParams } from "react-router-dom";
import { acceptJobBoardInvitation, getJobBoardInvitation, rejectJobBoardInvitation } from "../api/jobBoardApi";
import { useTimedError } from "../hooks/useTimedError";
import styles from "./InvitationConfirmationPage.module.css";
import { useAuth } from "../context/AuthContext";

export default function InvitationConfirmationPage() {
    const { user } = useAuth();
    const [error, setError] = useTimedError(3000);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);
    const [isAccepting, setIsAccepting] = useState(false);
    const [isRejecting, setIsRejecting] = useState(false);
    
    const queryClient = useQueryClient();
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const { jobBoardId: jobBoardIdParam } = useParams();
    
    const invitationToken = searchParams.get('token');
    const thisJobBoardId = jobBoardIdParam ? Number(jobBoardIdParam) : null;
    const hasValidParams = Boolean(
        thisJobBoardId !== null && 
        Number.isFinite(thisJobBoardId) && 
        invitationToken
    );
    
    // fetch invitation details
    const {
        data: invitationData,
        isPending: isPendingInvitation,
        error: invitationError,
    } = useQuery({
        queryKey: ["invitation", invitationToken, thisJobBoardId],
        queryFn: () => getJobBoardInvitation(thisJobBoardId as number, invitationToken as string),
        enabled: hasValidParams,
    });

    // redirect if invalid params
    useEffect(() => {
        if (!hasValidParams) {
            navigate("/");
        }
    }, [hasValidParams, navigate]);

    // redirect if not logged in to recieving account
    useEffect(() => {
        if (invitationData && (!user || user.email !== invitationData.inviteeEmail)) {
            setError("Please log in to the account associated with the invitation.");
            const timeout = setTimeout(() => navigate("/"), 3000);
            return () => clearTimeout(timeout);
        }
    }, [navigate, user, invitationData, setError]);

    // redirect on error
    useEffect(() => {
        if (invitationError) {
            setError("Invalid or expired invitation link");
            const timeout = setTimeout(() => navigate("/"), 3000);
            return () => clearTimeout(timeout);
        }
    }, [invitationError, setError, navigate]);

    // handlers
    const handleAcceptInvitation = async () => {
        if (!hasValidParams) return;
        
        setIsAccepting(true);
        try {
            await acceptJobBoardInvitation(thisJobBoardId as number, invitationToken as string);
            queryClient.invalidateQueries({ queryKey: ["invitation"] });
            setSuccessMessage("Invitation accepted! Redirecting...");
            setTimeout(() => {
                navigate(`/job-boards/${thisJobBoardId}`);
            }, 1500);
        } catch (err: any) {
            setError(err.message || "Failed to accept invitation");
            setIsAccepting(false);
        }
    };

    const handleRejectInvitation = async () => {
        if (!hasValidParams) return;
        
        setIsRejecting(true);
        try {
            await rejectJobBoardInvitation(thisJobBoardId as number, invitationToken as string);
            queryClient.invalidateQueries({ queryKey: ["invitation"] });
            setSuccessMessage("Invitation rejected. Redirecting...");
            setTimeout(() => {
                navigate("/");
            }, 1500);
        } catch (err: any) {
            setError(err.message || "Failed to reject invitation");
            setIsRejecting(false);
        }
    };

    if (isPendingInvitation) {
        return (
            <div className={styles.container}>
                <div className={styles.card}>
                    <div className={styles.header}>
                        <div className={styles.icon}>
                            ⏳
                        </div>
                        <h1>Loading Invitation</h1>
                        <p>Please wait while we fetch your invitation details...</p>
                    </div>
                </div>
            </div>
        );
    }

    if (!invitationData) {
        return null;
    }

    return (
        <div className={styles.container}>
            <div className={styles.card}>
                <div className={styles.header}>
                    <div className={styles.icon}>
                        📧
                    </div>
                    <h1>Job Board Invitation</h1>
                    <p>You've been invited to join a job board</p>
                </div>

                {error && (
                    <div className={styles.error}>
                        {error}
                    </div>
                )}

                {successMessage && (
                    <div className={styles.success}>
                        {successMessage}
                    </div>
                )}

                <div className={styles.invitationDetails}>
                    <div className={styles.detailSection}>
                        <p className={styles.detailLabel}>
                            Job Board Name
                        </p>
                        <p className={styles.detailValue}>
                            {invitationData.jobBoardTitle}
                        </p>
                    </div>

                    <div className={styles.detailSection}>
                        <p className={styles.detailLabel}>
                            Invitation Sent From
                        </p>
                        <p className={styles.detailValueEmail}>
                            {invitationData.inviterName}
                        </p>
                    </div>
                </div>

                <p className={styles.description}>
                    Would you like to accept or reject this invitation?
                </p>

                <div className={styles.formFooter}>
                    <button
                        className={styles.secondaryButton}
                        onClick={handleRejectInvitation}
                        disabled={isRejecting || isAccepting || !!successMessage}
                    >
                        {isRejecting ? "Rejecting..." : "Reject"}
                    </button>
                    <button
                        className={styles.button}
                        onClick={handleAcceptInvitation}
                        disabled={isAccepting || isRejecting || !!successMessage}
                    >
                        {isAccepting ? "Accepting..." : "Accept Invitation"}
                    </button>
                </div>
            </div>
        </div>
    );
}