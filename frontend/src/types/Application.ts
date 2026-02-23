import type { ApplicationStatus } from "./ApplicationStatus";

export type Application = {
    title: string;
    url: string;
    status: ApplicationStatus;
    companyId: number;
    interviewIds: number[];
};
