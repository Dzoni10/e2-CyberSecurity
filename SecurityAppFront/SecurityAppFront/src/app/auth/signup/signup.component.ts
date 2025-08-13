import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../auth.service';
import { FormBuilder, Validators,FormGroup } from '@angular/forms';
import * as zxcvbn from 'zxcvbn';
import {trigger,state,style,transition,animate} from '@angular/animations'

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
  animations:[trigger('slideIn', [
        state('void', style({ transform: 'translateY(0)', opacity: 0 })),
        transition(':enter', [
          animate('2.0s ease-out', style({ transform: 'translateY(0)', opacity: 1 }))
        ])
      ])]
})
export class SignupComponent  implements OnInit{

  signupForm!: FormGroup
  passwordStrength: number=0;
  passwordFeedback:string='';

  constructor(private fb: FormBuilder, private authService: AuthService,private snackBar: MatSnackBar ){}

  ngOnInit(): void {
    
    this.signupForm=this.fb.group({
      name: ['', Validators.required],
      surname: ['', Validators.required],
      email: ['', Validators.required],
      password: ['', Validators.required],
      password2: ['', Validators.required], // Dodato
      organization:['',Validators.required]
    })
  }

  register(): void{
    if (this.signupForm.valid){

      if(this.signupForm.value.password!==this.signupForm.value.password2)
      {
        this.snackBar.open("The passwords do not match!","Close",{duration:400,horizontalPosition:"center"})
        return; 
      }
      
      const { password2, ...payload } = this.signupForm.value; // izbacujemo password2
      this.authService.register(payload).subscribe({
        next: res=> {
          console.log("REGISTER SUCCESS");
          this.snackBar.open("Register successfully!","Close",{duration:3000,horizontalPosition:"center"})
          this.signupForm.reset();
                },
        error: err=>{ 

          if (err.status === 201) {
              this.snackBar.open("Register successfully!", "Close", { duration: 3000, horizontalPosition: "center" });
              this.signupForm.reset();
            }
            else 
            {
              this.snackBar.open("Cannot register!", "Close", { duration: 3000, horizontalPosition: "center" });
            } 
    }
    });
  }
}


checkPasswordStrength(){
  const pwd = this.signupForm.get('password')?.value || '';
  const result = zxcvbn(pwd);
  this.passwordStrength=result.score
  this.passwordFeedback=result.feedback.suggestions.join(' ');
}
}
