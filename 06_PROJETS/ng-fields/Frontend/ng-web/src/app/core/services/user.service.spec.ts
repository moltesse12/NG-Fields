import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { UserService } from './user.service';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        UserService,
      ],
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getUsers', () => {
    it('should fetch paginated users', () => {
      const mockPage = {
        content: [{ id: '1', username: 'admin', email: 'admin@test.tg', role: 'ADMIN' }],
        totalElements: 1,
        totalPages: 1,
        number: 0,
        size: 20,
        first: true,
        last: true,
        empty: false,
      };

      service.getUsers().subscribe((result) => {
        expect(result.content.length).toBe(1);
        expect(result.totalElements).toBe(1);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/admin/users');
      expect(req.request.method).toBe('GET');
      req.flush(mockPage);
    });
  });

  describe('getUser', () => {
    it('should fetch user by id', () => {
      const mockUser = { id: '1', username: 'admin', email: 'admin@test.tg', role: 'ADMIN' };

      service.getUser('1').subscribe((result) => {
        expect(result.id).toBe('1');
        expect(result.username).toBe('admin');
      });

      const req = httpMock.expectOne('http://localhost:8080/api/admin/users/1');
      req.flush(mockUser);
    });
  });

  describe('createUser', () => {
    it('should create user', () => {
      const req = {
        username: 'newuser',
        email: 'new@test.tg',
        firstName: 'New',
        lastName: 'User',
        role: 'TECHNICIAN' as const,
      };

      service.createUser(req).subscribe((result) => {
        expect(result.username).toBe('newuser');
      });

      const httpReq = httpMock.expectOne('http://localhost:8080/api/admin/users');
      expect(httpReq.request.method).toBe('POST');
      expect(httpReq.request.body).toEqual(req);
      httpReq.flush({ id: '2', ...req });
    });
  });

  describe('deleteUser', () => {
    it('should delete user by id', () => {
      service.deleteUser('1').subscribe();

      const req = httpMock.expectOne('http://localhost:8080/api/admin/users/1');
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });

  describe('assignRole', () => {
    it('should patch role for user', () => {
      service.assignRole('kc-123', { role: 'MANAGER' }).subscribe();

      const req = httpMock.expectOne('http://localhost:8080/api/admin/users/kc-123/roles');
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body).toEqual({ role: 'MANAGER' });
      req.flush({});
    });
  });

  describe('updateStatus', () => {
    it('should patch status for user', () => {
      service.updateStatus('kc-123', { enabled: false }).subscribe();

      const req = httpMock.expectOne('http://localhost:8080/api/admin/users/kc-123/status');
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body).toEqual({ enabled: false });
      req.flush({});
    });
  });

  describe('resetPassword', () => {
    it('should POST to reset password', () => {
      service.resetPassword('kc-123').subscribe((result) => {
        expect(result.message).toBeDefined();
      });

      const req = httpMock.expectOne('http://localhost:8080/api/admin/users/kc-123/reset-password');
      expect(req.request.method).toBe('POST');
      req.flush({ message: 'Password reset email sent' });
    });
  });

  describe('getMe', () => {
    it('should fetch current user profile', () => {
      const mockUser = { id: '1', username: 'me', email: 'me@test.tg', role: 'ADMIN' };

      service.getMe().subscribe((result) => {
        expect(result.username).toBe('me');
      });

      const req = httpMock.expectOne('http://localhost:8080/api/users/me');
      req.flush(mockUser);
    });
  });

  describe('updateMe', () => {
    it('should update current user profile', () => {
      const update = { firstName: 'Updated', lastName: 'Name' };

      service.updateMe(update).subscribe((result) => {
        expect(result.firstName).toBe('Updated');
      });

      const req = httpMock.expectOne('http://localhost:8080/api/users/me');
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(update);
      req.flush({ id: '1', ...update });
    });
  });
});
