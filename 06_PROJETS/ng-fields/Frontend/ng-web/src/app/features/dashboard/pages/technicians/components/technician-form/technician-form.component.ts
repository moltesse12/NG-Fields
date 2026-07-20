import { Component, signal, inject, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { TechnicianService } from '../../../../../../core/services/technician.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-technician-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  template: `
    <div class="max-w-2xl mx-auto p-4 md:p-6">
      <div class="flex items-center justify-between mb-6">
        <div>
          <h1 class="text-2xl font-bold">Nouveau technicien</h1>
          <p class="text-sm text-muted-foreground">Ajouter un technicien à l'équipe</p>
        </div>
        <button (click)="cancel()" class="rounded-md border px-3 py-1.5 text-sm hover:bg-muted">Annuler</button>
      </div>

      <form [formGroup]="form" class="space-y-4">
        @if (error(); as err) {
          <div class="rounded-md bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
            {{ err }}
          </div>
        }
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium mb-1">Prénom *</label>
            <input formControlName="firstName" class="w-full rounded-md border border-input bg-background px-3 py-2 text-sm" placeholder="Jean" />
            @if (form.get('firstName')?.invalid && form.get('firstName')?.touched) {
              <p class="text-xs text-red-500 mt-1">Champ requis</p>
            }
          </div>
          <div>
            <label class="block text-sm font-medium mb-1">Nom *</label>
            <input formControlName="lastName" class="w-full rounded-md border border-input bg-background px-3 py-2 text-sm" placeholder="Dupont" />
            @if (form.get('lastName')?.invalid && form.get('lastName')?.touched) {
              <p class="text-xs text-red-500 mt-1">Champ requis</p>
            }
          </div>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium mb-1">Email *</label>
            <input type="email" formControlName="email" class="w-full rounded-md border border-input bg-background px-3 py-2 text-sm" placeholder="jean.dupont@@ng-fields.com" />
            @if (form.get('email')?.invalid && form.get('email')?.touched) {
              <p class="text-xs text-red-500 mt-1">Email invalide</p>
            }
          </div>
          <div>
            <label class="block text-sm font-medium mb-1">Téléphone</label>
            <input type="tel" formControlName="phone" class="w-full rounded-md border border-input bg-background px-3 py-2 text-sm" placeholder="+228 90 12 34 56" />
          </div>
        </div>
        <div>
          <label class="block text-sm font-medium mb-1">Statut</label>
          <select formControlName="status" class="w-full rounded-md border border-input bg-background px-3 py-2 text-sm">
            <option value="AVAILABLE">Disponible</option>
            <option value="BUSY">Occupé</option>
            <option value="ON_LEAVE">Congé</option>
            <option value="INACTIVE">Inactif</option>
          </select>
        </div>
      </form>

      <div class="flex justify-between mt-8">
        <button (click)="cancel()" class="rounded-md border px-4 py-2 text-sm hover:bg-muted">Annuler</button>
        <button (click)="onSubmit()" [disabled]="form.invalid || isSubmitting()"
          class="rounded-md bg-primary px-6 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90 disabled:opacity-50">
          @if (isSubmitting()) { Création en cours... } @else { Créer le technicien }
        </button>
      </div>
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class TechnicianFormComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private technicianService = inject(TechnicianService);

  isSubmitting = signal(false);
  error = signal<string | null>(null);

  form: FormGroup = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: [''],
    status: ['AVAILABLE'],
  });

  onSubmit(): void {
    if (this.form.invalid) return;
    this.isSubmitting.set(true);
    this.error.set(null);
    const val = this.form.value;
    this.technicianService.createTechnician({
      firstName: val.firstName,
      lastName: val.lastName,
      email: val.email,
      phone: val.phone || undefined,
      status: val.status,
    }).subscribe({
      next: () => this.router.navigate(['/dashboard/technicians']),
      error: (err) => {
        this.error.set(err?.detail || 'Erreur lors de la création du technicien');
        this.isSubmitting.set(false);
      },
    });
  }

  cancel(): void {
    this.router.navigate(['/dashboard/technicians']);
  }
}
