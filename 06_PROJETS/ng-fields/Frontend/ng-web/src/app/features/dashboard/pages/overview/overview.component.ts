import { Component , ChangeDetectionStrategy } from '@angular/core';
import { SectionCardsComponent } from './components/section-cards.component';
import { ChartAreaInteractiveComponent } from './components/chart-area-interactive.component';
import { ProposalSectionsTableComponent } from './components/proposal-sections-table/table.component';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-overview',
  standalone: true,
  imports: [SectionCardsComponent, ChartAreaInteractiveComponent, ProposalSectionsTableComponent],
  templateUrl: './overview.component.html',
  styleUrl: './overview.component.css',
})
export class OverviewComponent {}
