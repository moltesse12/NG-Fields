import { Component, signal, inject, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { UserService } from '../../../../core/services/user.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  template: `
    <div class="max-w-2xl mx-auto p-4 md:p-6">
      <div class="flex items-center justify-between mb-6">
        <div>
          <h1 class="text-2xl font-bold">Nouvel utilisateur</h1>
          <p class="text-sm text-muted-foreground">Ajouter un membre à l'organisation</p>
        </div>
        <button (click)="cancel()" class="rounded-md border px-3 py-1.5 text-sm hover:bg-muted">Annuler</button>
      </div>

      <form [formGroup]="form" class="space-y-4">
        <div>
          <label class="block text-sm font-medium mb-1">Nom complet *</label>
          <input formControlName="name" class="w-full rounded-md border border-input bg-background px-3 py-2 text-sm" placeholder="Jean Dupont" />
          @if (form.get('name')?.invalid && form.get('name')?.touched) {
            <p class="text-xs text-red-500 mt-1">Champ requis</p>
          }
        </div>
        <div>
          <label class="block text-sm font-medium mb-1">Email *</label>
          <input type="email" formControlName="email" class="w-full rounded-md border border-input bg-background px-3 py-2 text-sm" placeholder="jean.dupont@@ng-fields.com" />
          @if (form.get('email')?.invalid && form.get('email')?.touched) {
            <p class="text-xs text-red-500 mt-1">Email invalide</p>
          }
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium mb-1">Rôle *</label>
            <select formControlName="role" class="w-full rounded-md border border-input bg-background px-3 py-2 text-sm">
              <option value="TECHNICIAN">Technicien</option>
              <option value="MANAGER">Manager</option>
              <option value="ADMIN">Administrateur</option>
            </select>
            @if (form.get('role')?.invalid && form.get('role')?.touched) {
              <p class="text-xs text-red-500 mt-1">Champ requis</p>
            }
          </div>
          <div>
            <label class="block text-sm font-medium mb-1">Statut</label>
            <select formControlName="status" class="w-full rounded-md border border-input bg-background px-3 py-2 text-sm">
              <option value="active">Actif</option>
              <option value="inactive">Inactif</option>
            </select>
          </div>
        </div>
      </form>

      <div class="flex justify-between mt-8">
        <button (click)="cancel()" class="rounded-md border px-4 py-2 text-sm hover:bg-muted">Annuler</button>
        <button (click)="onSubmit()" [disabled]="form.invalid || isSubmitting()"
          class="rounded-md bg-primary px-6 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90 disabled:opacity-50">
          @if (isSubmitting()) { Création en cours... } @else { Créer l'utilisateur }
        </button>
      </div>
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class UserFormComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private userService = inject(UserService);

  isSubmitting = signal(false);

  form: FormGroup = this.fb.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    role: ['TECHNICIAN', Validators.required],
    status: ['active'],
  });

  onSubmit(): void {
    if (this.form.invalid) return;
    this.isSubmitting.set(true);
    const val = this.form.value;
    this.userService.createUser({
      username: val.email,
      email: val.email,
      firstName: val.name.split(' ')[0] || val.name,
      lastName: val.name.split(' ').slice(1).join(' ') || '',
      role: val.role,
    }).subscribe({
      next: () => this.router.navigate(['/dashboard/users']),
      error: () => this.isSubmitting.set(false),
    });
  }

  cancel(): void {
    this.router.navigate(['/dashboard/users']);
  }
}
