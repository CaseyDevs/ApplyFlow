import { getJobBoards } from "../api/jobBoardApi";
import type { JobBoardResponse } from "../types/JobBoard";
import { useNavigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { useAuth } from "../context/AuthContext";

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
    
    return (
        <>
            {isPending && <p>Loading...</p>}
            {error instanceof Error && <p style={{ color: "red" }}>{error.message}</p>}

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