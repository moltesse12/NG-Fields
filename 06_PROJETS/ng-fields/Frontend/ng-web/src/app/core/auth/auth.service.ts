import { Injectable } from '@angular/core';
import { OidcSecurityService, UserDataResult } from 'angular-auth-oidc-client';
import { Observable, map } from 'rxjs';

export interface AuthUser {
  sub: string;
  email: string;
  preferred_username: string;
  realm_access: { roles: string[] };
  given_name?: string;
  family_name?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  constructor(private oidc: OidcSecurityService) {}

  get isAuthenticated$(): Observable<boolean> {
    return this.oidc.isAuthenticated$.pipe(map((r) => r.isAuthenticated));
  }

  get userData$(): Observable<UserDataResult> {
    return this.oidc.userData$;
  }

  get token$(): Observable<string> {
    return this.oidc.getAccessToken();
  }

  login(): void {
    this.oidc.authorize();
  }

  logout(): void {
    this.oidc.logoff().subscribe({
      error: (err) => console.warn('Logout error:', err),
    });
  }

  checkAuth(url?: string): Observable<any> {
    return this.oidc.checkAuth(url);
  }
}
