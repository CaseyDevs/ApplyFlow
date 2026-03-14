import { useEffect, useState } from "react"
import { addApplicationToJobBoard, addJobBoardMember, deleteJobBoard, getJobBoardById } from "../api/jobBoardApi";
import { useNavigate, useParams } from "react-router-dom";
import type { JobBoardResponse } from "../types/JobBoard";
import type { Application } from "../types/Application";
import { getApplications } from "../api/applicationApi";
import { getAllCompanies } from "../api/companiesApi";

/* TODO: 
    => DISPLAY APPLICATION COMPANIES 
    => REFRESH ON APPLICATION UPDATES
    => PREVENT QUERYING THE SAME APPLICATIONS
    => FIX JOB BOARDS DISPLAYING INCORRECTLY FOR OTHER MEMBERS
*/

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
    const { jobBoardId } = useParams(); // get the job board id from url

    // Fetch job board & users applications
    useEffect(() => {
        if (!jobBoardId) return;
        setLoading(true);
        setError(null);

        Promise.all([getJobBoardById(Number(jobBoardId)), getApplications(), getAllCompanies()])
            .then(([jobBoardRes, applicationsRes, companiesRes]) => {
                setJobBoard(jobBoardRes ?? null);
                const apps = applicationsRes?.content ?? [];
                setApplications(apps);

                const companies: Record<number, { name: string; location: string }> = {};
                (companiesRes?.content ?? []).forEach((company) => {
                    companies[company.id] = {
                        name: company.name,
                        location: company.location ?? "Unknown",
                    };
                });
                setCompanyById(companies);

                if (apps.length > 0) setSelectedApplicationId(apps[0].id);
            })
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

    async function handleAddApplication(jobBoardId: string | undefined) {
        if (!jobBoardId || selectedApplicationId == null) return;
        try {
            setLoading(true);
            await addApplicationToJobBoard(Number(jobBoardId), selectedApplicationId);
            setSelectedApplicationId(null);
            setDisplayApplications(false);
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
            {jobBoard?.applications?.map((app) => {
                const company = companyById[app.companyId];

                return (
                    <div key={app.id}>
                        {app.title} - {company?.name ?? "Unknown Company"} - {app.status} - {app.url}
                    </div>
                );
            })}


            <button onClick={() => setDisplayApplications(!displayApplications)}>+ Application</button>

            {/* Add existing applications */}
            {displayApplications && applications ?
                <div>
                    <label htmlFor="your-applications">Your Applications:</label>
                    <select name="your-applications" id="application-select" value={selectedApplicationId ?? ""} onChange={(e) => setSelectedApplicationId(Number(e.target.value))}>
                        {applications?.map((app) => 
                                <option value={app.id}>{app.title}</option>
                            )}
                    </select>
                    <button onClick={() => handleAddApplication(jobBoardId)}>Add application</button>
                </div>
            : null}

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