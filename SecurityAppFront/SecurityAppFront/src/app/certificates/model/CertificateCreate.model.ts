export interface CertificateRequestDTO{
    subject:string,
    issuerId:number,
    durationInDays:number,
    isCA: boolean,
    extensions: Map<string,string>;
}