import type { User } from "./User"

export type JobBoardRequest = {
    title: string,
    ownerId: number
    members: JobBoardMember[]
}

export type JobBoardResponse = {
    id: number,
    title: string,
    ownerId: number,
    members: JobBoardMember[]
}

export type JobBoardMember = {
    id: number,
    user: User // TODO: UPDATE
    jobBoard: JobBoardResponse
    role: string // TODO: MAKE ENUM OF TYPE ROLE
}