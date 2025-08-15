import { AfterViewInit, Component, OnInit } from '@angular/core';
import {trigger,state,style,transition,animate} from '@angular/animations'
import { FormGroup,FormControl,Validators, FormBuilder } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import * as zxcvbn from 'zxcvbn';

declare const grecaptcha: any;

@Component({
  selector: 'app-pass-recovery',
  templateUrl: './pass-recovery.component.html',
  styleUrls: ['./pass-recovery.component.css'],
  animations:[
      trigger('slideIn', [
        state('void', style({ transform: 'translateY(0)', opacity: 0 })),
        transition(':enter', [
          animate('2.0s ease-out', style({ transform: 'translateY(0)', opacity: 1 }))
        ])
      ])
    ]
})

export class PassRecoveryComponent implements AfterViewInit,OnInit {


  recaptchaToken: string | null = null;
  recoveryForm!: FormGroup
  passwordStrength: number=0;
  passwordFeedback:string='';

  
  constructor(private authService: AuthService,private snackBar: MatSnackBar,private router: Router, private fb: FormBuilder){}
  
    ngAfterViewInit(): void {
    if (grecaptcha) {
      grecaptcha.render('recaptcha-container', {
        sitekey: '6Ldm3qUrAAAAADxjsnZHzG4HdMmsCjdKrgFHRmBB',
        callback: (response: string) => {
          this.recaptchaToken = response;
        }
      });
    }
  }

  ngOnInit(): void {
    this.recoveryForm =this.fb.group({
      email: new FormControl('', [Validators.required]),
      password: new FormControl('',[Validators.required]),
      password2: new FormControl('',[Validators.required])
    });
  }


  recovery(): void{

    if(!this.recaptchaToken)
    {
      this.snackBar.open("Please verify you are not a robot","Close",{duration:3000,horizontalPosition:"center"})
      return;
    }

    if(this.recoveryForm.valid)
      {

      if(this.recoveryForm.value.password!==this.recoveryForm.value.password2)
      {
        this.snackBar.open("The passwords do not match!","Close",{duration:400,horizontalPosition:"center"})
        return; 
      }

      const { password2, ...payload } = this.recoveryForm.value; // izbacujem password2
      this.authService.passRecovery(payload).subscribe({
        next: (res)=>{
            console.log("New password changed");
            this.snackBar.open(`Password changed! Verification mail sent on this mail`,"Close",{duration:4000, horizontalPosition:"center"});
            this.recoveryForm.reset();
            this.router.navigate(['/login']);
        },error: (err)=>{
            console.log("Cannot change password",err);
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
            case 201:
              this.snackBar.open("Password changed. Verification mail sent on this email", "Close", { duration: 3000, horizontalPosition: "center" });
              this.recoveryForm.reset();
              break;
            
          }
          this.openSnackBar(message)
        }
      });
    }
  }

  checkPasswordStrength(){
    const pwd = this.recoveryForm.get('newPassword')?.value || '';
    const result = zxcvbn(pwd);
    this.passwordStrength=result.score
    this.passwordFeedback=result.feedback.suggestions.join(' ');
  }

  openSnackBar(message: string): void {
    this.snackBar.open(message, '', {
      duration: 4000,
      horizontalPosition: 'center'
    });
  }
}
