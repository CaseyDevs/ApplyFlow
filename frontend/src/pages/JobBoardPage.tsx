import { useEffect, useState } from "react"
import { addJobBoardMember, deleteJobBoard, getJobBoards } from "../api/jobBoardApi";
import type { JobBoardResponse } from "../types/JobBoard";
import { useNavigate } from "react-router-dom";


export default function JobBoardPage() {
    const navigate = useNavigate();
    const [jobBoards, setJobBoards] = useState<JobBoardResponse[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [errors, setErrors] = useState<string[]>([]);
    const [displayInput, setDisplayInput] = useState<boolean>(false);
    const [memberEmail, setMemberEmail] = useState<string>("");
    
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

    async function handleDeleteJobBoard(jobBoardId: number) {
        try {
            setLoading(true);
            await deleteJobBoard(jobBoardId);

            const jobBoardPage = await getJobBoards();
            setJobBoards(jobBoardPage.content);
        } catch (err: any) {
            setErrors([err.message]);
        } finally {
            setLoading(false);
        }
    }

    async function handleAddJobBoardMember(jobBoardId: number, userEmail: string) {        
        try {
            setLoading(true);
            await addJobBoardMember(jobBoardId, userEmail)
            setMemberEmail("");
        } catch (err: any) {
            setErrors([err.message])
        } finally {
            setLoading(false);
        }
    }
    
    return (
        <>
            {loading && <p>Loading...</p>}
            {errors.length > 0 && errors.map((err, index) => <p key={index}>{err}</p>)}

            <h1>Your Job Boards</h1>
            {jobBoards.length > 0
                ? jobBoards.map((jb) => 
                    <div key={jb.id}>
                        <button onClick={() => navigate(`/job-board/${jb.id}`)}>{jb.title}</button>
                        {jb.applications.map((app) => app.title)}
                        <button onClick={() => handleDeleteJobBoard(jb.id)}>Delete Job Board</button>
                        <button onClick={() => setDisplayInput(!displayInput)}>Add member to job board</button>
                       
                        {displayInput == true ?  
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
                                <button onClick={() => handleAddJobBoardMember(jb.id, memberEmail)}>Send Invite</button>
                            </div>
                       : <p></p>
                       }
                        {jb.applications.map((app) => app.title)}
                    </div>)
                : !loading && <p>You are not assigned to any job boards!</p>}

            <button onClick={() => navigate("/create-job-board")}>Create a Job Board</button> 
        </>
    )
}