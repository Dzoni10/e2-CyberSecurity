export interface CertificateSigningRequest {
  id?: number;
  filename?: string;
  uploadedAt?: string;           // ISO string
  uploadedByUserId?: number;
  pemContent?: string;
  subject?: string;
  publicKeyAlgorithm?: string;
  keySize?: number;
  selectedCaId?: number;
  requestedDurationDays?: number;
  publicKey?: string;
  status?: 'PENDING' | 'APPROVED' | 'REJECTED' | 'ISSUED';
  rejectionReason?: string;
  issuedCertificateId?: number;
  processedAt?: string;          // ISO string
  processedByUserId?: number;
}
