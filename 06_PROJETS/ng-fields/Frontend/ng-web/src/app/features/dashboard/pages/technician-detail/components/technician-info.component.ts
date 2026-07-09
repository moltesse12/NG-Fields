import { Component, Input , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TechnicianDetailView } from '../../technicians/components/schemas/technician.schema';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-technician-info',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="rounded-lg border bg-card p-4 md:p-6">
      <div class="text-sm font-medium mb-4">Informations générales</div>

      <div class="grid gap-4 md:grid-cols-2">
        <div class="space-y-3">
          <div>
            <span class="text-xs text-muted-foreground uppercase tracking-wide">Adresse</span>
            <p class="text-sm mt-1">{{ technician.address }}</p>
            <p class="text-sm">{{ technician.postalCode }} {{ technician.city }}, {{ technician.country }}</p>
          </div>
          <div>
            <span class="text-xs text-muted-foreground uppercase tracking-wide">Email</span>
            <p class="text-sm"><a [href]="'mailto:' + technician.email" class="text-blue-600 hover:underline">{{ technician.email }}</a></p>
          </div>
        </div>

        <div class="space-y-3">
          <div>
            <span class="text-xs text-muted-foreground uppercase tracking-wide">Département</span>
            <p class="text-sm font-medium">{{ technician.department }}</p>
          </div>
          <div>
            <span class="text-xs text-muted-foreground uppercase tracking-wide">Manager</span>
            <p class="text-sm font-medium">{{ technician.manager }}</p>
            <p class="text-xs text-muted-foreground"><a [href]="'mailto:' + technician.managerEmail" class="text-blue-600 hover:underline">{{ technician.managerEmail }}</a></p>
          </div>
        </div>
      </div>

      <div class="border-t mt-4 pt-4 grid gap-4 md:grid-cols-4">
        <div>
          <span class="text-xs text-muted-foreground uppercase tracking-wide">Interventions (mois)</span>
          <p class="text-2xl font-bold mt-1">{{ technician.interventions.thisMonth }}</p>
        </div>
        <div>
          <span class="text-xs text-muted-foreground uppercase tracking-wide">Interventions (total)</span>
          <p class="text-2xl font-bold mt-1">{{ technician.interventions.total }}</p>
        </div>
        <div>
          <span class="text-xs text-muted-foreground uppercase tracking-wide">Durée moy.</span>
          <p class="text-2xl font-bold mt-1">{{ technician.interventions.avgDuration }} min</p>
        </div>
        <div>
          <span class="text-xs text-muted-foreground uppercase tracking-wide">Embauché depuis</span>
          <p class="text-sm font-medium mt-1">{{ yearsOfService() }} ans</p>
        </div>
      </div>
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class TechnicianInfoComponent {
  @Input() technician!: TechnicianDetailView;

  yearsOfService(): string {
    const years = Math.floor((new Date().getTime() - new Date(this.technician.hireDate).getTime()) / (365.25 * 24 * 60 * 60 * 1000));
    return years.toString();
  }
}
