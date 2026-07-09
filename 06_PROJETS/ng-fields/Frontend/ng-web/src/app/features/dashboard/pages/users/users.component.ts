import { Component, signal, computed, inject, ChangeDetectionStrategy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { IconComponent } from '../../../../shared/ui/icon/icon.component';
import { UserService } from '../../../../core/services/user.service';
import { UserResponse } from '../../../../shared/models/user.dto';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule, IconComponent],
  template: `
    <div class="flex flex-col gap-6 p-4 md:p-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold tracking-tight">Utilisateurs</h1>
          <p class="text-sm text-muted-foreground">Gérer les membres de l'organisation et leurs accès</p>
        </div>
        <button (click)="router.navigate(['/dashboard/users/new'])" class="inline-flex items-center gap-2 rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90">
          <app-icon name="circle-plus" />
          Ajouter
        </button>
      </div>

      <div class="flex flex-wrap items-center gap-3">
        <div class="relative">
          <span class="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground">
            <app-icon name="search" />
          </span>
          <input type="text" placeholder="Rechercher un utilisateur..."
            class="pl-9 pr-4 py-2 rounded-md border border-input bg-background text-sm w-64"
            [ngModel]="searchQuery()" (ngModelChange)="searchQuery.set($event)" />
        </div>
        <select class="rounded-md border border-input bg-background px-3 py-2 text-sm"
          [ngModel]="roleFilter()" (ngModelChange)="roleFilter.set($event)">
          <option value="all">Tous les rôles</option>
          <option value="ADMIN">Admin</option>
          <option value="MANAGER">Manager</option>
          <option value="TECHNICIAN">Technicien</option>
        </select>
        <select class="rounded-md border border-input bg-background px-3 py-2 text-sm"
          [ngModel]="statusFilter()" (ngModelChange)="statusFilter.set($event)">
          <option value="all">Tous les statuts</option>
          <option value="active">Actif</option>
          <option value="inactive">Inactif</option>
        </select>
        <span class="ml-auto text-sm text-muted-foreground">{{ filteredUsers().length }} utilisateur(s)</span>
      </div>

      <div class="rounded-lg border bg-card shadow-sm">
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="border-b bg-muted/50 text-left">
                <th class="px-6 py-3 text-xs font-medium text-muted-foreground uppercase">Utilisateur</th>
                <th class="px-6 py-3 text-xs font-medium text-muted-foreground uppercase">Rôle</th>
                <th class="px-6 py-3 text-xs font-medium text-muted-foreground uppercase">Statut</th>
                <th class="px-6 py-3 text-xs font-medium text-muted-foreground uppercase">Inscription</th>
                <th class="px-6 py-3 text-xs font-medium text-muted-foreground uppercase text-right">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y">
              @for (user of filteredUsers(); track user.id) {
                <tr class="hover:bg-muted/30 transition-colors">
                  <td class="px-6 py-4">
                    <div class="flex items-center gap-3">
                      <div class="flex h-10 w-10 items-center justify-center rounded-full bg-secondary font-medium text-sm">
                        {{ user.firstName.charAt(0) }}{{ user.lastName.charAt(0) }}
                      </div>
                      <div>
                        <p class="text-sm font-medium">{{ user.firstName }} {{ user.lastName }}</p>
                        <p class="text-xs text-muted-foreground">{{ user.email }}</p>
                      </div>
                    </div>
                  </td>
                  <td class="px-6 py-4">
                    <span class="inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium"
                      [class.bg-purple-100]="user.role === 'ADMIN'" [class.text-purple-800]="user.role === 'ADMIN'"
                      [class.bg-blue-100]="user.role === 'MANAGER'" [class.text-blue-800]="user.role === 'MANAGER'"
                      [class.bg-green-100]="user.role === 'TECHNICIAN'" [class.text-green-800]="user.role === 'TECHNICIAN'"
                    >
                      @if (user.role === 'ADMIN') { Administrateur }
                      @if (user.role === 'MANAGER') { Manager }
                      @if (user.role === 'TECHNICIAN') { Technicien }
                    </span>
                  </td>
                  <td class="px-6 py-4">
                    <span class="inline-flex items-center gap-1.5 rounded-full px-2.5 py-0.5 text-xs font-medium"
                      [class.bg-green-100]="user.active" [class.text-green-800]="user.active"
                      [class.bg-gray-100]="!user.active" [class.text-gray-800]="!user.active"
                    >
                      <span class="h-2 w-2 rounded-full" [class.bg-green-500]="user.active" [class.bg-gray-400]="!user.active"></span>
                      @if (user.active) { Actif }
                      @if (!user.active) { Inactif }
                    </span>
                  </td>
                  <td class="px-6 py-4 text-muted-foreground">{{ user.createdAt | date:'mediumDate' }}</td>
                  <td class="px-6 py-4 text-right">
                    <button class="rounded-md border px-3 py-1.5 text-xs font-medium hover:bg-muted">
                      Modifier
                    </button>
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
export class UsersComponent implements OnInit {
  protected router = inject(Router);
  private userService = inject(UserService);

  searchQuery = signal('');
  roleFilter = signal('all');
  statusFilter = signal('all');
  users = signal<UserResponse[]>([]);

  ngOnInit() {
    this.userService.getUsers().subscribe(res => this.users.set(res.content));
  }

  filteredUsers = computed(() => {
    let result = this.users();
    const query = this.searchQuery().toLowerCase();
    if (query) {
      const fullName = (u: UserResponse) => `${u.firstName} ${u.lastName}`;
      result = result.filter(u => fullName(u).toLowerCase().includes(query) || u.email.toLowerCase().includes(query));
    }
    if (this.roleFilter() !== 'all') {
      result = result.filter(u => u.role === this.roleFilter());
    }
    if (this.statusFilter() === 'active') {
      result = result.filter(u => u.active);
    } else if (this.statusFilter() === 'inactive') {
      result = result.filter(u => !u.active);
    }
    return result;
  });
}
