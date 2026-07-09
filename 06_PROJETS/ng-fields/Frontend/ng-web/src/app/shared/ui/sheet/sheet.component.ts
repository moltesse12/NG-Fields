import { Component, Directive, Input, input, output, signal , ChangeDetectionStrategy } from '@angular/core';
import { OverlayModule } from '@angular/cdk/overlay';
import { NgClass } from '@angular/common';

@Directive({
  selector: '[appSheetTrigger]',
  standalone: true,
})
export class SheetTriggerDirective {
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
  selector: '[appSheetContent]',
  standalone: true,
  imports: [OverlayModule, NgClass],
  template: `
    <ng-template
      cdk-connected-overlay
      [cdkConnectedOverlayOpen]="isOpen()"
      [cdkConnectedOverlayOrigin]="origin"
      [cdkConnectedOverlayPositions]="positions"
      (overlayOutsideClick)="close()"
      (detach)="close()"
    >
      <div
        class="fixed inset-y-0 z-50 h-full w-72 border-l bg-background p-6 shadow-lg"
        [ngClass]="side() === 'left' ? 'left-0 border-r' : 'right-0 border-l'"
      >
        <ng-content />
      </div>
    </ng-template>
  `,
  styleUrl: './sheet.component.css',
})
export class SheetContentComponent {
  readonly isOpen = input(false);
  readonly side = input<'left' | 'right'>('right');
  readonly closed = output<void>();

  @Input() origin: any;
  positions = [
    { originX: 'start' as const, originY: 'bottom' as const, overlayX: 'start' as const, overlayY: 'top' as const },
  ];

  close(): void {
    this.closed.emit();
  }
}

@Directive({
  selector: '[appSheetHeader]',
  standalone: true,
  host: {
    class: 'flex flex-col space-y-2 text-center sm:text-left',
  },
})
export class SheetHeaderDirective {}

@Directive({
  selector: '[appSheetTitle]',
  standalone: true,
  host: {
    class: 'text-lg font-semibold text-foreground',
  },
})
export class SheetTitleDirective {}

@Directive({
  selector: '[appSheetDescription]',
  standalone: true,
  host: {
    class: 'text-sm text-muted-foreground',
  },
})
export class SheetDescriptionDirective {}

@Directive({
  selector: '[appSheetClose]',
  standalone: true,
  host: {
    class: 'absolute right-4 top-4 rounded-sm opacity-70 ring-offset-background transition-opacity hover:opacity-100 focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2',
  },
})
export class SheetCloseDirective {}
