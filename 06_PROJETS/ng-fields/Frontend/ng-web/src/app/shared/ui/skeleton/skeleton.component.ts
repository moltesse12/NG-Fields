import { Component , ChangeDetectionStrategy } from '@angular/core';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'span[appSkeleton]',
  standalone: true,
  template: '',
  styleUrl: './skeleton.component.css',
  host: {
    class: 'animate-pulse rounded-md bg-muted',
  },
})
export class SkeletonComponent {}
