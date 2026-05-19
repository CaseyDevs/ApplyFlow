import type { JobBoardApplicationResponse } from "./JobBoardApplication"
import type { User } from "./User"

export type JobBoardRequest = {
    title: string,
    userId: number | null
    members: JobBoardMember[] | null
}

export type JobBoardResponse = {
    id: number,
    title: string,
    ownerId: number,
    members: JobBoardMember[],
    applications: JobBoardApplicationResponse[]
}

export type JobBoardMember = {
    id: number,
    user: User // TODO: UPDATE
    jobBoard: JobBoardResponse
    role: string // TODO: MAKE ENUM OF TYPE ROLE
}