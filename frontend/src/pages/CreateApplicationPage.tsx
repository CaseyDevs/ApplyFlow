import { useState } from "react"
import { createApplication } from "../api/applicationApi";
import { useNavigate } from "react-router-dom";

export default function CreateApplicationPage() {
    const navigate = useNavigate();
    const [title, setTitle] = useState<string>("");
    const [url, setUrl] = useState<string>("");
    const [error, setError] = useState<string | null>(null);
    const companyId = 1;
    const interviewId = null;

    async function handleSubmit(e: React.SubmitEvent<HTMLFormElement>) {
        e.preventDefault();
        setError(null);

        try {
            await createApplication({
                title,
                url,
                companyId,
                interviewId,
                status: (e.currentTarget.status.value || "INTERESTED"),
            });
            navigate("/applications")
        } catch (err: any) {
            setError(err.message);
        } 
    };

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

                <button type="submit">Create Application</button>
            </form>

            {error ?? <p style={{color: "red"}}>{error}</p>}
        </div>
    )
}