import { useEffect, useState } from "react"
import { getJobBoards } from "../api/jobBoardApi";
import type { JobBoardResponse } from "../types/JobBoard";
import { useNavigate } from "react-router-dom";


export default function JobBoardPage() {
    const navigate = useNavigate();
    const [jobBoards, setJobBoards] = useState<JobBoardResponse[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [errors, setErrors] = useState<string[]>([]);
    
    useEffect(() => {
        setLoading(true);

        Promise.all([getJobBoards()])
            .then(([jobBoard]) => {
                setJobBoards(jobBoard.content);
            })
            .catch((err: any) => setErrors([err.messge]))
            .finally(() => setLoading(false));
    }, [])
    
    return (
        <>
            {loading ?? <p>Loading...</p>}
            {errors && errors.forEach((err: any) => <p>{err}</p>)}

            <h1>Job Boards</h1>
            {jobBoards.length > 0 ? jobBoards.forEach((jb) => 
                <div>{jb.title}</div>
            ) :
            <p>You are not assigned to any job boards!</p>
            }

            <button onClick={() => navigate("/create-job-board")}>Create Job Board</button> 
        </>
    )
}