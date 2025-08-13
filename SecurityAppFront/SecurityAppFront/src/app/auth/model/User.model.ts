export interface User
{
    id: number;
    name:string,
    surname: string,
    email: string,
    password: string,
    role: string,
    organization:string,
    is_verified: boolean
}