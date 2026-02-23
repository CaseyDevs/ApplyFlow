import type { ApplicationStatus } from "./ApplicationStatus";

export type Application = {
    title: string;
    url: string;
    status: ApplicationStatus;
    companyId: number;
    interviewIds: number[];
};

// Matches Spring Boot's Page<T> response
export type Page<T> = {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number; // current page (0-indexed)
    first: boolean;
    last: boolean;
};
