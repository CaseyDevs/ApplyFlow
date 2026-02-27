import { useEffect, useState, type ReactNode } from "react";
import { getApplications } from "../api/applicationApi";
import type { Application } from "../types/Application";
import { useNavigate } from "react-router-dom";
import { getCompanyById } from "../api/companiesApi";

// TODO: DISPLAY COMPANIES / ADD COMPANY

export default function ApplicationsPage() {
    const navigate = useNavigate();
    const [applications, setApplications] = useState<Application[]>([]);
    const [companyNames, setCompanyNames] = useState<string[]>([]);
    const [loading, setLoading] = useState(false);
    const [errors, setErrors] = useState<string[] | null>(null);

    useEffect(() => {
        setLoading(true);
        getApplications()
            .then(async (page) => {
                setApplications(page.content);
                // Fetch company names for all applications
                const names: string[] = [];
                await Promise.all(
                    page.content.map(async (app: Application) => {
                        try {
                            const company = await getCompanyById(app.companyId);
                            names[app.companyId] = company.name;
                        } catch (err: any) {
                            names[app.companyId] = "Unknown Company";
                        }
                    })
                );
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
                <button>Create Application +</button>
            </div>
        );
    }

    function handleCreateApplication() {
        navigate("/create-application");
    }

    return (
        <div>
            <h2>Applications</h2>
            <button onClick={handleCreateApplication}>Create Application +</button>
            <ul>
                {applications.map((app) => (
                    <li key={app.url}>
                        <a href={app.url}>{app.title}</a>
                        <span> — {app.status} - {app.url} - {companyNames[app.companyId] || "Loading..."} </span>
                    </li>
                ))}
            </ul>
        </div>
    );
}