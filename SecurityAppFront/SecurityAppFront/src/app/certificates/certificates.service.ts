import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CertificateResponse } from './model/CertificateResponse.model';
import { CertificateRequestDTO } from './model/CertificateRequestDTO.model';
import { AuthService } from '../auth/auth.service';
@Injectable({
  providedIn: 'root'
})
export class CertificatesService {

  private apiUrl = 'http://localhost:8080/api/certificates';
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

  issueCertificate(request: CertificateRequestDTO) {
  return this.http.post<CertificateResponse>(`${this.apiUrl}/issue`, request,{headers: this.getAuthHeaders()});
}

getCertificateById(id: number): Observable<CertificateResponse> {
  return this.http.get<CertificateResponse>(`${this.apiUrl}/${id}`,
    { headers: this.getAuthHeaders() }
  );
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
