import { Component, input, output, HostBinding, HostListener , ChangeDetectionStrategy } from '@angular/core';
import { NgClass } from '@angular/common';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'span[appCheckbox]',
  standalone: true,
  imports: [NgClass],
  template: `
    <span
      class="flex h-4 w-4 items-center justify-center"
      [ngClass]="{
        'bg-primary text-primary-foreground': checked(),
        'bg-transparent': !checked()
      }"
    >
      @if (checked()) {
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="h-3 w-3">
          <polyline points="20 6 9 17 4 12" />
        </svg>
      }
    </span>
  `,
  styleUrl: './checkbox.component.css',
})
export class CheckboxComponent {
  readonly checked = input(false);
  readonly indeterminate = input(false);
  readonly disabled = input(false);
  readonly checkedChange = output<boolean>();

  @HostBinding('class')
  get hostClass(): string {
    return 'peer h-4 w-4 shrink-0 rounded-sm border border-primary ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50';
  }

  @HostBinding('attr.role') role = 'checkbox';
  @HostBinding('attr.aria-checked')
  get ariaChecked(): boolean | string {
    if (this.indeterminate()) return 'mixed';
    return this.checked();
  }
  @HostBinding('attr.aria-disabled')
  get ariaDisabled(): boolean {
    return this.disabled();
  }
  @HostBinding('attr.tabindex') tabindex = '0';
  @HostBinding('attr.data-state')
  get dataState(): string {
    if (this.indeterminate()) return 'indeterminate';
    return this.checked() ? 'checked' : 'unchecked';
  }

  toggle(): void {
    if (!this.disabled()) {
      this.checkedChange.emit(!this.checked());
    }
  }

  @HostListener('click')
  onClick(): void {
    this.toggle();
  }

  @HostListener('keydown.enter', ['$event'])
  @HostListener('keydown.space', ['$event'])
  onKeydown(event: Event): void {
    event.preventDefault();
    this.toggle();
  }
}
