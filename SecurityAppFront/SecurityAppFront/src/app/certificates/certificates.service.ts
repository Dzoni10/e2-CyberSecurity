import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CertificateResponse } from './model/CertificateResponse.model';
import { CertificateRequestDTO } from './model/CertificateRequestDTO.model';
import { AuthService } from '../auth/auth.service';
import { CertificateTemplate } from './model/CertificateTemplate.model';
@Injectable({
  providedIn: 'root'
})
export class CertificatesService {

  private apiUrl = 'http://localhost:8080/api/certificates';
  private apiTemplateUrl = 'http://localhost:8080/api/templates';
  constructor(private http: HttpClient, private authService: AuthService) { }

  // svaka metoda mora imati headers deo u slanju zahteva ka serveru
  // da bi se znalo koji korisnik pristupa i da li ima privilegije
  getCertificates(): Observable<CertificateResponse[]>{
    return this.http.get<CertificateResponse[]>(`${this.apiUrl}/all`,{headers:this.getAuthHeaders()});
  }

  getCACertificates(): Observable<CertificateResponse[]>{
    console.log("d")
    return this.http.get<CertificateResponse[]>(`${this.apiUrl}/ca`,{headers:this.getAuthHeaders()});
  }
  
  getCACertificatesByOrg(): Observable<any[]> {
    const headers = this.authService.getAuthHeaders();
    const userId = this.authService.getCurrentUser()?.userId

    const params = { userId: userId?.toString() || ''}
    return this.http.get<any[]>(`${this.apiUrl}/caOrg`, { headers, params});
  }

  getCACertificatesByUser(): Observable<any[]> {
    const headers = this.authService.getAuthHeaders();
    const userId = this.authService.getCurrentUser()?.userId

    const params = { userId: userId?.toString() || ''}
    return this.http.get<any[]>(`${this.apiUrl}/by-user`, { headers, params});
  }

  issueCertificate(request: CertificateRequestDTO) {
  return this.http.post<CertificateResponse>(`${this.apiUrl}/issue`, request,{headers: this.getAuthHeaders()});
}

getCertificateById(id: number): Observable<CertificateResponse> {
  return this.http.get<CertificateResponse>(`${this.apiUrl}/id/${id}`,
    { headers: this.getAuthHeaders() }
  );
}

revokeCertificate(certificateId: number, reason: string): Observable<any> {
  const headers = this.authService.getAuthHeaders();
  return this.http.put(`${this.apiUrl}/${certificateId}/revoke`, { reason }, { headers });
}

createTemplate(template: CertificateTemplate) {
  const headers = this.authService.getAuthHeaders();
  const userId = this.authService.getCurrentUser()?.userId

    const params = { userId: userId?.toString() || ''}
  return this.http.post(`${this.apiTemplateUrl}/create`, template, {params, headers});
}

getAllTemplatesByIssuer(issuerId: number) {
  return this.http.get<CertificateTemplate[]>(`${this.apiTemplateUrl}/by-issuer/${issuerId}`);
}


//metoda koja dobavlja header jer u svaki zahtev ka serveru mora da se salje token kako bi se validirao korisik koji je ulogovan
// i kako bi se znalo sta ulogovan sme da koristi od URL-ova
//
private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    let headers = new HttpHeaders();
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    return headers;
  }


}
