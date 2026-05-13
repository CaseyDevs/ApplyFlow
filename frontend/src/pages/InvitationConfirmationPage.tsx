import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { useNavigate, useSearchParams, useParams } from "react-router-dom";
import { acceptJobBoardInvitation, getJobBoardInvitation, rejectJobBoardInvitation } from "../api/jobBoardApi";
import { useTimedError } from "../hooks/useTimedError";
import styles from "./InvitationConfirmationPage.module.css";
import { useAuth } from "../context/AuthContext";
import type { InvitationAction } from "../types/InvitationAction";
import DetailItem from "../components/DetailItem";

export default function InvitationConfirmationPage() {
    const { user } = useAuth();
    const [error, setError] = useTimedError(3000);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);
    const [loadingAction, setLoadingAction] = useState<InvitationAction | null>(null);
    
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

    // validate params and handle errors
    useEffect(() => {
        if (!hasValidParams) {
            navigate("/");
            return;
        }

        if (invitationError) {
            setError("Invalid or expired invitation link");
            const timeout = setTimeout(() => navigate("/"), 3000);
            return () => clearTimeout(timeout);
        }

        if (invitationData && (!user || user.email !== invitationData.inviteeEmail)) {
            setError("Please log in to the account associated with the invitation.");
            const timeout = setTimeout(() => navigate("/"), 3000);
            return () => clearTimeout(timeout);
        }
    }, [hasValidParams, invitationError, invitationData, user, navigate, setError]);

    const handleInvitationAction = async (action: InvitationAction) => {
        if (!hasValidParams) return;
        
        setLoadingAction(action);
        try {
            if (action === 'accept') {
                await acceptJobBoardInvitation(thisJobBoardId as number, invitationToken as string);
                setSuccessMessage("Invitation accepted! Redirecting...");
                setTimeout(() => navigate(`/job-boards/${thisJobBoardId}`), 1500);
            } else {
                await rejectJobBoardInvitation(thisJobBoardId as number, invitationToken as string);
                setSuccessMessage("Invitation rejected. Redirecting...");
                setTimeout(() => navigate("/"), 1500);
            }
            queryClient.invalidateQueries({ queryKey: ["invitation"] });
        } catch (err: any) {
            setError(err.message || `Failed to ${action} invitation`);
            setLoadingAction(null);
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
                    <DetailItem label="Job Board Name" value={invitationData.jobBoardTitle} />
                    <DetailItem label="Invitation Sent From" value={invitationData.inviterName} />
                </div>

                <p className={styles.description}>
                    Would you like to accept or reject this invitation?
                </p>

                <div className={styles.formFooter}>
                    <button
                        className={styles.secondaryButton}
                        onClick={() => handleInvitationAction('reject')}
                        disabled={loadingAction !== null || !!successMessage}
                    >
                        {loadingAction === 'reject' ? "Rejecting..." : "Reject"}
                    </button>
                    <button
                        className={styles.button}
                        onClick={() => handleInvitationAction('accept')}
                        disabled={loadingAction !== null || !!successMessage}
                    >
                        {loadingAction === 'accept' ? "Accepting..." : "Accept Invitation"}
                    </button>
                </div>
            </div>
        </div>
    );
}