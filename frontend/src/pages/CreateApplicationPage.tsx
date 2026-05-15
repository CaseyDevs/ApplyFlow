import { useEffect, useState } from "react"
import { createApplication } from "../api/applicationApi";
import { useNavigate } from "react-router-dom";
import { getAllCompanies, createCompany } from "../api/companiesApi";
import type { JobBoardResponse } from "../types/JobBoard";
import type { ApplicationStatus } from "../types/ApplicationStatus";
import { addApplicationToJobBoard, getJobBoards } from "../api/jobBoardApi";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import styles from "./Forms.module.css";
import { useTimedError } from "../hooks/useTimedError";
import SearchableSelect from "../components/SearchableSelect";

export default function CreateApplicationPage() {
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const [title, setTitle] = useState<string>("");
    const [url, setUrl] = useState<string>("");
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useTimedError(3000);
    const [newCompanyName, setNewCompanyName] = useState<string>("");
    const [selectedCompanyId, setSelectedCompanyId] = useState<number | null>(null);
    const [location, setLocation] = useState<string | null>(null);
    const [status, setStatus] = useState<ApplicationStatus>("INTERESTED");
    const [showNewCompanyForm, setShowNewCompanyForm] = useState(false);
    const interviewId = null;
    const [selectedJobBoardId, setSelectedJobBoardId] = useState<number | null>(null);

    // fetch company data
    const { 
        data: companyData, 
        isPending: isCompaniesPending,
        error: companyError,
        refetch: refetchCompanies
    } = useQuery({
        queryKey: ["companies"],
        queryFn: getAllCompanies,
    });

    // fetch job board data
    const { 
        data: jobBoardData,
        isPending: isJobBoardsPending,
        error: jobBoardError,
    } = useQuery({
        queryKey: ["job-boards"],
        queryFn: getJobBoards
    });

    const companies = companyData?.content ?? [];
    const jobBoards = jobBoardData?.content ?? [];

    const queryError = companyError ?? jobBoardError;
    const isQueryLoading = isCompaniesPending || isJobBoardsPending;

    // set selected company id when data arrives
    useEffect(() => {
        if (selectedCompanyId === null && companies.length > 0) {
            setSelectedCompanyId(companies[0].id);
        }
    }, [companies, selectedCompanyId]);

    async function handleAddCompany() {
        if (!newCompanyName) return;        
        setLoading(true);
        setError(null);

        try {
            // add company to db and refresh company list
            const newCompany = await createCompany({ name: newCompanyName, location: location, rating: null });
            
            setSelectedCompanyId(newCompany.id);
            setNewCompanyName("");
            setLocation(null);
            setShowNewCompanyForm(false);
            await refetchCompanies();
        } catch (err: any) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }
    
    async function handleSubmit(e: React.SubmitEvent<HTMLFormElement>) {
        e.preventDefault();
        setError(null);

        if (!selectedCompanyId) {
            setError("Please select or add a company.");
            return;
        }

        try {
            setLoading(true);
            const application = await createApplication({
                title,
                url,
                companyId: selectedCompanyId,
                interviewId, // will always be null *need to remove in backend*
                status,
            });

            // add this new application to the chosen job board
            if (selectedJobBoardId) { 
                await addApplicationToJobBoard(selectedJobBoardId, application.id);
                queryClient.invalidateQueries({ queryKey: ["job-board", selectedJobBoardId]}); // refresh cache for chosen job board
            }

            // Ensure applications list refetches after create.
            await queryClient.invalidateQueries({ queryKey: ["applications"] });

            navigate("/applications");
        } catch (err: any) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    if (isQueryLoading) {
        return (
            <div className={styles.container}>
                <div style={{textAlign: 'center'}}>Loading form data...</div>
            </div>
        );
    }

    return (
        <div className={styles.container}>
            <div className={styles.card}>
                <div className={styles.header}>
                    <h1>Create Application</h1>
                    <p>Track your new job application</p>
                </div>

                {queryError instanceof Error && <div className={styles.error}>{queryError.message}</div>}
                {error && <div className={styles.error}>{error}</div>}

                <form className={styles.form} onSubmit={handleSubmit}>
                    <div className={styles.formGroup}>
                        <label htmlFor="title" className={styles.label}>Job Title</label>
                        <input
                            className={styles.input}
                            type="text"
                            id="title"
                            name="title"
                            placeholder="e.g., Senior Software Engineer"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
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
                            placeholder="https://example.com/jobs/123"
                            value={url}
                            onChange={(e) => setUrl(e.target.value)}
                            required
                        />
                    </div>

                    <div className={styles.formGroup}>
                        <label htmlFor="status" className={styles.label}>Application Status</label>
                        <select 
                            className={styles.select} 
                            name="status" 
                            id="status"
                            value={status}
                            onChange={(e) => setStatus(e.target.value as ApplicationStatus)}
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

                    {/* Company Selection */}
                    <div className={styles.formGroup}>
                        <label className={styles.label}>Company</label>
                        {companies.length > 0 ? (
                            <SearchableSelect
                                items={companies}
                                selectedId={selectedCompanyId}
                                onSelect={setSelectedCompanyId}
                                getLabel={(company) => company.name}
                                getSearchFields={(company) => [company.name, company.location || ""]}
                                renderOption={(company) => (
                                    <div style={{ flex: 1 }}>
                                        <div className={styles.optionTitle}>{company.name}</div>
                                        {company.location && (
                                            <div style={{ fontSize: "var(--font-size-xs)", color: "var(--text-tertiary)" }}>
                                                {company.location}
                                            </div>
                                        )}
                                    </div>
                                )}
                                placeholder="Search by name or location..."
                                loading={loading}
                            />
                        ) : (
                            <p style={{color: 'var(--color-error)'}}>No companies found. Please add a company.</p>
                        )}
                    </div>

                    {/* Add New Company */}
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
                                disabled={loading || !newCompanyName}
                            >
                                {loading ? "Adding..." : "Add Company"}
                            </button>
                        </div>
                    )}

                    {/* Job Board Selection */}
                    <div className={styles.formGroup}>
                        <label htmlFor="jobBoard" className={styles.label}>Add to Job Board (Optional)</label>
                        {jobBoards.length > 0 ? (
                            <select
                                className={styles.select}
                                id="jobBoard"
                                value={selectedJobBoardId ?? ""}
                                onChange={(e) => setSelectedJobBoardId(e.target.value ? Number(e.target.value) : null)}
                            >
                                <option value="">-- Select a job board --</option>
                                {jobBoards.map((jb: JobBoardResponse) => (
                                    <option key={jb.id} value={jb.id}>{jb.title}</option>
                                ))}
                            </select>
                        ) : (
                            <p style={{color: 'var(--text-secondary)'}}>You are not yet a member of any job boards. Create one to organize your applications.</p>
                        )}
                    </div>

                    <div className={styles.formFooter}>
                        <button 
                            type="button" 
                            className={styles.secondaryButton}
                            onClick={() => navigate("/applications")}
                        >
                            Cancel
                        </button>
                        <button type="submit" className={styles.button} disabled={loading || !selectedCompanyId}>
                            {loading ? "Creating..." : "Create Application"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}