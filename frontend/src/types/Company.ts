export type CompanyRequest = {
    name: string,
    rating: number | null
}

export type CompanyResponse = {
    id: number,
    name: string,
    rating: number | null
}