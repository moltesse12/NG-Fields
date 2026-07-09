import { Component, input, HostBinding , ChangeDetectionStrategy } from '@angular/core';

export type BadgeVariant = 'default' | 'secondary' | 'outline' | 'destructive';

const variantClasses: Record<BadgeVariant, string> = {
  default: 'border-transparent bg-primary text-primary-foreground shadow-xs hover:bg-primary/80',
  secondary: 'border-transparent bg-secondary text-secondary-foreground hover:bg-secondary/80',
  outline: 'text-foreground',
  destructive: 'border-transparent bg-destructive text-destructive-foreground shadow-xs hover:bg-destructive/80',
};

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'span[appBadge]',
  standalone: true,
  template: '<ng-content />',
  styleUrl: './badge.component.css',
})
export class BadgeComponent {
  readonly variant = input<BadgeVariant>('default');

  @HostBinding('class')
  get hostClass(): string {
    const base = 'inline-flex items-center rounded-md border px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2';
    return `${base} ${variantClasses[this.variant()]}`;
  }
}
