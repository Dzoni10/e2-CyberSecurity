import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Observable } from 'rxjs';
import { CertificateSigningRequest } from './model/CertificateSigningRequest.model';
import { CSRDecisionDTO } from './model/CSRDecisionDTO.model';

@Injectable({
  providedIn: 'root'
})
export class CsrService {

  private apiUrl = 'http://localhost:8080/api/csr';
  constructor(private http: HttpClient, private authService: AuthService) { }

  uploadCSR(formData: FormData): Observable<any>{
    const headers = this.authService.getAuthHeaders()
    return this.http.post<any>(`${this.apiUrl}/upload`, formData, {headers});
  }

  getPendingCSRs(userId: number) {
  return this.http.get<CertificateSigningRequest[]>(`${this.apiUrl}/pending`, {
    params: { userId: userId.toString() },
    headers: this.authService.getAuthHeaders()
  });
}

processCSRDecision(decision: CSRDecisionDTO) {
  const userId = this.authService.getCurrentUser()?.userId
  return this.http.post(`${this.apiUrl}/process`, decision, {
    params: { userId: userId?.toString() || "" },
    headers: this.authService.getAuthHeaders()
  });
}


}
