import { Component , ChangeDetectionStrategy } from '@angular/core';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-calendar',
  standalone: true,
  template: `<div class="flex flex-col items-center justify-center h-full text-muted-foreground"><h1 class="text-2xl font-semibold">Calendar</h1><p class="text-sm">Coming soon</p></div>`,
})
export class CalendarComponent {}
