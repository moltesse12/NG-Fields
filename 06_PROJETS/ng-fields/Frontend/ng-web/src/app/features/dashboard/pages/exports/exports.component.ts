import { Component, signal , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IconComponent } from '../../../../shared/ui/icon/icon.component';

interface ExportJob {
  id: string;
  type: 'CSV' | 'Excel' | 'PDF';
  name: string;
  status: 'completed' | 'processing' | 'failed';
  size: string;
  createdAt: Date;
}

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-exports',
  standalone: true,
  imports: [CommonModule, IconComponent],
  template: `
    <div class="flex flex-col gap-6 p-4 md:p-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold tracking-tight">Exports</h1>
          <p class="text-sm text-muted-foreground">Générer et télécharger des rapports dans différents formats</p>
        </div>
        <button class="inline-flex items-center gap-2 rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90">
          <app-icon name="circle-plus" />
          Nouvel export
        </button>
      </div>

      <div class="grid gap-4 md:grid-cols-3">
        @for (type of exportTypes(); track type.name) {
          <div class="rounded-lg border bg-card p-6 shadow-sm hover:shadow-md transition-shadow cursor-pointer">
            <div class="flex items-center gap-4">
              <div class="flex h-12 w-12 items-center justify-center rounded-lg bg-secondary">
                @if (type.name === 'CSV') { <span class="text-xl text-blue-600">CSV</span> }
                @if (type.name === 'Excel') { <span class="text-xl text-green-600">XLS</span> }
                @if (type.name === 'PDF') { <span class="text-xl text-red-600">PDF</span> }
              </div>
              <div>
                <h3 class="font-semibold">{{ type.name }}</h3>
                <p class="text-sm text-muted-foreground">{{ type.description }}</p>
              </div>
            </div>
          </div>
        }
      </div>

      <div class="rounded-lg border bg-card shadow-sm">
        <div class="p-6 border-b">
          <h3 class="text-lg font-semibold">Exports récents</h3>
          <p class="text-sm text-muted-foreground">Rapports générés et téléchargements</p>
        </div>
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="border-b bg-muted/50 text-left">
                <th class="px-6 py-3 text-xs font-medium text-muted-foreground uppercase">Nom</th>
                <th class="px-6 py-3 text-xs font-medium text-muted-foreground uppercase">Type</th>
                <th class="px-6 py-3 text-xs font-medium text-muted-foreground uppercase">Statut</th>
                <th class="px-6 py-3 text-xs font-medium text-muted-foreground uppercase">Taille</th>
                <th class="px-6 py-3 text-xs font-medium text-muted-foreground uppercase">Créé le</th>
                <th class="px-6 py-3 text-xs font-medium text-muted-foreground uppercase text-right">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y">
              @for (job of exports(); track job.id) {
                <tr class="hover:bg-muted/30 transition-colors">
                  <td class="px-6 py-4 font-medium">{{ job.name }}</td>
                  <td class="px-6 py-4">
                    <span class="inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium"
                      [class.bg-blue-100]="job.type === 'CSV'" [class.text-blue-800]="job.type === 'CSV'"
                      [class.bg-green-100]="job.type === 'Excel'" [class.text-green-800]="job.type === 'Excel'"
                      [class.bg-red-100]="job.type === 'PDF'" [class.text-red-800]="job.type === 'PDF'"
                    >{{ job.type }}</span>
                  </td>
                  <td class="px-6 py-4">
                    <span class="inline-flex items-center gap-1.5 rounded-full px-2.5 py-0.5 text-xs font-medium"
                      [class.bg-green-100]="job.status === 'completed'" [class.text-green-800]="job.status === 'completed'"
                      [class.bg-yellow-100]="job.status === 'processing'" [class.text-yellow-800]="job.status === 'processing'"
                      [class.bg-red-100]="job.status === 'failed'" [class.text-red-800]="job.status === 'failed'"
                    >
                      @if (job.status === 'processing') {
                        <span class="h-2 w-2 rounded-full bg-current animate-pulse"></span>
                      }
                      @if (job.status === 'completed') { Terminé }
                      @if (job.status === 'processing') { En cours }
                      @if (job.status === 'failed') { Échoué }
                    </span>
                  </td>
                  <td class="px-6 py-4 text-muted-foreground">{{ job.size }}</td>
                  <td class="px-6 py-4 text-muted-foreground">{{ job.createdAt | date:'short' }}</td>
                  <td class="px-6 py-4 text-right">
                    @if (job.status === 'completed') {
                      <button class="inline-flex items-center gap-1 rounded-md border px-3 py-1.5 text-xs font-medium hover:bg-muted">
                        <app-icon name="download" />
                        Télécharger
                      </button>
                    }
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class ExportsComponent {
  exportTypes = signal([
    { name: 'CSV', description: 'Valeurs séparées par virgule pour analyse de données' },
    { name: 'Excel', description: 'Format de feuille de calcul Microsoft Excel' },
    { name: 'PDF', description: 'Document portable pour rapports imprimables' },
  ]);

  exports = signal<ExportJob[]>([
    { id: '1', type: 'PDF', name: 'Rapport Interventions - Juillet 2026', status: 'completed', size: '2.4 Mo', createdAt: new Date('2026-07-08T10:30:00') },
    { id: '2', type: 'Excel', name: 'Export Liste Clients', status: 'completed', size: '856 Ko', createdAt: new Date('2026-07-07T14:20:00') },
    { id: '3', type: 'CSV', name: 'Données Performance Techniciens', status: 'processing', size: '—', createdAt: new Date('2026-07-08T11:45:00') },
    { id: '4', type: 'PDF', name: 'Statistiques Mensuelles - Juin 2026', status: 'failed', size: '—', createdAt: new Date('2026-07-06T09:15:00') },
  ]);
}
