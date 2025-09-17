import { NgModule } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { CsrFormComponent } from "./csr-form/csr-form.component";
import { CommonModule } from "@angular/common";

@NgModule({
    declarations: [
        CsrFormComponent
    ],
    imports: [
        ReactiveFormsModule,
        CommonModule
    ]
})

export class CsrModule{}