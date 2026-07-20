import { Component, signal, inject, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { ClientService } from '../../../../../../core/services/client.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-client-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './client-form.component.html',
})
export class ClientFormComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private clientService = inject(ClientService);

  isSubmitting = signal(false);
  error = signal<string | null>(null);

  form: FormGroup = this.fb.group({
    companyName: ['', Validators.required],
    contactName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: [''],
    address: [''],
    city: [''],
    country: ['Côte d\'Ivoire'],
    notes: [''],
  });

  onSubmit(): void {
    if (this.form.invalid) return;
    this.isSubmitting.set(true);
    this.error.set(null);
    const val = this.form.value;
    const fullAddress = [val.address, val.city, val.country].filter(Boolean).join(', ');
    this.clientService.createClient({
      companyName: val.companyName,
      contactName: val.contactName,
      email: val.email,
      phone: val.phone || undefined,
      address: fullAddress || undefined,
    }).subscribe({
      next: () => this.router.navigate(['/dashboard/clients']),
      error: (err) => {
        this.error.set(err?.detail || 'Erreur lors de la création du client');
        this.isSubmitting.set(false);
      },
    });
  }

  cancel(): void {
    this.router.navigate(['/dashboard/clients']);
  }
}
