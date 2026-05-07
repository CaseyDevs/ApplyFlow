import { getJobBoards } from "../api/jobBoardApi";
import type { JobBoardResponse } from "../types/JobBoard";
import { useNavigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { useAuth } from "../context/AuthContext";
import styles from "./Pages.module.css";

export default function JobBoardPage() {
    const navigate = useNavigate();
    const { user, isLoggedIn, loading } = useAuth();

    // fetch job board data
    const { data, isPending, error } = useQuery({
        queryKey: ["job-boards", "list", user?.id ?? "anonymous"],
        queryFn: getJobBoards,
        enabled: isLoggedIn && !loading,
    });

    const jobBoards = data?.content ?? [];

    if (isPending) {
        return (
            <div className={styles.container}>
                <div className={styles.loading}>Loading job boards...</div>
            </div>
        );
    }

    if (error instanceof Error) {
        return (
            <div className={styles.container}>
                <div className={styles.error}>{error.message}</div>
            </div>
        );
    }

    if (jobBoards.length === 0 && !isPending) {
        return (
            <div className={styles.container}>
                <div className={styles.header}>
                    <h1>Job Boards</h1>
                </div>
                <div className={styles.emptyState}>
                    <h2>No Job Boards Yet</h2>
                    <p>Create your first job board to start organizing your job search.</p>
                    <button 
                        className={styles.button}
                        onClick={() => navigate("/job-boards/create")}
                    >
                        + Create Job Board
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className={styles.container}>
            <div className={styles.header}>
                <h1>Job Boards</h1>
                <button 
                    className={styles.button}
                    onClick={() => navigate("/job-boards/create")}
                >
                    + Create Job Board
                </button>
            </div>

            <div className={styles.grid}>
                {jobBoards.map((jb: JobBoardResponse) => (
                    <div key={jb.id} className={styles.card}>
                        <h3 className={styles.cardTitle}>{jb.title}</h3>
                        <div className={styles.cardMeta}>Members: {jb.members.length} <br /> Appliactions: {jb.applications.length}</div>
                        <div className={styles.cardFooter}>
                            <button 
                                className={styles.button}
                                onClick={() => navigate(`/job-boards/${jb.id}`)}
                            >
                                View Board
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}