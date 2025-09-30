import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from 'src/app/auth/auth.service';
import { CertificatesService } from '../certificates.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CertificateRequestDTO } from '../model/CertificateRequestDTO.model';
import { ActivatedRoute } from '@angular/router';
import {trigger,state,style,transition,animate} from '@angular/animations'


@Component({
  selector: 'app-create-int-certificate',
  templateUrl: './create-int-certificate.component.html',
  styleUrls: ['./create-int-certificate.component.css'],
  animations:[trigger('slideIn', [
          state('void', style({ transform: 'translateY(0)', opacity: 0 })),
          transition(':enter', [
            animate('2.0s ease-out', style({ transform: 'translateY(0)', opacity: 1 }))
          ])
        ])]
})
export class CreateIntCertificateComponent implements OnInit{

  intermediateForm!: FormGroup;
    userId!: number | null;
  issuerId!: number
  maxDays!:number

constructor(private fb: FormBuilder,private route: ActivatedRoute,private certificateService: CertificatesService,private authService : AuthService, private snackBar: MatSnackBar){}

  ngOnInit(): void {

    this.route.queryParams.subscribe(params=>{
      this.issuerId=params['issuerId'];

      this.certificateService.getCertificateById(this.issuerId).subscribe(issuer=>{
      const today = new Date();
      const end = new Date(issuer.endDate);
      const diff = Math.floor((end.getTime() - today.getTime())/(1000*60*60*24));
      this.maxDays = diff;
    });
    });


    this.intermediateForm = this.fb.group({
          cn:['',Validators.required],
          o:['',Validators.required],
          ou:['',Validators.required],
          c:['',Validators.required],
          durationInDays: [1, [Validators.required,Validators.min(1)]],
          isRoot:[false],
          isIntermediate:[true],
          isEndEntity:[false],
          isCA:[true],
        extensions: this.fb.control({})
        });
    
        const currentUser = this.authService.getCurrentUser();
        this.userId = currentUser ? currentUser.userId : null;
  }

    onSubmit(){
      if(this.intermediateForm.valid && this.userId !==null){
      const dto: CertificateRequestDTO={
        cn:this.intermediateForm.value.cn,
        o:this.intermediateForm.value.o,
        ou: this.intermediateForm.value.ou,
        c: this.intermediateForm.value.c,
        issuerId:this.issuerId,
        durationInDays: this.intermediateForm.value.durationInDays,
        isRoot:false,
        isIntermediate: true,
        isEndEntity:false,
        isCA:true,
        extensions:this.intermediateForm.value.extensions||{}
      };

      this.certificateService.issueCertificate(dto).subscribe({
        next: res=>{
           console.log('Intermediate certificate issued'+res);
           this.snackBar.open("Intermediate certificate created","Close",{duration:4000,horizontalPosition:"center"});
           this.intermediateForm.reset({durationInDays:1,isCA:true});
        },
        error:err=>{
          console.error('Error during making certificate',err);
          this.snackBar.open("Error during certificate issue","Close",{duration:4000,horizontalPosition:"center"});
        }
      });
    }
  }
}
