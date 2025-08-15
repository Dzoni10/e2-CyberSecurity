import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { SignupComponent } from './auth/signup/signup.component';
import { PassRecoveryComponent } from './auth/pass-recovery/pass-recovery.component';

const routes: Routes = [
   { path: 'login', component: LoginComponent },
   { path: 'signup', component: SignupComponent},
   { path: 'recovery', component:PassRecoveryComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
