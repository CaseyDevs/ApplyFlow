import { useEffect, useState } from "react";
import type { UpdateApplicationRequest } from "../types/Application";
import { createCompany, getAllCompanies } from "../api/companiesApi";
import { getApplicationById, updateApplication } from "../api/applicationApi";
import { useNavigate, useParams } from "react-router-dom";
import type { CompanyResponse } from "../types/Company";
import type { ApplicationStatus } from "../types/ApplicationStatus";
import styles from "./Forms.module.css";

export default function UpdateApplicationPage() {
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const [updatedTitle, setUpdatedTitle] = useState<string | null>(null);
    const [updatedUrl, setUpdatedUrl] = useState<string | null>(null);
    const [updatedStatus, setUpdatesStatus] = useState<ApplicationStatus | null>(null);
    const [companies, setCompanies] = useState<CompanyResponse[]>([]);
    const [selectedCompanyId, setSelectedCompanyId] = useState<number | null>(null);
    const [newCompanyName, setNewCompanyName] = useState<string>("");
    const [location, setLocation] = useState<string | null>(null);
    const [showNewCompanyForm, setShowNewCompanyForm] = useState(false);

    const navigate = useNavigate();

    // get application id from url
    const { applicationId: applicationIdParam } = useParams();
    const applicationId = applicationIdParam ? Number(applicationIdParam) : null;
    const hasValidApplicationId = applicationId !== null && Number.isFinite(applicationId);

    useEffect(() => {
        if (!hasValidApplicationId) return;
        setLoading(true);
        setError(null);

        Promise.all([getAllCompanies(), getApplicationById(applicationId)])
            .then(([companyRes, applicationRes]) => {
                // update all necessary state
                setCompanies(companyRes.content);
                setUpdatedTitle(applicationRes.title);
                setUpdatedUrl(applicationRes.url);
                setUpdatesStatus(applicationRes.status);
                setSelectedCompanyId(applicationRes.companyId);
            })
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false))
    }, []);


    async function handleAddCompany() {
            if (!newCompanyName) return;
            
            setLoading(true);
            setError(null);
    
            try {
                // add company to db and refresh company list
                const newCompany = await createCompany({ name: newCompanyName, location: location, rating: null });
                const page = await getAllCompanies();
    
                setCompanies(page.content); 
                setSelectedCompanyId(newCompany.id);
                setNewCompanyName("");
                setLocation(null);
                setShowNewCompanyForm(false);
            } catch (err: any) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        }


    async function handleSubmit(e: React.SubmitEvent<HTMLFormElement>) {
        e.preventDefault();

        // updated application object with new fields
        const updatedApplication: UpdateApplicationRequest = {
            title: updatedTitle,
            url: updatedUrl,
            companyId: selectedCompanyId,
            status: updatedStatus
        };

        // check id is valid
        if (!hasValidApplicationId) {
            setError("Invalid application id");
            return;
        }

        try {
            setLoading(true);
            await updateApplication(applicationId, updatedApplication);
            navigate("/applications");
        } catch (err: any){
            setError(err.message || "Failed to update application");
        } finally {
            setLoading(false);
        }
    }

    if (loading && !updatedTitle) {
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

                {error && <div className={styles.error}>{error}</div>}

                <form className={styles.form} onSubmit={handleSubmit}>
                    <div className={styles.formGroup}>
                        <label htmlFor="title" className={styles.label}>Job Title</label>
                        <input
                            className={styles.input}
                            type="text"
                            id="title"
                            name="title"
                            value={updatedTitle ?? ''}
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
                            value={updatedUrl ?? ''}
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
                                disabled={loading || !newCompanyName}
                            >
                                {loading ? "Adding..." : "Add Company"}
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
                            {loading ? "Updating..." : "Update Application"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}