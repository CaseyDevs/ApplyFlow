import { useEffect, useState } from "react";
import { getApplications } from "../api/applicationApi";
import { getAllCompanies } from "../api/companiesApi";
import type { Application } from "../types/Application";
import { useNavigate } from "react-router-dom";

export default function ApplicationsPage() {
    const navigate = useNavigate();
    const [applications, setApplications] = useState<Application[]>([]);
    const [companyNames, setCompanyNames] = useState<Record<number, string>>({});
    const [loading, setLoading] = useState(false);
    const [errors, setErrors] = useState<string[] | null>(null);

    useEffect(() => {
        setLoading(true);
        Promise.all([getApplications(), getAllCompanies()])
            .then(([appPage, companyPage]) => {
                setApplications(appPage.content);
                
                // Build companyId to name map
                const names: Record<number, string> = {};
                companyPage.content.forEach((company) => {
                    names[company.id] = company.name;
                });
                setCompanyNames(names);
            })
            .catch((err) => setErrors([err.message]))
            .finally(() => setLoading(false));
    }, []);

    if (loading) return <p>Loading applications...</p>;
    if (errors && errors.length > 0) {
        return (
            <div>
                {errors.map((error, idx) => (
                    <p key={idx} style={{ color: "red" }}>{error}</p>
                ))}
            </div>
        );
    }
    if (applications.length === 0) {
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
                {applications.map((app) => (
                    <li key={app.url}>
                        <span><a href={app.url}>{app.title}</a> - {companyNames[app.companyId]} - {app.status}</span>
                    </li>
                ))}
            </ul>
        </div>
    );
}