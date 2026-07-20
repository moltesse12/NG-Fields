import { Component, signal, inject, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { IconComponent } from '../../../../../../shared/ui/icon/icon.component';
import { InterventionService } from '../../../../../../core/services/intervention.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-intervention-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, IconComponent],
  templateUrl: './intervention-form.component.html',
})
export class InterventionFormComponent {
  private fb = inject(FormBuilder);
  protected router = inject(Router);
  private interventionService = inject(InterventionService);

  currentStep = signal(1);
  totalSteps = 8;
  isSubmitting = signal(false);
  isSubmitted = signal(false);
  error = signal<string | null>(null);

  form: FormGroup = this.fb.group({
    siteName: ['', Validators.required],
    interventionDate: ['', Validators.required],
    interventionType: ['', Validators.required],
    priority: ['medium'],
    technology: [''],
    equipment: [''],
    situation: [''],
    panneType: [''],
    description: [''],
    cause: [''],
    actions: ['', Validators.required],
    duration: [0],
    technicianId: [''],
    status: ['resolved', Validators.required],
    technicianSignature: [false],
    observation: [''],
  });

  nextStep(): void {
    if (this.currentStep() < this.totalSteps) {
      this.currentStep.update(s => s + 1);
    }
  }

  prevStep(): void {
    if (this.currentStep() > 1) {
      this.currentStep.update(s => s - 1);
    }
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.isSubmitting.set(true);
    this.error.set(null);
    const val = this.form.value;
    this.interventionService.createIntervention({
      reference: `INT-${Date.now()}`,
      clientId: undefined,
      clientName: val.siteName,
      equipmentType: val.equipment || undefined,
      reportedIssue: val.description || undefined,
      diagnosis: val.cause || undefined,
      workDone: val.actions || undefined,
      status: val.status === 'resolved' ? 'COMPLETED' : 'IN_PROGRESS',
      interventionDate: val.interventionDate || undefined,
      assignedTo: val.technicianId || undefined,
      notes: val.observation || undefined,
      siteAddress: val.situation || undefined,
    }).subscribe({
      next: () => { this.isSubmitted.set(true); this.isSubmitting.set(false); },
      error: () => { this.error.set('Erreur lors de la création'); this.isSubmitting.set(false); },
    });
  }

  cancel(): void {
    this.router.navigate(['/dashboard/interventions']);
  }
}
