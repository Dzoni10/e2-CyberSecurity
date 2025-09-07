export interface CertificateRequestDTO{
    subject:string,
    issuerId:number | null,
    durationInDays:number,
    isCA: boolean,
    extensions: { [key: string]: string };
}