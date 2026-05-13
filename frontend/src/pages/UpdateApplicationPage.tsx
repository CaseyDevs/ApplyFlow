import { useEffect, useState } from "react";
import type { UpdateApplicationRequest } from "../types/Application";
import { createCompany, getAllCompanies } from "../api/companiesApi";
import { getApplicationById, updateApplication } from "../api/applicationApi";
import { useNavigate, useParams } from "react-router-dom";
import type { CompanyRequest, CompanyResponse } from "../types/Company";
import type { ApplicationStatus } from "../types/ApplicationStatus";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import styles from "./Forms.module.css";

export default function UpdateApplicationPage() {
    const [updatedTitle, setUpdatedTitle] = useState<string>("");
    const [updatedUrl, setUpdatedUrl] = useState<string>("");
    const [updatedStatus, setUpdatesStatus] = useState<ApplicationStatus | null>(null);
    const [selectedCompanyId, setSelectedCompanyId] = useState<number | null>(null);
    const [newCompanyName, setNewCompanyName] = useState<string>("");
    const [location, setLocation] = useState<string | null>(null);
    const [showNewCompanyForm, setShowNewCompanyForm] = useState(false);

    const navigate = useNavigate();
    const queryClient = useQueryClient();

    // get application id from url
    const { applicationId: applicationIdParam } = useParams();
    const applicationId = applicationIdParam ? Number(applicationIdParam) : null;
    const hasValidApplicationId = applicationId !== null && Number.isFinite(applicationId);

    const { jobBoardId: jobBoardIdParam } = useParams(); // get the job board id from url
    const thisJobBoardId = jobBoardIdParam ? Number(jobBoardIdParam) : null;
    const hasValidJobBoardId = thisJobBoardId !== null && Number.isFinite(thisJobBoardId); // ensure job board id is always valid

    // Fetch companies list
    const { data: companiesData, isLoading: companiesLoading, error: companiesError } = useQuery({
        queryKey: ["companies"],
        queryFn: getAllCompanies,
    });

    const companies = companiesData?.content || [];

    // Fetch application data
    const { data: application, isLoading: applicationLoading, error: applicationError } = useQuery({
        queryKey: ["application", applicationId],
        queryFn: () => getApplicationById(applicationId!),
        enabled: hasValidApplicationId,
    });

    // Populate form fields when application data loads
    useEffect(() => {
        if (application) {
            setUpdatedTitle(application.title);
            setUpdatedUrl(application.url);
            setUpdatesStatus(application.status);
            setSelectedCompanyId(application.companyId);
        }
    }, [application]);

    // Mutation for creating a company
    const createCompanyMutation = useMutation({
        mutationFn: (data: { name: string; location: string | null }) =>
            createCompany(data as CompanyRequest),
        onSuccess: () => {
            // Invalidate and refetch companies list
            queryClient.invalidateQueries({ queryKey: ["companies"] });
            setNewCompanyName("");
            setLocation(null);
            setShowNewCompanyForm(false);
        },
    });

    // Mutation for updating application
    const updateApplicationMutation = useMutation({
        mutationFn: (updatedApp: UpdateApplicationRequest) =>
            updateApplication(applicationId!, updatedApp),
        onSuccess: () => {
            // Invalidate related queries
            queryClient.invalidateQueries({ queryKey: ["application", applicationId] });  // for job boards
            queryClient.invalidateQueries({ queryKey: ["applications"] });  // for personal application refresh
            if (hasValidJobBoardId) {
                queryClient.invalidateQueries({ queryKey: ["job-board", thisJobBoardId] });
            }
            hasValidJobBoardId ? navigate(`/job-boards/${thisJobBoardId}`) : navigate(`/applications`);
        },
    });

    async function handleAddCompany() {
        if (!newCompanyName) return;
        createCompanyMutation.mutate({ name: newCompanyName, location });
    }

    async function handleSubmit(e: React.SubmitEvent<HTMLFormElement>) {
        e.preventDefault();

        // updated application object with new fields
        const updatedApplication: UpdateApplicationRequest = {
            title: updatedTitle,
            url: updatedUrl,
            companyId: selectedCompanyId,
            status: updatedStatus,
        };

        // check id is valid
        if (!hasValidApplicationId) {
            return;
        }

        updateApplicationMutation.mutate(updatedApplication);
    }

    const loading =
        applicationLoading ||
        updateApplicationMutation.isPending ||
        createCompanyMutation.isPending;
    const error = applicationError || updateApplicationMutation.error || createCompanyMutation.error;

    if (applicationLoading && !updatedTitle) {
        return (
            <div className={styles.container}>
                <div style={{textAlign: 'center'}}>Loading application...</div>
            </div>
        );
    }

    return (
        <div className={styles.container}>
            <div className={styles.card}>
                <div className={styles.header}>
                    <h1>Update Application</h1>
                    <p>Modify your job application details</p>
                </div>

                {error && (
                    <div className={styles.error}>
                        {error instanceof Error ? error.message : "An error occurred"}
                    </div>
                )}

                <form className={styles.form} onSubmit={handleSubmit}>
                    <div className={styles.formGroup}>
                        <label htmlFor="title" className={styles.label}>Job Title</label>
                        <input
                            className={styles.input}
                            type="text"
                            id="title"
                            name="title"
                            value={updatedTitle}
                            onChange={(e) => setUpdatedTitle(e.target.value)}
                            required
                        />
                    </div>

                    <div className={styles.formGroup}>
                        <label htmlFor="url" className={styles.label}>Job URL</label>
                        <input
                            className={styles.input}
                            type="url"
                            id="url"
                            name="url"
                            value={updatedUrl}
                            onChange={(e) => setUpdatedUrl(e.target.value)}
                            required
                        />
                    </div>

                    <div className={styles.formGroup}>
                        <label htmlFor="status" className={styles.label}>Application Status</label>
                        <select
                            className={styles.select}
                            name="status"
                            id="status"
                            value={updatedStatus ?? ''}
                            onChange={(e) => setUpdatesStatus(e.target.value as ApplicationStatus)}
                        >
                            <option value="INTERESTED">Interested</option>
                            <option value="APPLIED">Applied</option>
                            <option value="INTERVIEWING">Interviewing</option>
                            <option value="OFFER">Offer</option>
                            <option value="REJECTED">Rejected</option>
                            <option value="WITHDRAWN">Withdrawn</option>
                            <option value="ACCEPTED">Accepted</option>
                        </select>
                    </div>

                    <div className={styles.formGroup}>
                        <label htmlFor="company" className={styles.label}>Company</label>
                        {companies && companies.length > 0 ? (
                            <select
                                className={styles.select}
                                name="company"
                                id="company"
                                value={selectedCompanyId ?? ''}
                                onChange={e => setSelectedCompanyId(Number(e.target.value))}
                            >
                                {companies.map(company => (
                                    <option key={company.id} value={company.id}>{company.name}</option>
                                ))}
                            </select>
                        ) : (
                            <p style={{color: 'var(--color-error)'}}>No companies found. Please add a company.</p>
                        )}
                    </div>

                    <button
                        type="button"
                        className={styles.secondaryButton}
                        onClick={() => setShowNewCompanyForm(!showNewCompanyForm)}
                        style={{marginBottom: 'var(--space-4)'}}
                    >
                        {showNewCompanyForm ? "Cancel" : "+ Add New Company"}
                    </button>

                    {showNewCompanyForm && (
                        <div style={{padding: 'var(--space-4)', backgroundColor: 'var(--bg-secondary)', borderRadius: 'var(--radius-md)', marginBottom: 'var(--space-4)'}}>
                            <div className={styles.formGroup}>
                                <label htmlFor="newCompanyName" className={styles.label}>Company Name</label>
                                <input
                                    className={styles.input}
                                    type="text"
                                    id="newCompanyName"
                                    placeholder="e.g., Google, Meta, Microsoft"
                                    value={newCompanyName}
                                    onChange={e => setNewCompanyName(e.target.value)}
                                />
                            </div>
                            <div className={styles.formGroup}>
                                <label htmlFor="location" className={styles.label}>Location (Optional)</label>
                                <input
                                    className={styles.input}
                                    type="text"
                                    id="location"
                                    placeholder="e.g., San Francisco, CA"
                                    value={location ?? ""}
                                    onChange={e => setLocation(e.target.value)}
                                />
                            </div>
                            <button
                                type="button"
                                className={styles.button}
                                onClick={handleAddCompany}
                                disabled={createCompanyMutation.isPending || !newCompanyName}
                            >
                                {createCompanyMutation.isPending ? "Adding..." : "Add Company"}
                            </button>
                        </div>
                    )}

                    <div className={styles.formFooter}>
                        <button 
                            type="button" 
                            className={styles.secondaryButton}
                            onClick={() => navigate("/applications")}
                        >
                            Cancel
                        </button>
                        <button type="submit" className={styles.button} disabled={loading || !selectedCompanyId}>
                            {updateApplicationMutation.isPending ? "Updating..." : "Update Application"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}