import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Observable } from 'rxjs';

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

}
