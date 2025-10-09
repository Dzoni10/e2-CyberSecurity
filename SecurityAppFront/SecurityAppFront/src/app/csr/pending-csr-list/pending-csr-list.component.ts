import { Component } from '@angular/core';
import { CSRDecisionDTO } from '../model/CSRDecisionDTO.model';
import { CertificateSigningRequest } from '../model/CertificateSigningRequest.model';
import { FormGroup, FormBuilder } from '@angular/forms';
import { AuthService } from 'src/app/auth/auth.service';
import { CsrService } from '../csr.service';

@Component({
  selector: 'app-pending-csr-list',
  templateUrl: './pending-csr-list.component.html',
  styleUrls: ['./pending-csr-list.component.css']
})
export class PendingCsrListComponent {
csrs: CertificateSigningRequest[] = [];
  loading = false;
  errorMessage = '';
  decisionForms: { [key: number]: FormGroup } = {}; // dynamic form per CSR

  constructor(
    private csrService: CsrService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.loadPendingCSRs();
  }

  loadPendingCSRs() {
    this.loading = true;
    this.errorMessage = '';

    const userId = this.authService.getCurrentUser()?.userId;

    if (!userId) {
      this.errorMessage = 'User not logged in';
      this.loading = false;
      return;
    }

    this.csrService.getPendingCSRs(userId).subscribe({
      next: (data: CertificateSigningRequest[]) => {
        this.csrs = data;
        // Create form for each CSR
        this.csrs.forEach(csr => {
          this.decisionForms[csr.id!] = this.fb.group({
            approved: [true],
            finalDurationDays: [csr.requestedDurationDays],
            rejectionReason: ['']
          });
        });
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load CSRs';
        console.error(err);
        this.loading = false;
      }
    });
  }

  approve(csrId: number) {
    const form = this.decisionForms[csrId];
    const decision: CSRDecisionDTO = {
      csrId: csrId,
      approved: true,
      finalDurationDays: form.value.finalDurationDays,
      rejectionReason: ''
    };

    this.processDecision(decision);
  }

  reject(csrId: number) {
    const form = this.decisionForms[csrId];
    const reason = form.value.rejectionReason?.trim();
    if (!reason) {
      alert('Rejection reason is required');
      return;
    }

    const decision: CSRDecisionDTO = {
      csrId: csrId,
      approved: false,
      finalDurationDays: undefined,
      rejectionReason: reason
    };

    this.processDecision(decision);
  }

  private processDecision(decision: CSRDecisionDTO) {
    this.csrService.processCSRDecision(decision).subscribe({
      next: () => {
        alert(`CSR ${decision.csrId} processed successfully`);
        // Remove processed CSR from list
        this.csrs = this.csrs.filter(c => c.id !== decision.csrId);
        delete this.decisionForms[decision.csrId!];
      },
      error: (err) => {
        console.error(err);
        alert(`Failed to process CSR ${decision.csrId}`);
      }
    });
  }
}
