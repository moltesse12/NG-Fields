import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ClientService } from './client.service';

describe('ClientService', () => {
  let service: ClientService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        ClientService,
      ],
    });
    service = TestBed.inject(ClientService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getClients', () => {
    it('should fetch paginated clients', () => {
      const mockPage = {
        content: [{ id: '1', companyName: 'Test Corp', email: 'test@corp.tg' }],
        totalElements: 1,
        totalPages: 1,
        number: 0,
        size: 20,
        first: true,
        last: true,
        empty: false,
      };

      service.getClients().subscribe((result) => {
        expect(result.content.length).toBe(1);
      });

      const req = httpMock.expectOne((r) =>
        r.url === 'http://localhost:8080/api/clients' &&
        r.params.get('page') === '0' &&
        r.params.get('size') === '20'
      );
      expect(req.request.method).toBe('GET');
      req.flush(mockPage);
    });

    it('should support custom page and size', () => {
      service.getClients(2, 50).subscribe();

      const req = httpMock.expectOne((r) =>
        r.url === 'http://localhost:8080/api/clients' &&
        r.params.get('page') === '2' &&
        r.params.get('size') === '50'
      );
      req.flush({ content: [], totalElements: 0, totalPages: 0, number: 2, size: 50, first: false, last: true, empty: true });
    });
  });

  describe('searchClients', () => {
    it('should search clients with query', () => {
      service.searchClients('acme').subscribe();

      const req = httpMock.expectOne((r) =>
        r.url === 'http://localhost:8080/api/clients/search' &&
        r.params.get('q') === 'acme'
      );
      expect(req.request.method).toBe('GET');
      req.flush({ content: [], totalElements: 0, totalPages: 0, number: 0, size: 20, first: true, last: true, empty: true });
    });
  });

  describe('getClient', () => {
    it('should fetch client by id', () => {
      const mockClient = { id: '1', companyName: 'Test Corp', email: 'test@corp.tg' };

      service.getClient('1').subscribe((result) => {
        expect(result.companyName).toBe('Test Corp');
      });

      const req = httpMock.expectOne('http://localhost:8080/api/clients/1');
      req.flush(mockClient);
    });
  });

  describe('createClient', () => {
    it('should create client', () => {
      const req = {
        companyName: 'New Corp',
        email: 'new@corp.tg',
      };

      service.createClient(req).subscribe((result) => {
        expect(result.companyName).toBe('New Corp');
      });

      const httpReq = httpMock.expectOne('http://localhost:8080/api/clients');
      expect(httpReq.request.method).toBe('POST');
      expect(httpReq.request.body).toEqual(req);
      httpReq.flush({ id: '2', ...req });
    });
  });

  describe('updateClient', () => {
    it('should update client', () => {
      const req = { companyName: 'Updated Corp', email: 'updated@corp.tg' };

      service.updateClient('1', req).subscribe((result) => {
        expect(result.companyName).toBe('Updated Corp');
      });

      const httpReq = httpMock.expectOne('http://localhost:8080/api/clients/1');
      expect(httpReq.request.method).toBe('PUT');
      expect(httpReq.request.body).toEqual(req);
      httpReq.flush({ id: '1', ...req });
    });
  });

  describe('deleteClient', () => {
    it('should delete client by id', () => {
      service.deleteClient('1').subscribe();

      const req = httpMock.expectOne('http://localhost:8080/api/clients/1');
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });
});
