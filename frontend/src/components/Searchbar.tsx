import { useState } from "react";
import type { Application } from "../types/Application";
import styles from "./Searchbar.module.css";

interface SearchbarProps {
    applications: Application[];
    onSearchChange: (filteredApplications: Application[]) => void;
    companies?: Record<number, { name: string; }>;
}

export default function Searchbar({ 
    applications,
    onSearchChange,
    companies = {}
}: SearchbarProps) {
    const [searchTerm, setSearchTerm] = useState("");

    // Filter applications based on search term (title and company name)
    const filteredApplications = applications.filter((app) => {
        const titleMatch = app.title.toLowerCase().includes(searchTerm.toLowerCase());
        const companyName = companies[app.companyId]?.name || "";
        const companyMatch = companyName.toLowerCase().includes(searchTerm.toLowerCase());
        const location = app.location || "";
        const locationMatch = location.toLowerCase().includes(searchTerm.toLowerCase());
        const statusMatch = app.status.toLowerCase().includes(searchTerm.toLowerCase());
        return titleMatch || companyMatch || locationMatch || statusMatch;
    });

    const handleSearchChange = (value: string) => {
        setSearchTerm(value);
        onSearchChange(value === "" ? [] : filteredApplications);
    };

    const handleClear = () => {
        setSearchTerm("");
        onSearchChange([]);
    };

    return (
        <div className={styles.searchbarContainer}>
            <input
                type="text"
                className={styles.searchInput}
                placeholder="Search by position, company, location, or status..."
                value={searchTerm}
                onChange={(e) => handleSearchChange(e.target.value)}
            />
            {searchTerm && (
                <div className={styles.rightContent}>
                    <div className={styles.resultCount}>
                        {filteredApplications.length} result{filteredApplications.length !== 1 ? "s" : ""}
                    </div>
                    <button 
                        className={styles.clearButton}
                        onClick={handleClear}
                        aria-label="Clear search"
                        type="button"
                    >
                        ✕
                    </button>
                </div>
            )}
        </div>
    );
}