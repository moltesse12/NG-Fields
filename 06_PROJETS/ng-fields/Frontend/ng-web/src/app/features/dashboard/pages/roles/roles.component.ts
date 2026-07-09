import { Component, signal , ChangeDetectionStrategy } from '@angular/core';
import { IconComponent } from '../../../../shared/ui/icon/icon.component';

interface Role {
  id: string;
  name: string;
  description: string;
  type: 'System' | 'Custom';
  usersCount: number;
  permissions: string[];
  status: 'active' | 'needs-review';
}

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-roles',
  standalone: true,
  imports: [IconComponent],
  template: `
    <div class="flex flex-col gap-6 p-4 md:p-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold tracking-tight">Rôles & Permissions</h1>
          <p class="text-sm text-muted-foreground">Gérer les rôles d'accès et les permissions</p>
        </div>
        <div class="flex items-center gap-2">
          <button class="inline-flex items-center gap-2 rounded-md border px-4 py-2 text-sm font-medium hover:bg-muted">
            <app-icon name="download" />
            Importer JSON
          </button>
          <button class="inline-flex items-center gap-2 rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90">
            <app-icon name="circle-plus" />
            Créer un rôle
          </button>
        </div>
      </div>

      <div class="rounded-lg border border-yellow-200 bg-yellow-50 dark:bg-yellow-950 p-4">
        <div class="flex items-start gap-3">
          <app-icon name="alert-triangle" class="text-yellow-500 mt-0.5" />
          <div class="flex-1">
            <h4 class="text-sm font-semibold text-yellow-900 dark:text-yellow-200">Révision requise</h4>
            <p class="text-sm text-yellow-700 dark:text-yellow-400">2 rôles ont des changements de permissions non révisés.</p>
          </div>
          <button class="rounded-md bg-yellow-100 dark:bg-yellow-800 px-3 py-1.5 text-xs font-medium text-yellow-900 dark:text-yellow-200 hover:bg-yellow-200 dark:hover:bg-yellow-700">
            Réviser
          </button>
        </div>
      </div>

      <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        @for (role of roles(); track role.id) {
          <div class="rounded-lg border bg-card p-6 shadow-sm hover:shadow-md transition-shadow">
            <div class="flex items-start justify-between mb-4">
              <div>
                <h3 class="font-semibold text-lg">{{ role.name }}</h3>
                <p class="text-sm text-muted-foreground mt-1">{{ role.description }}</p>
              </div>
              <span class="inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium"
                [class.bg-blue-100]="role.type === 'System'" [class.text-blue-800]="role.type === 'System'"
                [class.bg-purple-100]="role.type === 'Custom'" [class.text-purple-800]="role.type === 'Custom'"
              >{{ role.type === 'System' ? 'Système' : 'Personnalisé' }}</span>
            </div>
            <div class="space-y-2 text-sm">
              <div class="flex justify-between">
                <span class="text-muted-foreground">Utilisateurs</span>
                <span class="font-medium">{{ role.usersCount }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-muted-foreground">Permissions</span>
                <span class="font-medium">{{ role.permissions.length }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-muted-foreground">Statut</span>
                <span class="inline-flex items-center gap-1.5 rounded-full px-2.5 py-0.5 text-xs font-medium"
                  [class.bg-green-100]="role.status === 'active'" [class.text-green-800]="role.status === 'active'"
                  [class.bg-yellow-100]="role.status === 'needs-review'" [class.text-yellow-800]="role.status === 'needs-review'"
                >
                  @if (role.status === 'needs-review') {
                    <span class="h-2 w-2 rounded-full bg-yellow-500 animate-pulse"></span>
                  }
                  {{ role.status === 'active' ? 'Actif' : 'À réviser' }}
                </span>
              </div>
            </div>
            <div class="mt-4 pt-4 border-t flex items-center gap-2">
              <button class="flex-1 rounded-md border px-3 py-1.5 text-xs font-medium hover:bg-muted">Modifier</button>
              <button class="flex-1 rounded-md border px-3 py-1.5 text-xs font-medium hover:bg-muted">Permissions</button>
            </div>
          </div>
        }
      </div>
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class RolesComponent {
  roles = signal<Role[]>([
    { id: '1', name: 'Administrateur', description: 'Accès complet à tous les modules', type: 'System', usersCount: 2, permissions: ['users.manage', 'clients.manage', 'interventions.manage', 'reports.export', 'settings.admin'], status: 'active' },
    { id: '2', name: 'Manager', description: 'Gestion des interventions, clients et équipe', type: 'System', usersCount: 3, permissions: ['clients.view', 'interventions.manage', 'reports.export', 'team.manage'], status: 'active' },
    { id: '3', name: 'Technicien', description: 'Créer et gérer ses propres interventions', type: 'System', usersCount: 12, permissions: ['interventions.create', 'interventions.own', 'clients.view'], status: 'active' },
    { id: '4', name: 'Portail Client', description: 'Accès pour soumettre des demandes', type: 'Custom', usersCount: 45, permissions: ['portal.submit', 'portal.view-own'], status: 'needs-review' },
    { id: '5', name: 'Auditeur', description: 'Accès en lecture seule pour la conformité', type: 'Custom', usersCount: 1, permissions: ['interventions.view', 'reports.view', 'audit.view'], status: 'needs-review' },
  ]);
}
