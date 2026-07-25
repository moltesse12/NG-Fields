import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ApiService } from './api.service';

describe('ApiService', () => {
  let service: ApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        ApiService,
      ],
    });
    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('get', () => {
    it('should make GET request to correct URL', () => {
      const mockResponse = { id: '1', name: 'test' };

      service.get('/test-endpoint').subscribe((result) => {
        expect(result).toEqual(mockResponse);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/test-endpoint');
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should append query params', () => {
      service.get('/test', { page: 0, size: 10 }).subscribe();

      const req = httpMock.expectOne((r) =>
        r.url === 'http://localhost:8080/api/test' &&
        r.params.get('page') === '0' &&
        r.params.get('size') === '10'
      );
      expect(req.request.method).toBe('GET');
      req.flush({});
    });

    it('should skip undefined params', () => {
      service.get('/test', { page: 0, status: undefined }).subscribe();

      const req = httpMock.expectOne((r) =>
        r.url === 'http://localhost:8080/api/test' &&
        r.params.has('page') &&
        !r.params.has('status')
      );
      req.flush({});
    });

    it('should parse HTTP error into ApiError', () => {
      service.get('/test').subscribe({
        error: (err) => {
          expect(err.status).toBe(404);
          expect(err.detail).toBe('Not found');
        },
      });

      const req = httpMock.expectOne('http://localhost:8080/api/test');
      req.flush(
        { type: 'about:blank', title: 'Not Found', status: 404, detail: 'Not found' },
        { status: 404, statusText: 'Not Found' }
      );
    });
  });

  describe('post', () => {
    it('should make POST request with body', () => {
      const body = { name: 'new item' };
      const mockResponse = { id: '1', ...body };

      service.post('/test', body).subscribe((result) => {
        expect(result).toEqual(mockResponse);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/test');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(body);
      req.flush(mockResponse);
    });
  });

  describe('put', () => {
    it('should make PUT request with body', () => {
      const body = { name: 'updated' };
      const mockResponse = { id: '1', ...body };

      service.put('/test/1', body).subscribe((result) => {
        expect(result).toEqual(mockResponse);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/test/1');
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(body);
      req.flush(mockResponse);
    });
  });

  describe('patch', () => {
    it('should make PATCH request with body', () => {
      const body = { status: 'ACTIVE' };

      service.patch('/test/1', body).subscribe();

      const req = httpMock.expectOne('http://localhost:8080/api/test/1');
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body).toEqual(body);
      req.flush({});
    });
  });

  describe('delete', () => {
    it('should make DELETE request', () => {
      service.delete('/test/1').subscribe();

      const req = httpMock.expectOne('http://localhost:8080/api/test/1');
      expect(req.request.method).toBe('DELETE');
      req.flush({});
    });
  });

  describe('parseError', () => {
    it('should parse RFC 7807 error body', () => {
      const error = {
        error: {
          type: 'https://example.com/errors/not-found',
          title: 'Not Found',
          status: 404,
          detail: 'User not found',
          errors: { id: 'must be valid UUID' },
        },
        status: 404,
      };

      const result = service.parseError(error as any);
      expect(result.status).toBe(404);
      expect(result.detail).toBe('User not found');
      expect(result.errors).toEqual({ id: 'must be valid UUID' });
    });

    it('should handle non-object error body', () => {
      const error = { error: 'Server Error', status: 500 };

      const result = service.parseError(error as any);
      expect(result.status).toBe(500);
      expect(result.detail).toBe('Server Error');
    });
  });
});
