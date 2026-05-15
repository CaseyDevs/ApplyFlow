import type { ApplicationStatus } from "./ApplicationStatus";

export type Application = {
    id: number,
    title: string;
    url: string;
    status: ApplicationStatus;
    location: string | null;
    companyId: number;
    interviewIds: number[];
    createdAt: string
};

export type ApplicationRequest = {
    title: string;
    url: string;
    location: string | null;
    companyId: number;
    interviewId: number | null;
    status: ApplicationStatus;
};

export type UpdateApplicationRequest = {
    title: string | null;
    url: string | null;
    location: string | null;
    companyId: number | null;
    status: ApplicationStatus | null;
}

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
