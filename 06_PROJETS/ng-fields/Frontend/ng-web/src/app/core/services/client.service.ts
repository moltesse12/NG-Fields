import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../api/api.service';
import { Page } from '../../shared/models/page';
import { CreateClientRequest, UpdateClientRequest, ClientResponse } from '../../shared/models/client.dto';

@Injectable({ providedIn: 'root' })
export class ClientService {
  private api = inject(ApiService);

  getClients(page = 0, size = 20): Observable<Page<ClientResponse>> {
    return this.api.get<Page<ClientResponse>>('/clients', { page, size });
  }

  searchClients(q: string, page = 0, size = 20): Observable<Page<ClientResponse>> {
    return this.api.get<Page<ClientResponse>>('/clients/search', { q, page, size });
  }

  getClient(id: string): Observable<ClientResponse> {
    return this.api.get<ClientResponse>(`/clients/${id}`);
  }

  createClient(req: CreateClientRequest): Observable<ClientResponse> {
    return this.api.post<ClientResponse>('/clients', req);
  }

  updateClient(id: string, req: UpdateClientRequest): Observable<ClientResponse> {
    return this.api.put<ClientResponse>(`/clients/${id}`, req);
  }

  deleteClient(id: string): Observable<void> {
    return this.api.delete<void>(`/clients/${id}`);
  }
}
