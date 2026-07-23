export type UserRole = 'ADMIN' | 'MANAGER' | 'TECHNICIAN' | 'CLIENT_ADMIN' | 'CLIENT_USER' | 'CLIENT_VIEWER';

export interface CreateUserRequest {
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  password?: string;
  role: UserRole;
  phone?: string;
}

export interface UpdateProfileRequest {
  firstName: string;
  lastName: string;
}

export interface RoleAssignRequest {
  role: UserRole;
}

export interface UserStatusRequest {
  enabled: boolean;
}

export interface UserResponse {
  id: string;
  keycloakId: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
  phone: string | null;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}
