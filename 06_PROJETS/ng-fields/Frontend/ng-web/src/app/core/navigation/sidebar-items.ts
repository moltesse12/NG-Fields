export interface NavSubItem {
  id: string;
  title: string;
  url: string;
  icon?: string;
  badge?: 'new' | 'soon';
  disabled?: boolean;
  newTab?: boolean;
}

export interface NavMainItem {
  id: string;
  title: string;
  url?: string;
  icon?: string;
  badge?: 'new' | 'soon';
  disabled?: boolean;
  newTab?: boolean;
  subItems?: NavSubItem[];
}

export interface NavGroup {
  id: number;
  label?: string;
  items: NavMainItem[];
}

export const sidebarItems: NavGroup[] = [
  {
    id: 1,
    label: 'Dashboard',
    items: [
      { id: 'overview', title: 'Overview', url: '/dashboard/overview', icon: 'layout-dashboard' },
    ],
  },
  {
    id: 2,
    label: 'Field Operations',
    items: [
      { id: 'interventions', title: 'Interventions', url: '/dashboard/interventions', icon: 'wrench' },
      { id: 'clients', title: 'Clients', url: '/dashboard/clients', icon: 'users' },
      { id: 'technicians', title: 'Technicians', url: '/dashboard/technicians', icon: 'user-cog' },
    ],
  },
  {
    id: 3,
    label: 'Reports',
    items: [
      { id: 'analytics-reports', title: 'Analytics', url: '/dashboard/analytics', icon: 'chart-bar' },
      { id: 'exports', title: 'Exports', url: '/dashboard/exports', icon: 'download' },
    ],
  },
  {
    id: 4,
    label: 'Administration',
    items: [
      { id: 'admin-users', title: 'Users', url: '/dashboard/users', icon: 'users' },
      { id: 'admin-roles', title: 'Roles', url: '/dashboard/roles', icon: 'lock' },
      { id: 'admin-settings', title: 'Settings', url: '/dashboard/settings', icon: 'settings' },
    ],
  },
  {
    id: 5,
    label: 'Pages',
    items: [
      { id: 'email', title: 'Email', url: '/dashboard/mail', icon: 'mail', disabled: true },
      { id: 'chat', title: 'Chat', url: '/dashboard/chat', icon: 'message-square', disabled: true },
      { id: 'page-calendar', title: 'Calendar', url: '/dashboard/calendar', icon: 'calendar', disabled: true },
      { id: 'page-kanban', title: 'Kanban', url: '/dashboard/kanban', icon: 'kanban', disabled: true },
      { id: 'page-tasks', title: 'Tasks', url: '/dashboard/tasks', icon: 'check-square', disabled: true },
      { id: 'page-users', title: 'Users', url: '/dashboard/users', icon: 'users', disabled: true },
      { id: 'page-roles', title: 'Roles', url: '/dashboard/roles', icon: 'lock', disabled: true },
    ],
  },
];

export const users = [
  {
    id: '1',
    name: 'Admin NG-Fields',
    email: 'admin@ng-fields.ngs.tg',
    avatar: '',
    role: 'administrator',
  },
];
