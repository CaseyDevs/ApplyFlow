import { useEffect, useState } from "react"
import { getJobBoardById } from "../api/jobBoardApi";
import type { JobBoardResponse } from "../types/JobBoard";

export default function JobBoardDetailsPage() {
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>();
    const [jobBoard, setJobBoard] = useState<JobBoardResponse | null>(null);

    const jobBoardId: number = 1;

    // Fetch job board
    useEffect(() => {
        setLoading(true);
        setError(null);

        getJobBoardById(jobBoardId)
            .then((jobBoard) => setJobBoard(jobBoard ?? null))
            .catch((err) => setError(err?.message ?? "Failed to fetch job board"))
            .finally(() => setLoading(false));

    }, []);

    return (
        <div>
            {loading && <p>Loading</p>}
            {error ?? <p style={{color: "red"}}>{error}</p>}

            <h1>{jobBoard?.title}</h1>
                {/* Output applications */}
                {jobBoard?.applications?.map((app) => (
                    <div key={app.id}>
                        {app.title} - {app.status} - {app.url}
                    </div>
                ))}
        </div>
    )
}