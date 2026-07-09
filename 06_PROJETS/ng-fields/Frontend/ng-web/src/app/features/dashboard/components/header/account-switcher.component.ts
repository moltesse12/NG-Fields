import { Component, OnInit, inject, signal , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../../core/auth/auth.service';
import { map } from 'rxjs/operators';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-account-switcher',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './account-switcher.component.html',
  styleUrl: './account-switcher.component.css',
})
export class AccountSwitcherComponent implements OnInit {
  private auth = inject(AuthService);
  user: { name: string; email: string } = { name: 'Chargement...', email: '' };
  isOpen = signal(false);

  menuItems = [
    { id: 'profile', label: 'Profil', url: '/dashboard/profile' },
    { id: 'settings', label: 'Paramètres', url: '/dashboard/settings' },
    { id: 'sep1', label: '', separator: true },
    { id: 'logout', label: 'Déconnexion', onClick: () => this.auth.logout() },
  ];

  ngOnInit(): void {
    this.auth.userData$.subscribe(data => {
      const ud = data?.userData || data;
      if (ud?.preferred_username || ud?.name) {
        this.user = { name: ud.preferred_username || ud.name, email: ud.email || '' };
      }
    });
  }

  toggle(): void { this.isOpen.update(v => !v); }
  close(): void { this.isOpen.set(false); }
  handleAction(item: { onClick?: () => void; url?: string }): void {
    this.isOpen.set(false);
    item.onClick?.();
  }
}
