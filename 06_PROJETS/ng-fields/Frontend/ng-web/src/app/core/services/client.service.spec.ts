import { TestBed } from '@angular/core/testing';
import { ClientService } from './client.service';
import { ApiService } from '../api/api.service';
import { of } from 'rxjs';

describe('ClientService', () => {
  let service: ClientService;
  let api: Record<string, ReturnType<typeof vi.fn>>;

  beforeEach(() => {
    TestBed.resetTestingModule();
    api = { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() };
    TestBed.configureTestingModule({
      providers: [ClientService, { provide: ApiService, useValue: api }],
    });
    service = TestBed.inject(ClientService);
  });

  it('fetches paginated clients', () => {
    api['get'].mockReturnValue(of({ content: [{ id: '1' }], totalElements: 1 }));
    service.getClients(0, 10).subscribe();
    expect(api['get']).toHaveBeenCalledWith('/clients', { page: 0, size: 10 });
  });

  it('creates a client', () => {
    api['post'].mockReturnValue(of({}));
    service.createClient({ companyName: 'New', email: 'a@b.com' }).subscribe();
    expect(api['post']).toHaveBeenCalledWith('/clients', { companyName: 'New', email: 'a@b.com' });
  });

  it('deletes a client', () => {
    api['delete'].mockReturnValue(of(undefined));
    service.deleteClient('1').subscribe();
    expect(api['delete']).toHaveBeenCalledWith('/clients/1');
  });
});
