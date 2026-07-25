import { Component, signal, ChangeDetectionStrategy, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IconComponent } from '../../../../shared/ui/icon/icon.component';
import { UserService } from '../../../../core/services/user.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-settings',
  standalone: true,
  imports: [FormsModule, IconComponent],
  template: `
    <div class="flex flex-col gap-6 p-4 md:p-6">
      <div>
        <h1 class="text-3xl font-bold tracking-tight">Paramètres</h1>
        <p class="text-sm text-muted-foreground">Gérer vos préférences et la configuration de l'application</p>
      </div>

      <div class="grid gap-6 md:grid-cols-[200px_1fr]">
        <aside class="flex flex-col gap-1">
          @for (tab of tabs(); track tab.id) {
            <button
              class="flex items-center gap-2 rounded-md px-3 py-2 text-sm font-medium transition-colors text-left"
              [class.bg-muted]="activeTab() === tab.id"
              [class.text-foreground]="activeTab() === tab.id"
              [class.text-muted-foreground]="activeTab() !== tab.id"
              [class.hover:bg-muted/50]="activeTab() !== tab.id"
              (click)="activeTab.set(tab.id)"
            >
              <app-icon [name]="tab.icon" />
              {{ tab.label }}
            </button>
          }
        </aside>

        <div class="flex-1">
          @switch (activeTab()) {
            @case ('profile') {
              <div class="rounded-lg border bg-card p-6">
                <h3 class="text-lg font-semibold mb-1">Profil</h3>
                <p class="text-sm text-muted-foreground mb-6">Mettre à jour vos informations personnelles</p>
                @if (profileSaveSuccess()) {
                  <div class="mb-4 rounded-md bg-green-50 p-3 text-sm text-green-700">Profil mis à jour avec succès</div>
                }
                @if (profileSaveError()) {
                  <div class="mb-4 rounded-md bg-red-50 p-3 text-sm text-red-700">{{ profileSaveError() }}</div>
                }
                <div class="space-y-4">
                  <div class="grid gap-2">
                    <label class="text-sm font-medium">Prénom</label>
                    <input type="text" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                      [ngModel]="profile().firstName" (ngModelChange)="profile.update(p => ({ ...p, firstName: $event }))" />
                  </div>
                  <div class="grid gap-2">
                    <label class="text-sm font-medium">Nom</label>
                    <input type="text" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                      [ngModel]="profile().lastName" (ngModelChange)="profile.update(p => ({ ...p, lastName: $event }))" />
                  </div>
                  <div class="grid gap-2">
                    <label class="text-sm font-medium">Email</label>
                    <input type="email" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                      [ngModel]="profile().email" disabled />
                  </div>
                  <div class="grid gap-2">
                    <label class="text-sm font-medium">Téléphone</label>
                    <input type="tel" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                      [ngModel]="profile().phone" (ngModelChange)="profile.update(p => ({ ...p, phone: $event }))" />
                  </div>
                  <button class="rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90"
                    (click)="saveProfile()" [disabled]="profileSaving()">
                    {{ profileSaving() ? 'Enregistrement...' : 'Enregistrer' }}
                  </button>
                </div>
              </div>
            }
            @case ('notifications') {
              <div class="rounded-lg border bg-card p-6">
                <h3 class="text-lg font-semibold mb-1">Notifications</h3>
                <p class="text-sm text-muted-foreground mb-6">Configurer la réception des alertes</p>
                <div class="space-y-4">
                  @for (notif of notifications(); track notif.id) {
                    <div class="flex items-center justify-between">
                      <div>
                        <p class="text-sm font-medium">{{ notif.label }}</p>
                        <p class="text-xs text-muted-foreground">{{ notif.description }}</p>
                      </div>
                      <label class="relative inline-flex items-center cursor-pointer">
                        <input type="checkbox" class="sr-only peer" [checked]="notif.enabled" (change)="toggleNotification(notif.id)" />
                        <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-primary/20 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary"></div>
                      </label>
                    </div>
                  }
                </div>
              </div>
            }
            @case ('security') {
              <div class="rounded-lg border bg-card p-6">
                <h3 class="text-lg font-semibold mb-1">Sécurité</h3>
                <p class="text-sm text-muted-foreground mb-6">Gérer votre mot de passe et l'authentification</p>
                <div class="space-y-6">
                  <div>
                    <h4 class="text-sm font-medium mb-3">Changer le mot de passe</h4>
                    @if (passwordSaveSuccess()) {
                      <div class="mb-3 rounded-md bg-green-50 p-3 text-sm text-green-700">Mot de passe mis à jour avec succès</div>
                    }
                    @if (passwordSaveError()) {
                      <div class="mb-3 rounded-md bg-red-50 p-3 text-sm text-red-700">{{ passwordSaveError() }}</div>
                    }
                    <div class="space-y-3">
                      <input type="password" placeholder="Mot de passe actuel" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                        [(ngModel)]="currentPassword" />
                      <input type="password" placeholder="Nouveau mot de passe" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                        [(ngModel)]="newPassword" />
                      <input type="password" placeholder="Confirmer le mot de passe" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                        [(ngModel)]="confirmPassword" />
                      <button class="rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90"
                        (click)="changePassword()" [disabled]="passwordSaving()">
                        {{ passwordSaving() ? 'Mise à jour...' : 'Mettre à jour' }}
                      </button>
                    </div>
                  </div>
                  <div class="border-t pt-6">
                    <h4 class="text-sm font-medium mb-3">Authentification à deux facteurs</h4>
                    <p class="text-xs text-muted-foreground mb-3">Ajouter une couche de sécurité supplémentaire</p>
                    <button class="inline-flex items-center gap-2 rounded-md border px-4 py-2 text-sm font-medium hover:bg-muted">
                      <app-icon name="lock" />
                      Activer 2FA
                    </button>
                  </div>
                </div>
              </div>
            }
            @case ('appearance') {
              <div class="rounded-lg border bg-card p-6">
                <h3 class="text-lg font-semibold mb-1">Apparence</h3>
                <p class="text-sm text-muted-foreground mb-6">Personnaliser l'affichage de l'application</p>
                <div>
                  <h4 class="text-sm font-medium mb-3">Thème</h4>
                  <div class="grid grid-cols-3 gap-3">
                    @for (theme of themes(); track theme.id) {
                      <button class="flex flex-col items-center gap-2 rounded-lg border p-4 transition-colors"
                        [class.border-primary]="activeTheme() === theme.id"
                        [class.bg-primary/5]="activeTheme() === theme.id"
                        [class.hover:bg-muted/50]="activeTheme() !== theme.id"
                        (click)="activeTheme.set(theme.id)"
                      >
                        <div class="h-12 w-12 rounded-full" [class]="theme.preview"></div>
                        <span class="text-sm font-medium">{{ theme.name }}</span>
                      </button>
                    }
                  </div>
                </div>
              </div>
            }
          }
        </div>
      </div>
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class SettingsComponent implements OnInit {
  private userService = inject(UserService);

  activeTab = signal('profile');
  activeTheme = signal('light');

  tabs = signal([
    { id: 'profile', label: 'Profil', icon: 'circle-user' },
    { id: 'notifications', label: 'Notifications', icon: 'message-square-dot' },
    { id: 'security', label: 'Sécurité', icon: 'lock' },
    { id: 'appearance', label: 'Apparence', icon: 'settings' },
  ]);

  profile = signal({ firstName: '', lastName: '', email: '', phone: '' });
  profileSaving = signal(false);
  profileSaveSuccess = signal(false);
  profileSaveError = signal('');

  currentPassword = '';
  newPassword = '';
  confirmPassword = '';
  passwordSaving = signal(false);
  passwordSaveSuccess = signal(false);
  passwordSaveError = signal('');

  notifications = signal([
    { id: '1', label: 'Notifications Email', description: 'Recevoir les alertes par email', enabled: true },
    { id: '2', label: 'Notifications Push', description: 'Recevoir les alertes sur mobile', enabled: true },
    { id: '3', label: 'Nouvelle intervention', description: 'Quand une intervention est créée', enabled: true },
    { id: '4', label: 'Intervention terminée', description: 'Quand une intervention est clôturée', enabled: false },
    { id: '5', label: 'Dépassement SLA', description: 'Quand le seuil de durée est dépassé', enabled: true },
  ]);

  themes = signal([
    { id: 'light', name: 'Clair', preview: 'bg-white border-2 border-gray-200' },
    { id: 'dark', name: 'Sombre', preview: 'bg-gray-900 border-2 border-gray-700' },
    { id: 'system', name: 'Système', preview: 'bg-gradient-to-r from-white to-gray-900 border-2 border-gray-400' },
  ]);

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.userService.getMe().subscribe({
      next: (user) => {
        this.profile.set({
          firstName: user.firstName,
          lastName: user.lastName,
          email: user.email,
          phone: user.phone ?? '',
        });
      },
      error: () => {},
    });
  }

  saveProfile(): void {
    this.profileSaving.set(true);
    this.profileSaveSuccess.set(false);
    this.profileSaveError.set('');

    const p = this.profile();
    this.userService.updateMe({ firstName: p.firstName, lastName: p.lastName }).subscribe({
      next: () => {
        this.profileSaving.set(false);
        this.profileSaveSuccess.set(true);
        setTimeout(() => this.profileSaveSuccess.set(false), 3000);
      },
      error: (err) => {
        this.profileSaving.set(false);
        this.profileSaveError.set(err.detail || 'Erreur lors de la mise à jour');
      },
    });
  }

  changePassword(): void {
    this.passwordSaving.set(true);
    this.passwordSaveSuccess.set(false);
    this.passwordSaveError.set('');

    if (this.newPassword !== this.confirmPassword) {
      this.passwordSaving.set(false);
      this.passwordSaveError.set('Les mots de passe ne correspondent pas');
      return;
    }

    if (this.newPassword.length < 8) {
      this.passwordSaving.set(false);
      this.passwordSaveError.set('Le mot de passe doit contenir au moins 8 caractères');
      return;
    }

    this.userService.changePassword(this.currentPassword, this.newPassword).subscribe({
      next: () => {
        this.passwordSaving.set(false);
        this.passwordSaveSuccess.set(true);
        this.currentPassword = '';
        this.newPassword = '';
        this.confirmPassword = '';
        setTimeout(() => this.passwordSaveSuccess.set(false), 3000);
      },
      error: (err) => {
        this.passwordSaving.set(false);
        this.passwordSaveError.set(err.detail || 'Erreur lors du changement de mot de passe');
      },
    });
  }

  toggleNotification(id: string): void {
    this.notifications.update(notifs => notifs.map(n => n.id === id ? { ...n, enabled: !n.enabled } : n));
  }
}
