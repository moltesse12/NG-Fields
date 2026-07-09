import { Component, Directive, input , ChangeDetectionStrategy } from '@angular/core';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'select[appSelectTrigger]',
  standalone: true,
  template: '',
  styleUrl: './select.component.css',
  host: {
    class: 'flex h-10 w-full items-center justify-between rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 [&>span]:line-clamp-1',
  },
})
export class SelectTriggerComponent {}

@Directive({
  selector: '[appSelectValue]',
  standalone: true,
})
export class SelectValueDirective {}

@Directive({
  selector: '[appSelectContent]',
  standalone: true,
  host: {
    class: 'relative z-50 max-h-96 min-w-32 overflow-hidden rounded-md border bg-popover text-popover-foreground shadow-md',
  },
})
export class SelectContentDirective {}

@Directive({
  selector: '[appSelectGroup]',
  standalone: true,
})
export class SelectGroupDirective {}

@Directive({
  selector: 'option[appSelectItem]',
  standalone: true,
  host: {
    class: 'relative flex w-full cursor-default select-none items-center rounded-sm py-1.5 pl-8 pr-2 text-sm outline-none focus:bg-accent focus:text-accent-foreground data-[disabled]:pointer-events-none data-[disabled]:opacity-50',
  },
})
export class SelectItemDirective {}

@Directive({
  selector: '[appSelectLabel]',
  standalone: true,
  host: {
    class: 'py-1.5 pl-8 pr-2 text-sm font-semibold',
  },
})
export class SelectLabelDirective {}
