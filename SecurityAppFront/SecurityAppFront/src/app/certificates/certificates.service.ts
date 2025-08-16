import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CertificateResponse } from './model/CertificateResponse.model';

@Injectable({
  providedIn: 'root'
})
export class CertificatesService {

  private apiUrl = 'http://localhost:8080/api/certificates';
  constructor(private http: HttpClient) { }

  getCertificates(): Observable<CertificateResponse[]>{
    return this.http.get<CertificateResponse[]>(`${this.apiUrl}/all`);
  }
}
