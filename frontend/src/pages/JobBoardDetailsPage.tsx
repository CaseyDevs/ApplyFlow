import { useState } from "react"
import { useTimedError } from "../hooks/useTimedError";
import { addApplicationToJobBoard, addJobBoardMember, deleteJobBoard, getJobBoardById, leaveJobBoard, removeApplicationFromJobBoard } from "../api/jobBoardApi";
import { useNavigate, useParams } from "react-router-dom";
import type { Application } from "../types/Application";
import { getApplicationById, getApplications } from "../api/applicationApi";
import { getAllCompanies } from "../api/companiesApi";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import styles from "./Details.module.css";

export default function JobBoardDetailsPage() {
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useTimedError(3000);
    const [memberEmail, setMemberEmail] = useState<string>("");
    const [displayInput, setDisplayInput] = useState<boolean>(false);
    const [applications, setApplications] = useState<Application[]>([]);
    const [displayApplications, setDisplayApplications] = useState<boolean>(false);
    const [selectedApplicationId, setSelectedApplicationId] = useState<number | null>(null);

    const queryClient = useQueryClient();
    const navigate = useNavigate();
    const { jobBoardId: jobBoardIdParam } = useParams(); // get the job board id from url
    const thisJobBoardId = jobBoardIdParam ? Number(jobBoardIdParam) : null;
    const hasValidJobBoardId = thisJobBoardId !== null && Number.isFinite(thisJobBoardId); // ensure job board id is always valid

    // fetch job board
    const { 
        data: jobBoard, 
        isPending: isJobBoardPending,
        error: jobBoardError,
        refetch: refetchJobBoardData
    } = useQuery({
        queryKey: ["job-board", thisJobBoardId],
        queryFn: () => getJobBoardById(thisJobBoardId as number),
        enabled: hasValidJobBoardId,
    });

    // fetch companies
    const {
        data: companyData,
        isPending: isCompaniesPending,
        error: companyError,
    } = useQuery({
        queryKey: ["companies"],
        queryFn: getAllCompanies,
        select: (companyPage) => {
            // select and map company data
            const map: Record<number, { name: string; location: string }> = {};
            companyPage.content.forEach((company) => {
                map[company.id] = {
                    name: company.name,
                    location: company.location || "Unknown",
                };
            });

            return map;
        },
    });

    // store query error / loading state
    const queryError = companyError ?? jobBoardError;
    const isQueryLoading = isCompaniesPending || isJobBoardPending;

    async function handleAddJobBoardMember(userEmail: string) {
        if (!hasValidJobBoardId) return;
        try {
            setLoading(true);
            await addJobBoardMember(thisJobBoardId, userEmail);
            setMemberEmail("");
            setDisplayInput(false);
            refetchJobBoardData(); // refresh job board data
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
            queryClient.invalidateQueries({ queryKey: ["job-boards"]});
            navigate("/job-boards");  // navigate to home page upon deletion
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
            setSelectedApplicationId(null);
            setDisplayApplications(false);
            refetchJobBoardData();
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
            refetchJobBoardData();
        } catch (err: any) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    async function handleUpdateJobBoardApplication(applicationId: number) {
        try {
            setLoading(true);
            await getApplicationById(applicationId); // ensure user owns application before navigating to update page
            navigate(`/job-boards/${thisJobBoardId}/applications/${applicationId}`);
        } catch (err: any) {
            setError("You cannot update applications that you do not own!");
        } finally {
            setLoading(false);
        }
    }

    async function handleLeaveJobBoard() {
        try {
            setLoading(true);
            if (thisJobBoardId) await leaveJobBoard(thisJobBoardId);
            queryClient.invalidateQueries({queryKey: ["job-boards"]});
            navigate("/job-boards")
        } catch (err: any) {
            setError(`Failed to leave job board`);
        } finally {
            setLoading(false);
        }
    }

    if (isQueryLoading) {
        return (
            <div className={styles.container}>
                <div className={styles.loading}>Loading job board...</div>
            </div>
        );
    }

    if (queryError instanceof Error) {
        return (
            <div className={styles.container}>
                <div className={styles.error}>{queryError.message}</div>
            </div>
        );
    }

    return (
        <div className={styles.container}>
            {loading && <div className={styles.loading}>Loading...</div>}
            {error && <div className={styles.error}>{error}</div>}

            <div className={styles.header}>
                <h1>{jobBoard?.title}</h1>
                <div className={styles.headerActions}>
                    <button className={styles.button} onClick={() => setDisplayInput(!displayInput)}>
                        + Add Member
                    </button>
                    <button className={styles.dangerButton} onClick={handleLeaveJobBoard}>
                        Leave Board
                    </button>
                    <button className={styles.dangerButton} onClick={handleDeleteJobBoard}>
                        Delete Board
                    </button>
                </div>
            </div>

            {/* Members Section */}
            <div className={styles.section}>
                <div className={styles.sectionTitle}>
                    <h2 style={{margin: 0}}>Members</h2>
                </div>
                <ul className={styles.memberList}>
                    {jobBoard?.members.map((member) => (
                        <li key={member.user.id} className={styles.memberItem}>
                            <span className={styles.memberEmail}>{member.user.email}</span>
                            <span className={styles.memberRole}>{member.role}</span>
                        </li>
                    ))}
                </ul>
            </div>

            {/* Add Member Form */}
            {displayInput && (
                <div className={styles.section}>
                    <h3 className={styles.sectionTitle} style={{marginBottom: 'var(--space-4)'}}>Add Member</h3>
                    <div className={styles.form}>
                        <div className={styles.formGroup}>
                            <label htmlFor="memberEmail" className={styles.label}>Member Email</label>
                            <input
                                className={styles.input}
                                name="memberEmail"
                                id="memberEmail"
                                type="email"
                                value={memberEmail}
                                onChange={(e) => setMemberEmail(e.target.value)}
                                placeholder="user@example.com"
                                required
                            />
                        </div>
                        <div style={{display: 'flex', gap: 'var(--space-3)'}}>
                            <button 
                                className={styles.button}
                                onClick={() => handleAddJobBoardMember(memberEmail)}
                                disabled={loading || !memberEmail}
                            >
                                {loading ? "Sending..." : "Send Invite"}
                            </button>
                            <button 
                                className={styles.secondaryButton}
                                onClick={() => {
                                    setDisplayInput(false);
                                    setMemberEmail("");
                                }}
                            >
                                Cancel
                            </button>
                        </div>
                    </div>
                </div>
            )}


            {/* Add Existing Application */}
            {displayApplications && (
                <div className={styles.section}>
                    <h3 className={styles.sectionTitle} style={{marginBottom: 'var(--space-4)'}}>Add Existing Application</h3>
                    <div className={styles.form}>
                        <div className={styles.formGroup}>
                            <label htmlFor="application-select" className={styles.label}>Your Applications</label>
                            <select 
                                className={styles.select}
                                id="application-select" 
                                value={selectedApplicationId ?? ""} 
                                onChange={(e) => setSelectedApplicationId(Number(e.target.value))}
                            >
                                <option value="">-- Select an application --</option>
                                {applications?.map((app) => (
                                    <option key={app.id} value={app.id}>{app.title}</option>
                                ))}
                            </select>
                        </div>
                        <div style={{display: 'flex', gap: 'var(--space-3)'}}>
                            <button 
                                className={styles.button}
                                onClick={handleAddApplication}
                                disabled={loading || !selectedApplicationId}
                            >
                                {loading ? "Adding..." : "Add Application"}
                            </button>
                            <button 
                                className={styles.secondaryButton}
                                onClick={() => {
                                    setDisplayApplications(false);
                                    setSelectedApplicationId(null);
                                }}
                            >
                                Cancel
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Applications Section */}
            <div className={styles.section}>
                <div className={styles.sectionTitle}>
                    <h2 style={{margin: 0}}>Applications ({jobBoard?.applications?.length || 0})</h2>
                    <button 
                        className={styles.button}
                        onClick={() => {
                            if (!displayApplications && applications.length === 0) {
                                getApplications()
                                    .then((page) => {
                                        const apps = page?.content ?? [];
                                        setApplications(apps);
                                        if (apps.length > 0) setSelectedApplicationId(apps[0].id);
                                    })
                                    .catch((err) => setError(err?.message ?? "Failed to fetch applications"));
                            }
                            setDisplayApplications(!displayApplications);
                        }}
                    >
                        + Add Application
                    </button>
                </div>

                {jobBoard?.applications && jobBoard.applications.length > 0 ? (
                    <div className={styles.applicationList}>
                        {jobBoard.applications.map((app) => {
                            const company = companyData ? companyData[app.companyId] : null;

                            return (
                                <div key={app.id} className={styles.applicationCard}>
                                    <h3 className={styles.applicationTitle}>{app.title}</h3>
                                    <div className={styles.applicationMeta}>
                                        <p style={{marginBottom: 'var(--space-2)', color: 'var(--text-primary)', fontWeight: 500}}>
                                            {company?.name ?? "Unknown Company"}
                                        </p>
                                        <p style={{marginBottom: 0, fontSize: 'var(--font-size-xs)', color: 'var(--text-tertiary)'}}>
                                            {company?.location ?? "Unknown Location"}  ⟟ 
                                        </p>
                                    </div>
                                    <span className={`${styles.applicationStatus} ${styles[`status${app.status.charAt(0).toUpperCase() + app.status.slice(1).toLowerCase()}`]}`}>
                                        {app.status}
                                    </span>
                                    <div className={styles.applicationActions}>
                                        <a 
                                            href={app.url} 
                                            target="_blank" 
                                            rel="noopener noreferrer"
                                            style={{
                                                flex: 1,
                                                padding: 'var(--space-2)',
                                                fontSize: 'var(--font-size-xs)',
                                                textAlign: 'center',
                                                color: 'var(--color-primary)',
                                                textDecoration: 'none',
                                                border: '1px solid var(--color-gray-300)',
                                                borderRadius: 'var(--radius-md)',
                                                cursor: 'pointer',
                                                transition: 'all var(--transition-fast)'
                                            }}
                                        >
                                            View Job
                                        </a>
                                        <button 
                                            className={styles.button}
                                            onClick={() => handleUpdateJobBoardApplication(app.id)}
                                        >
                                            Edit
                                        </button>
                                        <button 
                                            className={styles.dangerButton}
                                            onClick={() => handleRemoveApplication(app.id)}
                                            style={{padding: 'var(--space-2)', fontSize: 'var(--font-size-xs)'}}
                                        >
                                            Remove
                                        </button>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                ) : (
                    <div className={styles.emptyState}>
                        <p>No applications yet. Add one to get started!</p>
                    </div>
                )}
            </div>
        </div>
    )
}