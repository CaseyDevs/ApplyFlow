import { useEffect, useState } from "react";
import type { UpdateApplicationRequest } from "../types/Application";
import { createCompany, getAllCompanies } from "../api/companiesApi";
import { getApplicationById, updateApplication } from "../api/applicationApi";
import { useNavigate, useParams } from "react-router-dom";
import type { CompanyResponse } from "../types/Company";
import type { ApplicationStatus } from "../types/ApplicationStatus";

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

    return (
        <div>
            
            {error && <p>{error}</p>}

            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="title">Job Title:</label>
                    <input
                        type="text"
                        id="title"
                        name="title"
                        value={updatedTitle ?? ''}
                        onChange={(e) => setUpdatedTitle(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="url">URL:</label>
                    <input
                        type="url"
                        id="url"
                        name="url"
                        value={updatedUrl ?? ''}
                        onChange={(e) => setUpdatedUrl(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="status">Status:</label>
                    <select
                        name="status"
                        id="status"
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
                <div>
                    <label htmlFor="company">Company:</label>
                    {companies ? (
                        <select
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
                        <div>
                            <p>No companies found!</p>
                        </div>
                    )}
                                
                    <span>Company not on the list ?<button type="button" id="create-company" onClick={handleAddCompany}>Add a company here!</button></span>
                    <input
                        type="text"
                        placeholder="New company name"
                        value={newCompanyName}
                        onChange={e => setNewCompanyName(e.target.value)}
                    />
                    <input
                        type="text"
                        placeholder="Location"
                        value={location ?? ""}
                        onChange={e => setLocation(e.target.value)}
                    />
                </div>

                <button type="submit" disabled={loading}>Update Application</button>
            </form>
            {error && <p style={{color: "red"}}>{error}</p>}
        </div>
    )
}