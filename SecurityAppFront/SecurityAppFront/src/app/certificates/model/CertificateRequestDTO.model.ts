export interface CertificateRequestDTO{
    cn:string,
    o: string,
    ou: string,
    c: string,
    issuerId:number | null,
    durationInDays:number,
    isRoot: boolean,
    isIntermediate: boolean,
    isEndEntity: boolean,
    isCA: boolean,
    extensions: { [key: string]: string };
}