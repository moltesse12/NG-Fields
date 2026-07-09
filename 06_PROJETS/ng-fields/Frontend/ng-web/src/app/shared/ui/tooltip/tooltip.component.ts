import { Component, Directive, HostListener, Input, input, output, signal , ChangeDetectionStrategy } from '@angular/core';
import { OverlayModule } from '@angular/cdk/overlay';

@Directive({
  selector: '[appTooltipTrigger]',
  standalone: true,
})
export class TooltipTriggerDirective {
  readonly panelOpen = signal(false);

  @HostListener('mouseenter') onMouseEnter(): void {
    this.panelOpen.set(true);
  }
  @HostListener('mouseleave') onMouseLeave(): void {
    this.panelOpen.set(false);
  }
  @HostListener('focus') onFocus(): void {
    this.panelOpen.set(true);
  }
  @HostListener('blur') onBlur(): void {
    this.panelOpen.set(false);
  }
}

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: '[appTooltipContent]',
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
      <div class="z-50 overflow-hidden rounded-md border bg-popover px-3 py-1.5 text-sm text-popover-foreground shadow-md">
        <ng-content />
      </div>
    </ng-template>
  `,
  styleUrl: './tooltip.component.css',
})
export class TooltipContentComponent {
  readonly isOpen = input(false);
  readonly closed = output<void>();

  @Input() origin: any;
  positions = [
    { originX: 'center' as const, originY: 'top' as const, overlayX: 'center' as const, overlayY: 'bottom' as const },
    { originX: 'center' as const, originY: 'bottom' as const, overlayX: 'center' as const, overlayY: 'top' as const },
  ];

  close(): void {
    this.closed.emit();
  }
}
