import { getJobBoards } from "../api/jobBoardApi";
import type { JobBoardResponse } from "../types/JobBoard";
import { useNavigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";

export default function JobBoardPage() {
    const navigate = useNavigate();

    // fetch job board data
    const { data, isPending, error } = useQuery({
        queryKey: ["job-boards"],
        queryFn: getJobBoards
    });

    const jobBoards = data?.content ?? [];
    
    return (
        <>
            {isPending && <p>Loading...</p>}
            {error ?? <p>{error}</p>}

            <h1>Your Job Boards</h1>
            {jobBoards.length > 0
                ? jobBoards.map((jb: JobBoardResponse) => 
                    <div key={jb.id}>
                        <button onClick={() => navigate(`/job-boards/${jb.id}`)}>{jb.title}</button>
                    </div>)
                : !isPending && <p>You are not assigned to any job boards!</p>}

            <button onClick={() => navigate("/job-boards/create")}>Create a Job Board</button> 
        </>
    )
}