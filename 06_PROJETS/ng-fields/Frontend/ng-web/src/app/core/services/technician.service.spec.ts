import { TestBed } from '@angular/core/testing';
import { TechnicianService } from './technician.service';
import { ApiService } from '../api/api.service';
import { of } from 'rxjs';

describe('TechnicianService', () => {
  let service: TechnicianService;
  let api: Record<string, ReturnType<typeof vi.fn>>;

  beforeEach(() => {
    TestBed.resetTestingModule();
    api = { get: vi.fn(), put: vi.fn(), delete: vi.fn() };
    TestBed.configureTestingModule({
      providers: [TechnicianService, { provide: ApiService, useValue: api }],
    });
    service = TestBed.inject(TechnicianService);
  });

  it('fetches technicians', () => {
    api['get'].mockReturnValue(of({ content: [], totalElements: 0 }));
    service.getTechnicians(0, 20).subscribe();
    expect(api['get']).toHaveBeenCalledWith('/admin/technicians', { page: 0, size: 20 });
  });

  it('fetches single technician', () => {
    api['get'].mockReturnValue(of({ id: '1', firstName: 'John' }));
    service.getTechnician('1').subscribe();
    expect(api['get']).toHaveBeenCalledWith('/admin/technicians/1');
  });
});
