import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { DecodedToken } from './model/decodedToken.model';
import { HttpClient } from '@angular/common/http';
import { Register } from './model/Register.model';
import {jwtDecode} from 'jwt-decode'
import { AuthResponse } from './model/AuthResponse';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private currentUserSubject = new BehaviorSubject<DecodedToken | null>(this.loadUserFromToken());
  currentUser$ = this.currentUserSubject.asObservable();

  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) { }

  register(registration: Register): Observable<any>{
    return this.http.post(`${this.apiUrl}/signup`,registration);
  }

   login(email: string, password: string):  Observable<AuthResponse>{
    const body = {email, password};
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`,body)
  }

  saveToken(token: string){
    localStorage.setItem('jwtToken',token)
    const decoded=jwtDecode<DecodedToken>(token);
    this.currentUserSubject.next(decoded);
  }

  getToken():string|null{
    return localStorage.getItem('jwtToken')
  }


  getCurrentUser(): DecodedToken|null{
    return this.currentUserSubject.value;
  }

  logout(){
    localStorage.removeItem('jwtToken');
    this.currentUserSubject.next(null);
  }

  private loadUserFromToken(): DecodedToken | null {
    const token = this.getToken();
    if(token){
      try{
        const decoded = jwtDecode<DecodedToken>(token);

        if(this.isTokenExpired(decoded)){
          this.logout();
          return null;
        }

        return decoded;
        }
      catch
          {
              return null;
          }
    }
    return null;
  }

  private isTokenExpired(decoded: DecodedToken): boolean{
    return decoded.exp*1000 < Date.now();
  }

}
