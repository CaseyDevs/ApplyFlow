export type CompanyRequest = {
    name: string,
    location: string | null,
    rating: number | null
}

export type CompanyResponse = {
    id: number,
    name: string,
    location: string | null,
    rating: number | null
}