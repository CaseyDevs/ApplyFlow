import { useState, useRef, useEffect } from "react";
import type { Application } from "../types/Application";
import styles from "./SearchableSelect.module.css";

interface SearchableSelectProps {
    applications: Application[];
    selectedId: number | null;
    onSelect: (applicationId: number) => void;
    loading?: boolean;
}

export default function SearchableSelect({
    applications,
    selectedId,
    onSelect,
    loading = false,
}: SearchableSelectProps) {
    const [isOpen, setIsOpen] = useState(false);
    const [searchTerm, setSearchTerm] = useState("");
    const containerRef = useRef<HTMLDivElement>(null);
    const inputRef = useRef<HTMLInputElement>(null);

    // Filter applications based on search term
    const filteredApplications = applications.filter((app) =>
        app.title.toLowerCase().includes(searchTerm.toLowerCase())
    );

    // Get selected application
    const selectedApplication = applications.find((app) => app.id === selectedId);

    // Close dropdown when clicking outside
    useEffect(() => {
        function handleClickOutside(event: MouseEvent) {
            if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
                setIsOpen(false);
            }
        }

        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    // Focus input when dropdown opens
    useEffect(() => {
        if (isOpen && inputRef.current) {
            inputRef.current.focus();
        }
    }, [isOpen]);

    const handleSelect = (applicationId: number) => {
        onSelect(applicationId);
        setIsOpen(false);
        setSearchTerm("");
    };

    return (
        <div ref={containerRef} className={styles.container}>
            <button
                className={styles.trigger}
                onClick={() => setIsOpen(!isOpen)}
                disabled={loading}
                type="button"
            >
                <span className={styles.selectedText}>
                    {selectedApplication?.title || "-- Select an application --"}
                </span>
                <span className={`${styles.arrow} ${isOpen ? styles.open : ""}`}>▼</span>
            </button>

            {isOpen && (
                <div className={styles.dropdown}>
                    <input
                        ref={inputRef}
                        type="text"
                        className={styles.searchInput}
                        placeholder="Search applications..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />

                    <div className={styles.optionsList}>
                        {filteredApplications.length > 0 ? (
                            filteredApplications.map((app) => (
                                <button
                                    key={app.id}
                                    className={`${styles.option} ${
                                        selectedId === app.id ? styles.selected : ""
                                    }`}
                                    onClick={() => handleSelect(app.id)}
                                    type="button"
                                >
                                    <span className={styles.optionTitle}>{app.title}</span>
                                    <span className={styles.optionStatus}>{app.status}</span>
                                </button>
                            ))
                        ) : (
                            <div className={styles.noResults}>No applications found</div>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}
