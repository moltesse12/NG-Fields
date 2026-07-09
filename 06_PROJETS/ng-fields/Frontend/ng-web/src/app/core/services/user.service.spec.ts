import { TestBed } from '@angular/core/testing';
import { UserService } from './user.service';
import { ApiService } from '../api/api.service';
import { of } from 'rxjs';

describe('UserService', () => {
  let service: UserService;
  let api: Record<string, ReturnType<typeof vi.fn>>;

  beforeEach(() => {
    TestBed.resetTestingModule();
    api = { get: vi.fn(), post: vi.fn(), put: vi.fn(), patch: vi.fn() };
    TestBed.configureTestingModule({
      providers: [UserService, { provide: ApiService, useValue: api }],
    });
    service = TestBed.inject(UserService);
  });

  it('fetches users', () => {
    api['get'].mockReturnValue(of({ content: [], totalElements: 0 }));
    service.getUsers().subscribe();
    expect(api['get']).toHaveBeenCalledWith('/admin/users');
  });

  it('fetches current user', () => {
    api['get'].mockReturnValue(of({ id: '1', username: 'admin' }));
    service.getMe().subscribe();
    expect(api['get']).toHaveBeenCalledWith('/users/me');
  });

  it('resets password', () => {
    api['post'].mockReturnValue(of({ message: 'ok' }));
    service.resetPassword('kc-1').subscribe();
    expect(api['post']).toHaveBeenCalledWith('/admin/users/kc-1/reset-password', {});
  });
});
