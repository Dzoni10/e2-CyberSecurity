export interface CSRDecisionDTO {
  csrId: number;
  approved: boolean;
  rejectionReason?: string;
  finalDurationDays?: number;
  userId?: number;   // dodajemo da frontend može proslediti userId
}
