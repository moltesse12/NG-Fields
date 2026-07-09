import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs';
import { OidcSecurityService } from 'angular-auth-oidc-client';

export const authRoleGuard: CanActivateFn = (route, _state) => {
  const router = inject(Router);
  const oidc = inject(OidcSecurityService);
  const requiredRoles = route.data['roles'] as string[] | undefined;

  if (!requiredRoles || requiredRoles.length === 0) return true;

  return oidc.userData$.pipe(
    map((result) => {
      const userRoles: string[] = result?.userData?.realm_access?.roles ?? [];
      const hasRole = requiredRoles.some((r) => userRoles.includes(r));
      return hasRole || router.parseUrl('/unauthorized');
    }),
  );
};
