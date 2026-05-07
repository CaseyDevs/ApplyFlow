import { useState } from "react";
import { deleteApplication, getApplications } from "../api/applicationApi";
import { getAllCompanies } from "../api/companiesApi";
import { useNavigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import styles from "./Pages.module.css";

export default function ApplicationsPage() {
    const navigate = useNavigate();
    const [deleting, setDeleting] = useState(false);
    const [deleteError, setDeleteError] = useState<string | null>(null);

    // applications query
    const { 
        data: applicationData, 
        isPending: isApplicationsPending, 
        error: applicationError, 
        refetch: refetchApplications 
    } = useQuery({
        queryKey: ['applications'],
        queryFn: () => getApplications(0, 10) // first page, 10 elements
    });

    // company data query
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

    const apps = applicationData?.content ?? []; // keep applications empty if no data passed

    async function handleDeleteApplication(applicationId: number): Promise<void> {
        try {
            setDeleteError(null);
            setDeleting(true);
            await deleteApplication(applicationId);
            
            // Refresh applications
            await refetchApplications();
        } catch (err: any) {
            setDeleteError(err.message ?? "Failed to delete application");
        } finally {
            setDeleting(false);
        }
    }

    // check loading state
    if (isApplicationsPending || isCompaniesPending || deleting) {
        return (
            <div className={styles.container}>
                <div className={styles.loading}>Loading applications...</div>
            </div>
        );
    }

    const errors = [
        applicationError instanceof Error ? applicationError.message : null,
        companyError instanceof Error ? companyError.message : null,
        deleteError,
    ].filter((e): e is string => Boolean(e));

    // output errors
    if (errors.length > 0) {
        return (
            <div className={styles.container}>
                {errors.map((error, idx) => (
                    <div key={idx} className={styles.error}>{error}</div>
                ))}
            </div>
        );
    }

    // handle empty application list
    if (apps.length === 0) {
        return (
            <div className={styles.container}>
                <div className={styles.header}>
                    <h1>Applications</h1>
                </div>
                <div className={styles.emptyState}>
                    <h2>No Applications Yet</h2>
                    <p>Start by creating your first job application to track your progress.</p>
                    <button 
                        className={styles.button}
                        onClick={() => navigate("/create-application")}
                    >
                        + Create Application
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className={styles.container}>
            <div className={styles.header}>
                <h1>Applications</h1>
                <button 
                    className={styles.button}
                    onClick={() => navigate("/create-application")}
                >
                    + Create Application
                </button>
            </div>

            {deleteError && <div className={styles.error}>{deleteError}</div>}

            <table className={styles.table}>
                <thead className={styles.tableHeader}>
                    <tr>
                        <th className={styles.tableHeaderCell}>Position</th>
                        <th className={styles.tableHeaderCell}>Company</th>
                        <th className={styles.tableHeaderCell}>Location</th>
                        <th className={styles.tableHeaderCell}>Status</th>
                        <th className={styles.tableHeaderCell}>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {apps.map((app) => (
                        <tr key={app.id} className={styles.tableRow}>
                            <td className={styles.tableCell}>
                                <a href={app.url} target="_blank" rel="noopener noreferrer" style={{color: 'var(--color-primary)'}}>
                                    {app.title}
                                </a>
                            </td>
                            <td className={styles.tableCell}>
                                {companyData?.[app.companyId]?.name || "Unknown Company"}
                            </td>
                            <td className={styles.tableCell}>
                                {companyData?.[app.companyId]?.location || "Unknown Location"}
                            </td>
                            <td className={styles.tableCell}>
                                <span className={`${styles.statusBadge} ${styles[`status${app.status.charAt(0).toUpperCase() + app.status.slice(1).toLowerCase()}`]}`}>
                                    {app.status}
                                </span>
                            </td>
                            <td className={styles.tableCell}>
                                <div className={styles.actions}>
                                    <button 
                                        className={styles.actionButton}
                                        onClick={() => navigate(`/applications/${app.id}`)}
                                    >
                                        Edit
                                    </button>
                                    <button 
                                        className={`${styles.actionButton} ${styles.deleteButton}`}
                                        onClick={() => handleDeleteApplication(app.id)}
                                        disabled={deleting}
                                    >
                                        Delete
                                    </button>
                                </div>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}