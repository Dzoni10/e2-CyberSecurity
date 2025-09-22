export interface CSRRequestDTO {
    csrFile: File;
    privateKeyFile: File; 
    selectedCaId: number;
    requestedDurationDays: number;
}