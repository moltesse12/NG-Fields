import { TestBed } from '@angular/core/testing';
import { InterventionService } from './intervention.service';
import { ApiService } from '../api/api.service';
import { of } from 'rxjs';

describe('InterventionService', () => {
  let service: InterventionService;
  let api: Record<string, ReturnType<typeof vi.fn>>;

  beforeEach(() => {
    TestBed.resetTestingModule();
    api = { get: vi.fn(), post: vi.fn(), patch: vi.fn(), delete: vi.fn() };
    TestBed.configureTestingModule({
      providers: [InterventionService, { provide: ApiService, useValue: api }],
    });
    service = TestBed.inject(InterventionService);
  });

  it('fetches interventions', () => {
    api['get'].mockReturnValue(of({ content: [], totalElements: 0 }));
    service.getInterventions({ status: 'PLANNED', page: 0, size: 20 }).subscribe();
    expect(api['get']).toHaveBeenCalledWith('/interventions', { status: 'PLANNED', page: 0, size: 20 });
  });

  it('fetches single intervention', () => {
    api['get'].mockReturnValue(of({ id: '1', reference: 'INT-001' }));
    service.getIntervention('1').subscribe();
    expect(api['get']).toHaveBeenCalledWith('/interventions/1');
  });

  it('closes an intervention', () => {
    api['post'].mockReturnValue(of({}));
    service.closeIntervention('1').subscribe();
    expect(api['post']).toHaveBeenCalledWith('/interventions/1/close', {});
  });

  it('deletes an intervention', () => {
    api['delete'].mockReturnValue(of(undefined));
    service.deleteIntervention('1').subscribe();
    expect(api['delete']).toHaveBeenCalledWith('/interventions/1');
  });
});
