import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { of } from 'rxjs';
import { authRoleGuard } from './auth-role.guard';

describe('authRoleGuard', () => {
  let router: Router;
  let oidc: OidcSecurityService;

  beforeEach(() => {
    TestBed.resetTestingModule();
    const oidcMock = { userData$: of({ userData: { realm_access: { roles: [] } } }) };
    TestBed.configureTestingModule({
      providers: [
        { provide: OidcSecurityService, useValue: oidcMock },
        { provide: Router, useValue: { parseUrl: vi.fn().mockReturnValue('unauthorized') as any } },
      ],
    });
    router = TestBed.inject(Router);
    oidc = TestBed.inject(OidcSecurityService);
  });

  it('allows when no roles required', () => {
    const result = TestBed.runInInjectionContext(() =>
      authRoleGuard({ data: {} } as any, {} as any)
    );
    expect(result).toBe(true);
  });

  it('allows when user has required role', () => new Promise<void>(done => {
    (oidc as any).userData$ = of({ userData: { realm_access: { roles: ['administrator'] } } });
    const result = TestBed.runInInjectionContext(() =>
      authRoleGuard({ data: { roles: ['administrator'] } } as any, {} as any)
    ) as any;
    result.subscribe((v: boolean) => { expect(v).toBe(true); done(); });
  }));

  it('redirects to /unauthorized when user lacks required role', () => new Promise<void>(done => {
    (oidc as any).userData$ = of({ userData: { realm_access: { roles: ['user'] } } });
    const result = TestBed.runInInjectionContext(() =>
      authRoleGuard({ data: { roles: ['administrator'] } } as any, {} as any)
    ) as any;
    result.subscribe(() => {
      expect(router.parseUrl).toHaveBeenCalledWith('/unauthorized');
      done();
    });
  }));
});
