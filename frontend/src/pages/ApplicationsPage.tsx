import { useEffect, useState } from "react";
import { getApplications } from "../api/applicationApi";
import type { Application } from "../types/Application";

export default function ApplicationsPage() {
    const [applications, setApplications] = useState<Application[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        getApplications()
            .then((page) => setApplications(page.content))
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false));
    }, []);

    if (loading) return <p>Loading applications...</p>;
    if (error) return <p>Error: {error}</p>;
    if (applications.length === 0) return <p>No applications found.</p>;

    return (
        <div>
            <h2>Applications</h2>
            <ul>
                {applications.map((app) => (
                    <li key={app.url}>
                        <a href={app.url}>{app.title}</a>
                        <span> — {app.status}</span>
                    </li>
                ))}
            </ul>
        </div>
    );
}