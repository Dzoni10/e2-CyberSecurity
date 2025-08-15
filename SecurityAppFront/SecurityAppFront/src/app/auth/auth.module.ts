import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component';
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatButtonModule} from '@angular/material/button'
import { ReactiveFormsModule } from '@angular/forms';
import { MatOptionModule } from '@angular/material/core';
import {MatInputModule} from '@angular/material/input';
import { PassRecoveryComponent } from './pass-recovery/pass-recovery.component'


@NgModule({
  declarations: [
    LoginComponent,
    SignupComponent,
    PassRecoveryComponent
  ],
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatButtonModule,
    ReactiveFormsModule,
    MatOptionModule,
    MatInputModule
  ]
})
export class AuthModule { }
