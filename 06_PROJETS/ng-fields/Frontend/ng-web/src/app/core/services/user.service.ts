import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../api/api.service';
import { Page } from '../../shared/models/page';
import {
  CreateUserRequest, UserResponse, RoleAssignRequest, UserStatusRequest, UpdateProfileRequest,
} from '../../shared/models/user.dto';

@Injectable({ providedIn: 'root' })
export class UserService {
  private api = inject(ApiService);

  getUsers(): Observable<Page<UserResponse>> {
    return this.api.get<Page<UserResponse>>('/admin/users');
  }

  getUser(id: string): Observable<UserResponse> {
    return this.api.get<UserResponse>(`/admin/users/${id}`);
  }

  createUser(req: CreateUserRequest): Observable<UserResponse> {
    return this.api.post<UserResponse>('/admin/users', req);
  }

  updateUser(id: string, req: CreateUserRequest): Observable<UserResponse> {
    return this.api.put<UserResponse>(`/admin/users/${id}`, req);
  }

  deleteUser(id: string): Observable<void> {
    return this.api.delete<void>(`/admin/users/${id}`);
  }

  assignRole(keycloakId: string, req: RoleAssignRequest): Observable<UserResponse> {
    return this.api.patch<UserResponse>(`/admin/users/${keycloakId}/roles`, req);
  }

  updateStatus(keycloakId: string, req: UserStatusRequest): Observable<UserResponse> {
    return this.api.patch<UserResponse>(`/admin/users/${keycloakId}/status`, req);
  }

  resetPassword(keycloakId: string): Observable<{ message: string }> {
    return this.api.post<{ message: string }>(`/admin/users/${keycloakId}/reset-password`, {});
  }

  getMe(): Observable<UserResponse> {
    return this.api.get<UserResponse>('/users/me');
  }

  updateMe(req: UpdateProfileRequest): Observable<UserResponse> {
    return this.api.put<UserResponse>('/users/me', req);
  }
}
