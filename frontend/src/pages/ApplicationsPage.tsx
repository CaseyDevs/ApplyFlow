import { useEffect, useState } from "react";
import { getApplications } from "../api/applicationApi";
import type { Application } from "../types/Application";
import { useNavigate } from "react-router-dom";

export default function ApplicationsPage() {
    const navigate = useNavigate();
    const [applications, setApplications] = useState<Application[]>([]);
    const [loading, setLoading] = useState(true);
    const [errors, setErrors] = useState<string[] | null>(null);

    useEffect(() => {
        getApplications()
            .then((page) => setApplications(page.content))
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

    async function handleCreateApplication() {
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
                        <span> — {app.status}</span>
                    </li>
                ))}
            </ul>
        </div>
    );
}