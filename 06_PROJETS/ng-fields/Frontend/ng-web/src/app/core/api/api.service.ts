import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ApiError } from '../../shared/models/api-error';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  private toParams(params: Record<string, string | number | boolean | undefined>): HttpParams {
    let httpParams = new HttpParams();
    for (const [key, value] of Object.entries(params)) {
      if (value !== undefined && value !== null) {
        httpParams = httpParams.set(key, String(value));
      }
    }
    return httpParams;
  }

  parseError(err: HttpErrorResponse): ApiError {
    const body = err.error;
    if (body && typeof body === 'object') {
      return {
        type: body.type,
        title: body.title,
        status: body.status ?? err.status,
        detail: body.detail,
        errors: body.errors,
      };
    }
    return { status: err.status, detail: err.message };
  }

  get<T>(path: string, params?: Record<string, string | number | boolean | undefined>): Observable<T> {
    const opts = params ? { params: this.toParams(params) } : {};
    return this.http.get<T>(`${this.baseUrl}${path}`, opts).pipe(
      catchError(err => throwError(() => this.parseError(err)))
    );
  }

  post<T>(path: string, body: unknown): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${path}`, body).pipe(
      catchError(err => throwError(() => this.parseError(err)))
    );
  }

  put<T>(path: string, body: unknown): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}${path}`, body).pipe(
      catchError(err => throwError(() => this.parseError(err)))
    );
  }

  patch<T>(path: string, body: unknown): Observable<T> {
    return this.http.patch<T>(`${this.baseUrl}${path}`, body).pipe(
      catchError(err => throwError(() => this.parseError(err)))
    );
  }

  delete<T>(path: string): Observable<T> {
    return this.http.delete<T>(`${this.baseUrl}${path}`).pipe(
      catchError(err => throwError(() => this.parseError(err)))
    );
  }
}
