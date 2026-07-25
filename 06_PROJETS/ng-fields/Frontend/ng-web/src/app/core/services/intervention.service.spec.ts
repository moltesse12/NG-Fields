import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { InterventionService } from './intervention.service';

describe('InterventionService', () => {
  let service: InterventionService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        InterventionService,
      ],
    });
    service = TestBed.inject(InterventionService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getInterventions', () => {
    it('should fetch paginated interventions', () => {
      const mockPage = {
        content: [{ id: '1', reference: 'INT-001', status: 'PENDING', clientId: 'c1' }],
        totalElements: 1,
        totalPages: 1,
        number: 0,
        size: 20,
        first: true,
        last: true,
        empty: false,
      };

      service.getInterventions().subscribe((result) => {
        expect(result.content.length).toBe(1);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/interventions');
      expect(req.request.method).toBe('GET');
      req.flush(mockPage);
    });

    it('should pass filter params', () => {
      service.getInterventions({ status: 'IN_PROGRESS', technicianId: 'tech-1', page: 1, size: 10 }).subscribe();

      const req = httpMock.expectOne((r) =>
        r.url === 'http://localhost:8080/api/interventions' &&
        r.params.get('status') === 'IN_PROGRESS' &&
        r.params.get('technicianId') === 'tech-1' &&
        r.params.get('page') === '1' &&
        r.params.get('size') === '10'
      );
      req.flush({ content: [], totalElements: 0, totalPages: 0, number: 1, size: 10, first: false, last: true, empty: true });
    });
  });

  describe('getIntervention', () => {
    it('should fetch intervention by id', () => {
      const mock = { id: '1', reference: 'INT-001', status: 'PENDING' };

      service.getIntervention('1').subscribe((result) => {
        expect(result.reference).toBe('INT-001');
      });

      const req = httpMock.expectOne('http://localhost:8080/api/interventions/1');
      req.flush(mock);
    });
  });

  describe('createIntervention', () => {
    it('should create intervention', () => {
      const req = { reference: 'INT-002', clientId: 'c1', clientName: 'Test Client' };

      service.createIntervention(req).subscribe((result) => {
        expect(result.reference).toBe('INT-002');
      });

      const httpReq = httpMock.expectOne('http://localhost:8080/api/interventions');
      expect(httpReq.request.method).toBe('POST');
      expect(httpReq.request.body).toEqual(req);
      httpReq.flush({ id: '2', ...req, status: 'PENDING' });
    });
  });

  describe('updateSchedule', () => {
    it('should patch schedule', () => {
      const req = { departureTime: '2026-01-01T08:00:00Z' };

      service.updateSchedule('1', req).subscribe();

      const httpReq = httpMock.expectOne('http://localhost:8080/api/interventions/1/schedule');
      expect(httpReq.request.method).toBe('PATCH');
      expect(httpReq.request.body).toEqual(req);
      httpReq.flush({ id: '1' });
    });
  });

  describe('updateResult', () => {
    it('should patch result', () => {
      service.updateResult('1', { result: 'RESOLVED' }).subscribe();

      const httpReq = httpMock.expectOne('http://localhost:8080/api/interventions/1/result');
      expect(httpReq.request.method).toBe('PATCH');
      expect(httpReq.request.body).toEqual({ result: 'RESOLVED' });
      httpReq.flush({ id: '1', result: 'RESOLVED' });
    });
  });

  describe('addItem', () => {
    it('should add item to intervention', () => {
      const req = { type: 'PART', description: 'Filter', quantity: 2, unitPrice: 15.0 };

      service.addItem('1', req).subscribe();

      const httpReq = httpMock.expectOne('http://localhost:8080/api/interventions/1/items');
      expect(httpReq.request.method).toBe('POST');
      expect(httpReq.request.body).toEqual(req);
      httpReq.flush({ id: '1', items: [req] });
    });
  });

  describe('deleteItem', () => {
    it('should delete item from intervention', () => {
      service.deleteItem('1', 'item-1').subscribe();

      const req = httpMock.expectOne('http://localhost:8080/api/interventions/1/items/item-1');
      expect(req.request.method).toBe('DELETE');
      req.flush({});
    });
  });

  describe('closeIntervention', () => {
    it('should close intervention', () => {
      service.closeIntervention('1').subscribe();

      const req = httpMock.expectOne('http://localhost:8080/api/interventions/1/close');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({});
      req.flush({ id: '1', status: 'COMPLETED' });
    });
  });

  describe('syncIntervention', () => {
    it('should sync offline intervention', () => {
      const req = { reference: 'INT-003', clientId: 'c1', localId: 'local-123' };

      service.syncIntervention(req).subscribe();

      const httpReq = httpMock.expectOne('http://localhost:8080/api/sync/interventions');
      expect(httpReq.request.method).toBe('POST');
      expect(httpReq.request.body).toEqual(req);
      httpReq.flush({ id: '3', ...req, status: 'PENDING' });
    });
  });
});
