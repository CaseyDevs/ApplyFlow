import { useEffect, useState } from "react"
import { getJobBoards } from "../api/jobBoardApi";
import type { JobBoardResponse } from "../types/JobBoard";


export default function JobBoardPage() {
    const [jobBoards, setJobBoards] = useState<JobBoardResponse[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [errors, setErrors] = useState<string[]>([]);
    
    useEffect(() => {
        setLoading(true);

        Promise.all([getJobBoards()])
            .then(([jobBoard]) => {
                setJobBoards(jobBoard);
            })
            .catch((err: any) => setErrors([err.messge]))
            .finally(() => setLoading(false));
    }, [])
    
    return (
        <>
            {loading ?? <p>Loading...</p>}
            {errors.length ?? errors.forEach((err: any) => <p>{err}</p>)}

            <h1>Job Boards</h1>
            {jobBoards.length > 0 ? jobBoards.forEach((jb) => 
                <div>{jb.title}</div>
            ) :
            <p>You are not assigned to any job boards!</p>
            } 
        </>
    )
}