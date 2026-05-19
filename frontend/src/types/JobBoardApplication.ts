import type { Application } from "./Application"
import type { ApplicationStatus } from "./ApplicationStatus"

export type JobBoardApplicationRequest = {
    applicationId: number | null, //  null when fetching all applications
    jobBoardId: number
}

export type JobBoardApplicationResponse = {
    id: number,
    application: Application,
    addedAt: Date,
    addedByEmail: string,
    statusList: JobBoardStatus[]
}

export type JobBoardStatus = {
    id: number,
    jobBoardApplicationId: number,
    userId: number,
    userEmail: string,
    status: ApplicationStatus,
    updatedAt: Date,
    updatedBy: string
}