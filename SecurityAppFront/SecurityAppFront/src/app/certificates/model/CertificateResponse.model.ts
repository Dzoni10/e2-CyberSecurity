export interface CertificateResponse{
    id: number,
    alias:string,
    serialNumber:string,
    cn:string,
    ou: string,
    o:string,
    c:string,
    issuer:string,
    startDate:Date,
    endDate:Date
    root: boolean,
    intermediate:boolean,
    endEntity: boolean,
    ca:boolean
    revoked:boolean

    // dodajemo za front
  showReasonInput?: boolean;
  revocationReason?: string;
}