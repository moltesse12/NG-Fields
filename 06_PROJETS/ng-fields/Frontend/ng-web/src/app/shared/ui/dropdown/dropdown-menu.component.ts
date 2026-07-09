import { Component, input, output, signal , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-dropdown-menu',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="relative inline-block">
      <div (click)="toggle()" class="cursor-pointer"><ng-content select="[trigger]"></ng-content></div>
      @if (isOpen()) {
        <div class="absolute right-0 top-full z-50 mt-1 min-w-[200px] rounded-lg border bg-popover p-1 shadow-md">
          @for (item of items(); track item.id) {
            @if (item.separator) {
              <div class="my-1 border-t"></div>
            } @else if (item.url) {
              <a [routerLink]="item.url" (click)="close(); item.onClick?.()" class="flex items-center gap-2 rounded-md px-2 py-1.5 text-sm hover:bg-accent cursor-pointer">
                {{ item.label }}
              </a>
            } @else {
              <div (click)="close(); item.onClick?.()" class="flex items-center gap-2 rounded-md px-2 py-1.5 text-sm hover:bg-accent cursor-pointer">
                {{ item.label }}
              </div>
            }
          }
          <ng-content></ng-content>
        </div>
      }
    </div>
  `,
  styles: [':host { display: inline-block; }'],
})
export class DropdownMenuComponent {
  items = input<{ id: string; label: string; url?: string; separator?: boolean; onClick?: () => void }[]>([]);
  isOpen = signal(false);
  toggle(): void { this.isOpen.update(v => !v); }
  close(): void { this.isOpen.set(false); }
}
