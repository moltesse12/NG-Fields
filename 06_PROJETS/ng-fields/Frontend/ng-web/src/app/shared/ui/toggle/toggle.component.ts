import { Component, Directive, input, HostBinding , ChangeDetectionStrategy } from '@angular/core';

export type ToggleVariant = 'default' | 'outline';
export type ToggleSize = 'default' | 'sm';

const variantClasses: Record<ToggleVariant, string> = {
  default: 'bg-transparent hover:bg-muted hover:text-muted-foreground',
  outline: 'border border-input bg-transparent hover:bg-accent hover:text-accent-foreground',
};

const sizeClasses: Record<ToggleSize, string> = {
  default: 'h-10 px-3',
  sm: 'h-9 px-2.5',
};

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'button[appToggle]',
  standalone: true,
  template: '<ng-content />',
  styleUrl: './toggle.component.css',
})
export class ToggleComponent {
  readonly variant = input<ToggleVariant>('default');
  readonly size = input<ToggleSize>('default');
  readonly pressed = input(false);

  @HostBinding('class')
  get hostClass(): string {
    const base = 'inline-flex items-center justify-center gap-2 rounded-md text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg]:size-4 [&_svg]:shrink-0';
    return `${base} ${variantClasses[this.variant()]} ${sizeClasses[this.size()]}`;
  }

  @HostBinding('attr.data-state')
  get dataState(): string {
    return this.pressed() ? 'on' : 'off';
  }
}

@Directive({
  selector: '[appToggleGroup]',
  standalone: true,
  host: {
    class: 'flex items-center gap-1',
  },
})
export class ToggleGroupDirective {}
