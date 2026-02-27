import { useEffect, useState } from "react"
import { createApplication } from "../api/applicationApi";
import { useNavigate } from "react-router-dom";
import { getAllCompanies, createCompany } from "../api/companiesApi";
import type { CompanyResponse } from "../types/Company";

export default function CreateApplicationPage() {
    const navigate = useNavigate();
    const [companies, setCompanies] = useState<CompanyResponse[]>([]);
    const [title, setTitle] = useState<string>("");
    const [url, setUrl] = useState<string>("");
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const [newCompanyName, setNewCompanyName] = useState<string>("");
    const [selectedCompanyId, setSelectedCompanyId] = useState<number | null>(null);
    const [location, setLocation] = useState<string | null>(null);
    const interviewId = null;


    // load companies on mount
    useEffect(() => {
        setLoading(true);
        getAllCompanies()
            .then((page) => {
                setCompanies(page.content);
                if (page.content.length > 0) {
                    setSelectedCompanyId(page.content[0].id); // set the curr id to first compnay
                }
            })
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false));
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
        setError(null);

        if (!selectedCompanyId) {
            setError("Please select or add a company.");
            return;
        }

        try {
            await createApplication({
                title,
                url,
                companyId: selectedCompanyId,
                interviewId, // will always be null *need to remove in backend*
                status: (e.currentTarget.status.value || "INTERESTED"),
            });
            navigate("/applications");
        } catch (err: any) {
            setError(err.message);
        }
    }

    return (
        <div>
            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="title">Job Title:</label>
                    <input
                        type="text"
                        id="title"
                        name="title"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="url">URL:</label>
                    <input
                        type="url"
                        id="url"
                        name="url"
                        value={url}
                        onChange={(e) => setUrl(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="status">Status:</label>
                    <select name="status" id="status">
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
                    {companies.length > 0 ? (
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
                <button type="submit" disabled={loading}>Create Application</button>
            </form>
            {error && <p style={{color: "red"}}>{error}</p>}
        </div>
    );
}