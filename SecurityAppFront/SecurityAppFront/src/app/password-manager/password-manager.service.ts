import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';

export interface PasswordEntry {
  id: number;
  siteName: string;
  username: string;
  encryptedPassword: string;
  createdAt: string;
}

export interface PasswordEntryRequest {
  siteName: string;
  username: string;
  encryptedPassword: string;
}

export interface SharePasswordRequest {
  passwordEntryId: number;
  shareWithUserId: number;
  encryptedPasswordForUser: string;
}

export interface UserWithKey {
  userId: number;
  commonName: string;
}

@Injectable({
  providedIn: 'root'
})
export class PasswordManagerService {
  private apiUrl = 'http://localhost:8080/api/password-manager';

  constructor(private http: HttpClient, private authService: AuthService) {}

  savePasswordEntry(request: PasswordEntryRequest): Observable<PasswordEntry> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post<PasswordEntry>(`${this.apiUrl}/entries`, request, { headers });
  }

  getUserPasswordEntries(): Observable<PasswordEntry[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<PasswordEntry[]>(`${this.apiUrl}/entries`, { headers });
  }

  deletePasswordEntry(id: number): Observable<void> {
    const headers = this.authService.getAuthHeaders();
    return this.http.delete<void>(`${this.apiUrl}/entries/${id}`, { headers });
  }

  getCurrentUserPublicKey(): Observable<{ publicKey: string }> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<{ publicKey: string }>(`${this.apiUrl}/public-key`, { headers });
  }

  getUserPublicKey(userId: number): Observable<{ publicKey: string }> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<{ publicKey: string }>(`${this.apiUrl}/public-key/${userId}`, { headers });
  }

  sharePassword(request: SharePasswordRequest): Observable<void> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post<void>(`${this.apiUrl}/share`, request, { headers });
  }

  getUsersWithKeys(): Observable<UserWithKey[]> {
  const headers = this.authService.getAuthHeaders();
  return this.http.get<UserWithKey[]>(`${this.apiUrl}/users-with-keys`, { headers });
}
}
