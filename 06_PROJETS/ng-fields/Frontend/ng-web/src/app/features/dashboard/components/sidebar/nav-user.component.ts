import { Component, OnInit, inject, signal, HostListener, ElementRef , ChangeDetectionStrategy } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../../core/auth/auth.service';
import { IconComponent } from '../../../../shared/ui/icon/icon.component';
import { SidebarService } from '../../../../core/sidebar/sidebar.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-nav-user',
  standalone: true,
  imports: [RouterModule, IconComponent],
  templateUrl: './nav-user.component.html',
  styleUrl: './nav-user.component.css',
})
export class NavUserComponent implements OnInit {
  private auth = inject(AuthService);
  sidebar = inject(SidebarService);
  private el = inject(ElementRef);
  user: { name: string; email: string } = { name: 'Chargement...', email: '' };
  isOpen = signal(false);

  menuItems = [
    { id: 'account', label: 'Compte', url: '/dashboard/account', icon: 'circle-user' },
    { id: 'notifications', label: 'Notifications', url: '/dashboard/notifications', icon: 'message-square-dot' },
    { id: 'sep1', label: '', separator: true },
    { id: 'logout', label: 'Déconnexion', icon: 'log-out', onClick: () => this.auth.logout() },
  ];

  ngOnInit(): void {
    this.auth.userData$.subscribe(data => {
      const ud = data?.userData || data;
      if (ud?.preferred_username || ud?.name) {
        this.user = { name: ud.preferred_username || ud.name, email: ud.email || '' };
      }
    });
  }

  getInitials(name: string): string {
    return name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();
  }

  toggle(): void {
    this.isOpen.update(v => !v);
  }

  close(): void {
    this.isOpen.set(false);
  }

  @HostListener('document:mousedown', ['$event'])
  onGlobalClick(event: MouseEvent): void {
    if (!this.el.nativeElement.contains(event.target)) {
      this.isOpen.set(false);
    }
  }
}
