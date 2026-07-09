import { Component, inject, signal , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ThemeService, ThemeMode, ThemePreset } from '../../../../core/theme/theme.service';
import { SidebarService, SidebarLayoutMode, SidebarCollapsibleMode } from '../../../../core/sidebar/sidebar.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-layout-controls',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './layout-controls.component.html',
  styleUrl: './layout-controls.component.css',
})
export class LayoutControlsComponent {
  private theme = inject(ThemeService);
  private sidebar = inject(SidebarService);
  isOpen = signal(false);

  presets: ThemePreset[] = ['default'];
  modes: { value: ThemeMode; label: string }[] = [
    { value: 'light', label: 'Light' },
    { value: 'dark', label: 'Dark' },
    { value: 'system', label: 'System' },
  ];

  sidebarVariants: { value: SidebarLayoutMode; label: string }[] = [
    { value: 'sidebar', label: 'Sidebar' },
    { value: 'inset', label: 'Inset' },
    { value: 'floating', label: 'Floating' },
  ];

  sidebarCollapsibles: { value: SidebarCollapsibleMode; label: string }[] = [
    { value: 'icon', label: 'Icon' },
    { value: 'offcanvas', label: 'Offcanvas' },
    { value: 'none', label: 'None' },
  ];

  fonts = [
    { value: 'system-ui, sans-serif', label: 'System' },
    { value: 'Inter, sans-serif', label: 'Inter' },
    { value: 'Georgia, serif', label: 'Serif' },
    { value: 'monospace', label: 'Mono' },
  ];

  get currentMode(): ThemeMode { return this.theme.currentMode; }
  get currentPreset(): ThemePreset { return this.theme.currentPreset; }
  get layoutMode() { return this.sidebar.layoutMode; }
  get collapsibleMode() { return this.sidebar.collapsibleMode; }

  setMode(mode: ThemeMode): void { this.theme.setThemeMode(mode); }
  setPreset(preset: ThemePreset): void { this.theme.setThemePreset(preset); }
  setLayoutMode(mode: SidebarLayoutMode): void { this.sidebar.setLayoutMode(mode); }
  setCollapsibleMode(mode: SidebarCollapsibleMode): void { this.sidebar.setCollapsibleMode(mode); }

  setFont(font: string): void {
    document.documentElement.style.fontFamily = font;
  }

  toggle(): void { this.isOpen.update(v => !v); }
  close(): void { this.isOpen.set(false); }
}
