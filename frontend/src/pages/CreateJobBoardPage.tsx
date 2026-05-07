import { useState } from "react"
import { createJobBoard } from "../api/jobBoardApi";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import styles from "./Forms.module.css";
import { useTimedError } from "../hooks/useTimedError";

export default function CreateJobBoardPage() {
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const [title, setTitle] = useState<string>("");
    const [description, setDescription] = useState<string>("");
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useTimedError(3000);
    const { user } = useAuth();

    async function handleCreateJobBoard(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault();
        const trimmedTitle = title.trim();
        setError(null);

        if (!trimmedTitle) {
            setError("Please enter a job board name.");
            return;
        }

        try {
            setLoading(true);
            await createJobBoard({ 
                title: trimmedTitle, 
                userId: user?.id ?? null, 
                members: null 
            });
            await queryClient.invalidateQueries({ queryKey: ["job-boards"] }); // refresh cache
            navigate("/job-boards");
        } catch (err: any) {
            setError(err.message || String(err));
        }
        finally {
            setLoading(false);
        }
    }

    return (
        <div className={styles.container}>
            <div className={styles.card}>
                <div className={styles.header}>
                    <h1>Create Job Board</h1>
                    <p>Organize your job search with a new job board</p>
                </div>

                <form className={styles.form} onSubmit={handleCreateJobBoard}>
                    <div className={styles.formGroup}>
                        <label htmlFor="title" className={styles.label}>Job Board Name</label>
                        <input
                            className={styles.input}
                            type="text"
                            id="title"
                            name="title"
                            placeholder="e.g., Tech Companies, Startup Jobs"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            required
                        />
                    </div>

                    <div className={styles.formGroup}>
                        <label htmlFor="description" className={styles.label}>Description (Optional)</label>
                        <textarea
                            className={styles.textarea}
                            id="description"
                            name="description"
                            placeholder="Add notes about this job board..."
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                        />
                    </div>

                    {error && <div className={styles.error}>{error}</div>}

                    <div className={styles.formFooter}>
                        <button 
                            type="button" 
                            className={styles.secondaryButton}
                            onClick={() => navigate("/job-boards")}
                        >
                            Cancel
                        </button>
                        <button type="submit" className={styles.button} disabled={loading}>
                            {loading ? "Creating..." : "Create Job Board"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}