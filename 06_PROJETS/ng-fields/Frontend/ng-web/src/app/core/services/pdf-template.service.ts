import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../api/api.service';
import {
  PdfTemplateResponse,
  CreatePdfTemplateRequest,
  UpdatePdfTemplateRequest,
} from '../../shared/models/pdf-template.dto';

@Injectable({ providedIn: 'root' })
export class PdfTemplateService {
  private api = inject(ApiService);

  list(type?: string): Observable<PdfTemplateResponse[]> {
    const params: Record<string, string | number | boolean | undefined> = {};
    if (type) params['type'] = type;
    return this.api.get<PdfTemplateResponse[]>('/reports/templates', params);
  }

  getById(id: string): Observable<PdfTemplateResponse> {
    return this.api.get<PdfTemplateResponse>(`/reports/templates/${id}`);
  }

  getDefault(type?: string): Observable<PdfTemplateResponse> {
    const params: Record<string, string | number | boolean | undefined> = {};
    if (type) params['type'] = type;
    return this.api.get<PdfTemplateResponse>('/reports/templates/default', params);
  }

  create(req: CreatePdfTemplateRequest): Observable<PdfTemplateResponse> {
    return this.api.post<PdfTemplateResponse>('/reports/templates', req);
  }

  update(id: string, req: UpdatePdfTemplateRequest): Observable<PdfTemplateResponse> {
    return this.api.put<PdfTemplateResponse>(`/reports/templates/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.api.delete<void>(`/reports/templates/${id}`);
  }
}
