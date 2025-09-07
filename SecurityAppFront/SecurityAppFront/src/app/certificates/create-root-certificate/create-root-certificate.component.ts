import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CertificatesService } from '../certificates.service';
import { AuthService } from 'src/app/auth/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CertificateRequestDTO } from '../model/CertificateRequestDTO.model';
import {trigger,state,style,transition,animate} from '@angular/animations'

@Component({
  selector: 'app-create-root-certificate',
  templateUrl: './create-root-certificate.component.html',
  styleUrls: ['./create-root-certificate.component.css'],
  animations:[trigger('slideIn', [
          state('void', style({ transform: 'translateY(0)', opacity: 0 })),
          transition(':enter', [
            animate('2.0s ease-out', style({ transform: 'translateY(0)', opacity: 1 }))
          ])
        ])]
})
export class CreateRootCertificateComponent implements OnInit {

  rootForm!: FormGroup;
  userId!: number | null;

  constructor(private fb: FormBuilder,private certificateService: CertificatesService,private authService : AuthService, private snackBar: MatSnackBar){}

  ngOnInit(): void {
    this.rootForm = this.fb.group({
      subject:['',Validators.required],
      durationInDays: [365, [Validators.required,Validators.min(1)]],
      isCA:[true],
    extensions: this.fb.control({})
    });

    const currentUser = this.authService.getCurrentUser();
    this.userId = currentUser ? currentUser.userId : null;
  }

  onSubmit() {
    if(this.rootForm.valid && this.userId !==null){
      const dto: CertificateRequestDTO={
        subject:this.rootForm.value.subject,
        issuerId: null,
        durationInDays: this.rootForm.value.durationInDays,
        isCA: this.rootForm.value.isCA,
        extensions:this.rootForm.value.extensions||{}
      };

      this.certificateService.issueCertificate(dto).subscribe({
        next: res=>{
           console.log('Root certificate issued'+res);
           this.snackBar.open("Root certificate created","Close",{duration:4000,horizontalPosition:"center"});
           this.rootForm.reset({durationInDays:365,isCA:true});
        },
        error:err=>{
          console.error('Error during making certificate',err);
          this.snackBar.open("Error during certificate issue","Close",{duration:4000,horizontalPosition:"center"});
        }
      });
    }
  }

}
