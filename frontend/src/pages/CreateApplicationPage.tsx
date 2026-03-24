import { useEffect, useState } from "react"
import { createApplication } from "../api/applicationApi";
import { useNavigate } from "react-router-dom";
import { getAllCompanies, createCompany } from "../api/companiesApi";
import type { JobBoardResponse } from "../types/JobBoard";
import { addApplicationToJobBoard, getJobBoards } from "../api/jobBoardApi";
import { useQuery, useQueryClient } from "@tanstack/react-query";

export default function CreateApplicationPage() {
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const [title, setTitle] = useState<string>("");
    const [url, setUrl] = useState<string>("");
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const [newCompanyName, setNewCompanyName] = useState<string>("");
    const [selectedCompanyId, setSelectedCompanyId] = useState<number | null>(null);
    const [location, setLocation] = useState<string | null>(null);
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
            const application = await createApplication({
                title,
                url,
                companyId: selectedCompanyId,
                interviewId, // will always be null *need to remove in backend*
                status: (e.currentTarget.status.value || "INTERESTED"),
            });

            // add this new application to the chosen job board
            if (selectedJobBoardId) await addApplicationToJobBoard(selectedJobBoardId, application.id);

            // Ensure applications list refetches after create.
            await queryClient.invalidateQueries({ queryKey: ["applications"] });

            navigate("/applications");
        } catch (err: any) {
            setError(err.message);
        }
    }

    return (
        <div>
            {isQueryLoading && <p>Loading form data...</p>}
            {queryError instanceof Error && <p style={{color: "red"}}>{queryError.message}</p>}
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

                {/* Adding new application to members job board  */}
                <div>
                    <span>Would you like to add this application to a job board ?</span>
                    {jobBoards.length > 0 ?
                        <div>
                            <label htmlFor="">Your Job Boards</label>
                            <select
                                value={selectedJobBoardId ?? ""}
                                onChange={(e) => setSelectedJobBoardId(e.target.value ? Number(e.target.value) : null)}
                            >
                                <option value=""></option>
                                {jobBoards.map((jb: JobBoardResponse) => {
                                     return (
                                        <option value={jb.id}>{jb.title}</option>
                                     )
                                })}
                            </select>
                        </div> 
                    : <p>You are not yet a member of any job boards!</p> }
                </div>
                <button type="submit" disabled={loading}>Create Application</button>
            </form>
            {error && <p style={{color: "red"}}>{error}</p>}
        </div>
    );
}