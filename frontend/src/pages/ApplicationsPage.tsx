import { useState } from "react";
import { deleteApplication, getApplications } from "../api/applicationApi";
import { getAllCompanies } from "../api/companiesApi";
import { useNavigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";

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
    if (isApplicationsPending || isCompaniesPending || deleting) return <p>Loading applications...</p>;

    const errors = [
        applicationError instanceof Error ? applicationError.message : null,
        companyError instanceof Error ? companyError.message : null,
        deleteError,
    ].filter((e): e is string => Boolean(e));

    // output errors
    if (errors.length > 0) {
        return (
            <div>
                {errors.map((error, idx) => (
                    <p key={idx} style={{ color: "red" }}>{error}</p>
                ))}
            </div>
        );
    }

    // handle empty application list
    if (apps.length === 0) {
        return (
            <div>
                <p>You do not have any applications yet!</p>
                <h2>Applications</h2>
                <button onClick={() => navigate("/create-application")}>Create Application +</button>
            </div>
        );
    }

    return (
        <div>
            <h2>Applications</h2>
            <button onClick={() => navigate("/create-application")}>Create Application +</button>
            <ul>
                {/* Output application and company data */}
                {apps.map((app) => (
                    <li key={app.url}>
                        <span>
                            <a href={app.url}>{app.title}</a> - 
                            {companyData?.[app.companyId]?.name || "Unknown Company"} - 
                            {companyData?.[app.companyId]?.location || "Unknown Location"} - 
                            {app.status} - 
                            <button type="button" onClick={() => handleDeleteApplication(app.id)}>-</button>
                        </span>
                    </li> 
                ))}
            </ul>
        </div>
    );
}