import { useState } from "react"
import { createJobBoard } from "../api/jobBoardApi";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";

export default function CreateJobBoardPage() {
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const [title, setTitle] = useState<string>("");
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
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
            await createJobBoard({ title: trimmedTitle, userId: user?.id ?? null, members: null });
            await queryClient.invalidateQueries({ queryKey: ["job-boards"] }); // refresh cache
            navigate("/job-boards");
        } catch (err: any) {
            setError(err.message || String(err));
        }
        finally {
            setLoading(false);
        }
    }

    return(
        <div>
            <form onSubmit={(e) => handleCreateJobBoard(e)}>
                <div>
                    <label htmlFor="title">Job Board Name:</label>
                    <input
                        type="text"
                        id="title"
                        name="title"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        required
                    />
                </div>
                <button type="submit" disabled={loading}>Create Job Board</button>
            </form>

            {error && <p style={{color: "red"}}>{error}</p>}
        </div>
    )
}