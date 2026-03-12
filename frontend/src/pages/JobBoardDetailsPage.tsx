import { useEffect, useState } from "react"
import { addJobBoardMember, deleteJobBoard, getJobBoardById } from "../api/jobBoardApi";
import type { JobBoardResponse } from "../types/JobBoard";
import { useNavigate, useParams } from "react-router-dom";

export default function JobBoardDetailsPage() {
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>();
    const [jobBoard, setJobBoard] = useState<JobBoardResponse | null>(null);
    const [memberEmail, setMemberEmail] = useState<string>("");
    const [displayInput, setDisplayInput] = useState<boolean>(false);

    const navigate = useNavigate();
    const { jobBoardId } = useParams();

    // Fetch job board
    useEffect(() => {
        if (!jobBoardId) return;
        setLoading(true);
        setError(null);

        getJobBoardById(Number(jobBoardId))
            .then((jobBoard) => setJobBoard(jobBoard ?? null))
            .catch((err) => setError(err?.message ?? "Failed to fetch job board"))
            .finally(() => setLoading(false));

    }, [jobBoardId]);

    async function handleAddJobBoardMember(jobBoardId: string | undefined, userEmail: string) {
        if (!jobBoardId) return;
        try {
            setLoading(true);
            await addJobBoardMember(Number(jobBoardId), userEmail);
            setMemberEmail("");
        } catch (err: any) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    async function handleDeleteJobBoard(jobBoardId: string | undefined) {
        if (!jobBoardId) return;
        try {
            setLoading(true);
            await deleteJobBoard(Number(jobBoardId));
            navigate("/");  // navigate to home page upon deletion
        } catch (err: any) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    return (
        <div>
            {loading && <p>Loading</p>}
            {error ?? <p style={{ color: "red" }}>{error}</p>}

            <h1>{jobBoard?.title}</h1>
            {/* Output applications */}
            {jobBoard?.applications?.map((app) => (
                <div key={app.id}>
                    {app.title} - {app.status} - {app.url}
                </div>
            ))}

            {/* Adding a job board member logic */}
            <button onClick={() => setDisplayInput(!displayInput)}>Add member to job board</button>
            {displayInput ? (
                <div>
                    <label htmlFor="memberEmail">Member Email</label>
                    <input
                        name="memberEmail"
                        type="email"
                        value={memberEmail}
                        onChange={(e) => setMemberEmail(e.target.value)}
                        placeholder="Enter email..."
                        required
                    />
                    <button onClick={() => handleAddJobBoardMember(jobBoardId, memberEmail)}>Send Invite</button>
                </div>
            ) : <p></p>}

            {/* Job board deletion  */}
            <button onClick={() => handleDeleteJobBoard(jobBoardId)}>Delete Job Board</button>
        </div>
    )
}