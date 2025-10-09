import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component';
import {MatFormFieldModule} from '@angular/material/form-field'
import {MatButtonModule} from '@angular/material/button'
import { ReactiveFormsModule } from '@angular/forms';
import { MatOptionModule } from '@angular/material/core';
import {MatInputModule} from '@angular/material/input';
import { PassRecoveryComponent } from './pass-recovery/pass-recovery.component';
import { SessionManagementComponent } from './session-management/session-management.component';
import { CreateCaUserComponent } from './create-ca-user/create-ca-user.component'


@NgModule({
  declarations: [
    LoginComponent,
    SignupComponent,
    PassRecoveryComponent,
    SessionManagementComponent,
    CreateCaUserComponent
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
