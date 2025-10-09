import { NgModule } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { CsrFormComponent } from "./csr-form/csr-form.component";
import { CommonModule } from "@angular/common";
import { PendingCsrListComponent } from './pending-csr-list/pending-csr-list.component';

@NgModule({
    declarations: [
        CsrFormComponent,
        PendingCsrListComponent
    ],
    imports: [
        ReactiveFormsModule,
        CommonModule
    ]
})

export class CsrModule{}