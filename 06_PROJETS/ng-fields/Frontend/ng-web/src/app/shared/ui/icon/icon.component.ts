import { Component, input, ViewContainerRef, effect, inject , ChangeDetectionStrategy } from '@angular/core';
import {
  LucideLayoutDashboard, LucideChartBar, LucideBanknote, LucideGauge, LucideListTodo,
  LucideShoppingBag, LucideGraduationCap, LucideForklift, LucideServer, LucideWrench,
  LucideUsers, LucideUserCog, LucideDownload, LucideLock, LucideSettings, LucideMail,
  LucideMessageSquare, LucideCalendar, LucideKanban, LucideSquareCheck, LucideReceiptText,
  LucideSquareArrowUpRight, LucideCirclePlus, LucidePanelLeft, LucideChevronRight,
  LucideCircleUser, LucideCreditCard, LucideLogOut, LucideEllipsisVertical, LucideMessageSquareDot,
  LucideSearch, LucideTrendingUp, LucideAlertTriangle, LucideMapPin, LucideClock,
  LucideChevronLeft, LucideCheck,
} from '@lucide/angular';

const iconMap: Record<string, any> = {
  'layout-dashboard': LucideLayoutDashboard,
  'chart-bar': LucideChartBar,
  'banknote': LucideBanknote,
  'gauge': LucideGauge,
  'list-todo': LucideListTodo,
  'shopping-bag': LucideShoppingBag,
  'graduation-cap': LucideGraduationCap,
  'forklift': LucideForklift,
  'server': LucideServer,
  'wrench': LucideWrench,
  'users': LucideUsers,
  'user-cog': LucideUserCog,
  'download': LucideDownload,
  'lock': LucideLock,
  'settings': LucideSettings,
  'mail': LucideMail,
  'message-square': LucideMessageSquare,
  'calendar': LucideCalendar,
  'kanban': LucideKanban,
  'check-square': LucideSquareCheck,
  'receipt-text': LucideReceiptText,
  'square-arrow-up-right': LucideSquareArrowUpRight,
  'circle-plus': LucideCirclePlus,
  'panel-left': LucidePanelLeft,
  'chevron-right': LucideChevronRight,
  'circle-user': LucideCircleUser,
  'credit-card': LucideCreditCard,
  'log-out': LucideLogOut,
  'ellipsis-vertical': LucideEllipsisVertical,
  'message-square-dot': LucideMessageSquareDot,
  'search': LucideSearch,
  'trending-up': LucideTrendingUp,
  'alert-triangle': LucideAlertTriangle,
  'map-pin': LucideMapPin,
  'clock': LucideClock,
  'chevron-left': LucideChevronLeft,
  'check': LucideCheck,
};

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-icon',
  standalone: true,
  template: '',
})
export class IconComponent {
  name = input.required<string>();
  size = input<number | string>(16);
  strokeWidth = input<number | string>(2);

  private vcr = inject(ViewContainerRef);

  constructor() {
    effect(() => {
      this.vcr.clear();
      const compClass = iconMap[this.name()];
      if (compClass) {
        const ref = this.vcr.createComponent(compClass);
        ref.setInput('size', this.size());
        ref.setInput('strokeWidth', this.strokeWidth());
      }
    });
  }
}
