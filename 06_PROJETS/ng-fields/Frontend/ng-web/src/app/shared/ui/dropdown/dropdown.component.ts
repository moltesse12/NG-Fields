import { Component, Directive, Input, input, output, signal , ChangeDetectionStrategy } from '@angular/core';
import { OverlayModule } from '@angular/cdk/overlay';

@Directive({
  selector: '[appDropdownTrigger]',
  standalone: true,
})
export class DropdownTriggerDirective {
  readonly panelOpen = signal(false);
  readonly toggle = output<boolean>();

  togglePanel(): void {
    this.panelOpen.update(v => !v);
    this.toggle.emit(this.panelOpen());
  }

  open(): void {
    this.panelOpen.set(true);
    this.toggle.emit(true);
  }

  close(): void {
    this.panelOpen.set(false);
    this.toggle.emit(false);
  }
}

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: '[appDropdownContent]',
  standalone: true,
  imports: [OverlayModule],
  template: `
    <ng-template
      cdk-connected-overlay
      [cdkConnectedOverlayOpen]="isOpen()"
      [cdkConnectedOverlayOrigin]="origin"
      [cdkConnectedOverlayPositions]="positions"
      (overlayOutsideClick)="close()"
      (detach)="close()"
    >
      <div class="z-50 min-w-48 rounded-lg border bg-popover p-1 text-popover-foreground shadow-md">
        <ng-content />
      </div>
    </ng-template>
  `,
  styleUrl: './dropdown.component.css',
})
export class DropdownContentComponent {
  readonly isOpen = input(false);
  readonly closed = output<void>();

  @Input() origin: any;
  positions = [
    { originX: 'start' as const, originY: 'bottom' as const, overlayX: 'start' as const, overlayY: 'top' as const },
    { originX: 'start' as const, originY: 'top' as const, overlayX: 'start' as const, overlayY: 'bottom' as const },
  ];

  close(): void {
    this.closed.emit();
  }
}

@Directive({
  selector: '[appDropdownItem]',
  standalone: true,
  host: {
    class: 'relative flex cursor-default select-none items-center gap-2 rounded-sm px-2 py-1.5 text-sm outline-none transition-colors focus:bg-accent focus:text-accent-foreground data-[disabled]:pointer-events-none data-[disabled]:opacity-50 [&>svg]:size-4 [&>svg]:shrink-0',
  },
})
export class DropdownItemDirective {}

@Directive({
  selector: '[appDropdownSeparator]',
  standalone: true,
  host: {
    class: '-mx-1 my-1 h-px bg-border',
  },
})
export class DropdownSeparatorDirective {}

@Directive({
  selector: '[appDropdownGroup]',
  standalone: true,
})
export class DropdownGroupDirective {}

@Directive({
  selector: '[appDropdownLabel]',
  standalone: true,
  host: {
    class: 'px-2 py-1.5 text-sm font-semibold',
  },
})
export class DropdownLabelDirective {}
