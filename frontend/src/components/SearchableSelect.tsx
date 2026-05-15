import { useState, useRef, useEffect } from "react";
import styles from "./SearchableSelect.module.css";

interface SearchableSelectProps<T> {
    items: T[];
    selectedId: number | null;
    onSelect: (id: number) => void;
    getLabel: (item: T) => string;
    getSearchFields: (item: T) => string[];
    renderOption?: (item: T) => React.ReactNode;
    placeholder?: string;
    loading?: boolean;
}

export default function SearchableSelect<T extends { id: number }>({
    items,
    selectedId,
    onSelect,
    getLabel,
    getSearchFields,
    renderOption,
    placeholder = "Search...",
    loading = false,
}: SearchableSelectProps<T>) {
    const [isOpen, setIsOpen] = useState(false);
    const [searchTerm, setSearchTerm] = useState("");
    const containerRef = useRef<HTMLDivElement>(null);
    const inputRef = useRef<HTMLInputElement>(null);

    // Filter items based on search term
    const filteredItems = items.filter((item) =>
        getSearchFields(item).some((field) =>
            field.toLowerCase().includes(searchTerm.toLowerCase())
        )
    );

    // Get selected item
    const selectedItem = items.find((item) => item.id === selectedId);

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

    const handleSelect = (id: number) => {
        onSelect(id);
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
                    {selectedItem ? getLabel(selectedItem) : "-- Select an option --"}
                </span>
                <span className={`${styles.arrow} ${isOpen ? styles.open : ""}`}>▼</span>
            </button>

            {isOpen && (
                <div className={styles.dropdown}>
                    <input
                        ref={inputRef}
                        type="text"
                        className={styles.searchInput}
                        placeholder={placeholder}
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />

                    <div className={styles.optionsList}>
                        {filteredItems.length > 0 ? (
                            filteredItems.map((item) => (
                                <button
                                    key={item.id}
                                    className={`${styles.option} ${
                                        selectedId === item.id ? styles.selected : ""
                                    }`}
                                    onClick={() => handleSelect(item.id)}
                                    type="button"
                                >
                                    {renderOption ? (
                                        renderOption(item)
                                    ) : (
                                        <span className={styles.optionTitle}>{getLabel(item)}</span>
                                    )}
                                </button>
                            ))
                        ) : (
                            <div className={styles.noResults}>No options found</div>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}
