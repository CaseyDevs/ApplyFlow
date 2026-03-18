import { useEffect, useState } from "react"
import { addApplicationToJobBoard, addJobBoardMember, deleteJobBoard, getJobBoardById, leaveJobBoard, removeApplicationFromJobBoard } from "../api/jobBoardApi";
import { useNavigate, useParams } from "react-router-dom";
import type { JobBoardResponse } from "../types/JobBoard";
import type { Application } from "../types/Application";
import { getApplications } from "../api/applicationApi";
import { getAllCompanies } from "../api/companiesApi";

export default function JobBoardDetailsPage() {
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>();
    const [jobBoard, setJobBoard] = useState<JobBoardResponse | null>(null);
    const [memberEmail, setMemberEmail] = useState<string>("");
    const [displayInput, setDisplayInput] = useState<boolean>(false);
    const [applications, setApplications] = useState<Application[]>([]);
    const [companyById, setCompanyById] = useState<Record<number, { name: string; location: string }>>({});
    const [displayApplications, setDisplayApplications] = useState<boolean>(false);
    const [selectedApplicationId, setSelectedApplicationId] = useState<number | null>(null);

    const navigate = useNavigate();
    const { jobBoardId: jobBoardIdParam } = useParams(); // get the job board id from url
    const thisJobBoardId = jobBoardIdParam ? Number(jobBoardIdParam) : null;
    const hasValidJobBoardId = thisJobBoardId !== null && Number.isFinite(thisJobBoardId); // ensure job board id is always valid

    // Fetch job board & users applications
    useEffect(() => {
        if (!hasValidJobBoardId) return;
        setLoading(true);
        setError(null);

        // Fetch current job board and companies
        Promise.all([getJobBoardById(thisJobBoardId), getAllCompanies()])
            .then(([jobBoardRes, companiesRes]) => {
                setJobBoard(jobBoardRes ?? null);

                const companies: Record<number, { name: string; location: string }> = {};
                (companiesRes?.content ?? []).forEach((company) => {
                    companies[company.id] = {
                        name: company.name,
                        location: company.location ?? "Unknown",
                    };
                });
                setCompanyById(companies);
            })
            .catch((err) => setError(err?.message ?? "Failed to fetch job board"))
            .finally(() => setLoading(false));

    }, [thisJobBoardId]);

    async function handleAddJobBoardMember(userEmail: string) {
        if (!hasValidJobBoardId) return;
        try {
            setLoading(true);
            await addJobBoardMember(thisJobBoardId, userEmail);
            setMemberEmail("");
            const updated = await getJobBoardById(thisJobBoardId);
            setJobBoard(updated ?? null);
        } catch (err: any) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    async function handleDeleteJobBoard() {
        if (!hasValidJobBoardId) return;
        try {
            setLoading(true);
            await deleteJobBoard(thisJobBoardId);
            navigate("/");  // navigate to home page upon deletion
        } catch (err: any) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    async function handleAddApplication() {
        if (!hasValidJobBoardId || selectedApplicationId == null) return;
        try {
            setLoading(true);
            const alreadyAdded = jobBoard?.applications?.some((app) => app.id === selectedApplicationId); // check for first match
            
            // prevent application duplicates
            if (alreadyAdded) {
                setError("This application is already in the job board!");
                return;
            }

            await addApplicationToJobBoard(thisJobBoardId, selectedApplicationId);
            const updated = await getJobBoardById(thisJobBoardId);
            setJobBoard(updated ?? null);
            setSelectedApplicationId(null);
            setDisplayApplications(false);
        } catch (err: any) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    async function handleRemoveApplication(applicationId: number) {
        if (!applicationId || !hasValidJobBoardId) return;

        try {
            setLoading(true);
            await removeApplicationFromJobBoard(thisJobBoardId, applicationId);
            const updated = await getJobBoardById(thisJobBoardId);
            setJobBoard(updated ?? null);
        } catch (err: any) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    async function handleLeaveJobBoard() {
        if (!hasValidJobBoardId) return;

        try {
            setLoading(true);
            await leaveJobBoard(thisJobBoardId);
            navigate("/job-boards")
        } catch (err: any) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    return (
        <div>
            {loading && <p>Loading</p>}
            {error && 
            <p style={{ color: "red" }}>{error}</p>
            }

            <h1>{jobBoard?.title}</h1>
            
            <div>
                <h3>Members:</h3>
                {/* Display members */}
                {jobBoard?.members.map((member) => {
                    return (
                            <p>{member.user.email} - {member.role}</p>
                    );
                })}
            </div>

            {/* Output applications */}
            {jobBoard?.applications?.map((app) => {
                const company = companyById[app.companyId];

                return (
                    <div key={app.id}>
                        {app.title} - {company?.name ?? "Unknown Company"} - {app.status} - {app.url}
                        <button onClick={() => navigate(`/job-boards/${thisJobBoardId}/applications/${app.id}`)}>Update Application</button>
                        <button onClick={() => handleRemoveApplication(app.id)}>Remove Application</button>
                    </div>
                );
            })}


            <button onClick={() => {
                // Fetch applications on click if no applications exist in current state
                if (!displayApplications && applications.length === 0) {
                    getApplications()
                        .then((page) => {
                            const apps = page?.content ?? [];
                            setApplications(apps);  // store applications in state (cached)
                            if (apps.length > 0) setSelectedApplicationId(apps[0].id);
                        })
                        .catch((err) => setError(err?.message ?? "Failed to fetch applications"));
                }
                setDisplayApplications(!displayApplications);
            }}>+ Application</button>

            {/* Add existing applications */}
            {displayApplications &&
                <div>
                    <label htmlFor="your-applications">Your Applications:</label>
                    <select name="your-applications" id="application-select" value={selectedApplicationId ?? ""} onChange={(e) => setSelectedApplicationId(Number(e.target.value))}>
                        {applications?.map((app) => 
                                <option value={app.id}>{app.title}</option>
                        )}
                    </select>
                    {/* <button onClick={() => navigate("/create-application")}>Create new applicaiton</button> */}
                    <button onClick={handleAddApplication}>Add application</button>
                </div>
            }

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
                    <button onClick={() => handleAddJobBoardMember(memberEmail)}>Send Invite</button>
                </div>
            ) : <p></p>}

            {/* Leave a job board  */}
            <button onClick={handleLeaveJobBoard}>Leave Job Board</button>

            {/* Job board deletion  */}
            <button onClick={handleDeleteJobBoard}>Delete Job Board</button>
        </div>
    )
}