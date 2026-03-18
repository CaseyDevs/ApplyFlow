import { useEffect, useState } from "react"
import { getJobBoards } from "../api/jobBoardApi";
import type { JobBoardResponse } from "../types/JobBoard";
import { useNavigate } from "react-router-dom";


export default function JobBoardPage() {
    const navigate = useNavigate();
    const [jobBoards, setJobBoards] = useState<JobBoardResponse[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [errors, setErrors] = useState<string[]>([]);

    // Fetch all job boards to display titles    
    useEffect(() => {
        setLoading(true);
        setErrors([]);

        getJobBoards()
            .then((jobBoardPage) => {
                setJobBoards(jobBoardPage.content ?? []);
            })
            .catch((err: any) => setErrors([err?.message ?? "Failed to fetch job boards"]))
            .finally(() => setLoading(false));
    }, [])
    
    return (
        <>
            {loading && <p>Loading...</p>}
            {errors.length > 0 && errors.map((err, index) => <p key={index}>{err}</p>)}

            <h1>Your Job Boards</h1>
            {jobBoards.length > 0
                ? jobBoards.map((jb) => 
                    <div key={jb.id}>
                        <button onClick={() => navigate(`/job-boards/${jb.id}`)}>{jb.title}</button>
                    </div>)
                : !loading && <p>You are not assigned to any job boards!</p>}

            <button onClick={() => navigate("/job-boards/create")}>Create a Job Board</button> 
        </>
    )
}