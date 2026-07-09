import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ApiService } from './api.service';

describe('ApiService', () => {
  let service: ApiService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      providers: [ApiService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ApiService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('performs GET', () => {
    service.get<{ id: string }>('/test').subscribe(res => expect(res.id).toBe('123'));
    const req = http.expectOne('http://localhost:8080/api/test');
    expect(req.request.method).toBe('GET');
    req.flush({ id: '123' });
  });

  it('performs POST', () => {
    service.post<{ ok: boolean }>('/test', { foo: 'bar' }).subscribe(res => expect(res.ok).toBe(true));
    const req = http.expectOne('http://localhost:8080/api/test');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ foo: 'bar' });
    req.flush({ ok: true });
  });

  it('performs PUT', () => {
    service.put<{ ok: boolean }>('/test/1', { foo: 'bar' }).subscribe(res => expect(res.ok).toBe(true));
    const req = http.expectOne('http://localhost:8080/api/test/1');
    expect(req.request.method).toBe('PUT');
    req.flush({ ok: true });
  });

  it('performs PATCH', () => {
    service.patch<{ ok: boolean }>('/test/1', { foo: 'bar' }).subscribe(res => expect(res.ok).toBe(true));
    const req = http.expectOne('http://localhost:8080/api/test/1');
    expect(req.request.method).toBe('PATCH');
    req.flush({ ok: true });
  });

  it('performs DELETE', () => {
    service.delete<void>('/test/1').subscribe();
    const req = http.expectOne('http://localhost:8080/api/test/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('sends query params', () => {
    service.get('/test', { page: 1, size: 20 }).subscribe();
    const req = http.expectOne(r => r.url === 'http://localhost:8080/api/test');
    expect(req.request.params.get('page')).toBe('1');
    expect(req.request.params.get('size')).toBe('20');
    req.flush([]);
  });

  it('handles errors', () => {
    service.get('/test').subscribe({ error: err => expect(err.status).toBe(500) });
    const req = http.expectOne('http://localhost:8080/api/test');
    req.flush('Server Error', { status: 500, statusText: 'Internal Server Error' });
  });
});
