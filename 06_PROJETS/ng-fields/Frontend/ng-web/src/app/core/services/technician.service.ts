import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../api/api.service';
import { Page } from '../../shared/models/page';
import { CreateTechnicianRequest, TechnicianResponse } from '../../shared/models/technician.dto';

@Injectable({ providedIn: 'root' })
export class TechnicianService {
  private api = inject(ApiService);

  getTechnicians(page = 0, size = 20): Observable<Page<TechnicianResponse>> {
    return this.api.get<Page<TechnicianResponse>>('/admin/technicians', { page, size });
  }

  getTechnician(id: string): Observable<TechnicianResponse> {
    return this.api.get<TechnicianResponse>(`/admin/technicians/${id}`);
  }

  createTechnician(req: CreateTechnicianRequest): Observable<TechnicianResponse> {
    return this.api.post<TechnicianResponse>('/admin/technicians', req);
  }

  updateTechnician(id: string, req: CreateTechnicianRequest): Observable<TechnicianResponse> {
    return this.api.put<TechnicianResponse>(`/admin/technicians/${id}`, req);
  }

  deleteTechnician(id: string): Observable<void> {
    return this.api.delete<void>(`/admin/technicians/${id}`);
  }
}
