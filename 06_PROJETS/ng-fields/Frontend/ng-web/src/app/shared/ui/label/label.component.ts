import { Component , ChangeDetectionStrategy } from '@angular/core';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'label[appLabel]',
  standalone: true,
  template: '<ng-content />',
  styleUrl: './label.component.css',
  host: {
    class: 'text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70',
  },
})
export class LabelComponent {}
