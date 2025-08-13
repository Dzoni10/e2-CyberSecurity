import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/app/auth/auth.service';
import { DecodedToken } from 'src/app/auth/model/decodedToken.model';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit{

  user: DecodedToken | null=null;
  private subscription!: Subscription;

  constructor(private router:Router, private authService:AuthService){}

  ngOnInit(): void {
    this.subscription = this.authService.currentUser$.subscribe(user=>{
      this.user=user;
    });
  }

  logout(){
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean{
    return this.user!==null;
  }

  ngOnDestroy(): void{
    this.subscription.unsubscribe();
  }

  isLoggedAdmin(): boolean{
    return this.user?.role==='ROLE_ADMIN';
  }

  isLoggedCA(): boolean{
    return this.user?.role==='ROLE_CA';
  }

  isLoggedBasic(): boolean{
    return this.user?.role==='ROLE_BASIC';
  }

}
