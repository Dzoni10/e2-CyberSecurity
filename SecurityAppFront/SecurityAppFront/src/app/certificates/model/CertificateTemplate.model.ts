export interface CertificateTemplate {
  id?: number;
  name: string;
  issuerId: number;
  commonNameRegex: string;
  subjectAltNameRegex: string;
  ttlDays: number;
  keyUsage: string[];
  extendedKeyUsage: string[];
}