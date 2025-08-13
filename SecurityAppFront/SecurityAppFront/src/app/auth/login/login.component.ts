import { Component } from '@angular/core';
import { FormGroup,FormControl,Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import {trigger,state,style,transition,animate} from '@angular/animations'
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { MatFormFieldControl } from '@angular/material/form-field';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  animations:[
    trigger('slideIn', [
      state('void', style({ transform: 'translateY(0)', opacity: 0 })),
      transition(':enter', [
        animate('2.0s ease-out', style({ transform: 'translateY(0)', opacity: 1 }))
      ])
    ])
  ]
})

export class LoginComponent {

  loginForm = new FormGroup({
    email: new FormControl('', [Validators.required]),
    password: new FormControl('',[Validators.required])
  });

  constructor(private authService: AuthService,private snackBar: MatSnackBar,private router: Router){}

  login(): void{
    
    if(this.loginForm.value.email !==null){
      this.authService.login(this.loginForm.get('email')!.value!, this.loginForm.get('password')!.value!).subscribe({
        next: (res)=>{
          this.authService.saveToken(res.accessToken);
          console.log("Login successfully");
          this.snackBar.open("Logged successfully!","Close",{duration:3000,horizontalPosition:"center"})
          this.loginForm.reset();
          const user = this.authService.getCurrentUser();

          if(user?.role==='ROLE_ADMIN')
          {
            //this.router.navigate['']
          }
          else if(user?.role==='ROLE_CA')
          {
            //this.router.navigate['']
          }
          else
          {
            //this.router.navigate['']
          } 
        },
        error: (err)=>{
          let message='';

          switch(err.status){
            case 404:
              message="User with this credentials doesn't exist";
              break;
            case 406:
              message="You must verify your account via your email";
              break;
            case 401:
              message="Incorrect password";
              break;
            
          }
          this.openSnackBar(message)
        }
      })
    }
  }


  openSnackBar(message: string): void {
    this.snackBar.open(message, '', {
      duration: 4000,
      horizontalPosition: 'center'
    });
  }
}
