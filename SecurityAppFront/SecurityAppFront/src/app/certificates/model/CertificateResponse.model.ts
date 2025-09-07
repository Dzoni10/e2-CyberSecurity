export interface CertificateResponse{
    id: number,
    alias:string,
    serialNumber:string,
    subject:string,
    issuer:string,
    startDate:Date,
    endDate:Date
    isCA:boolean,
    revoked:boolean
}