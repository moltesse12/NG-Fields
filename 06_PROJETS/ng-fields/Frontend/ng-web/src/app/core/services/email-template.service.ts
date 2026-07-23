import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../api/api.service';
import {
  EmailTemplateResponse,
  CreateEmailTemplateRequest,
  UpdateEmailTemplateRequest,
} from '../../shared/models/email-template.dto';

@Injectable({ providedIn: 'root' })
export class EmailTemplateService {
  private api = inject(ApiService);

  list(): Observable<EmailTemplateResponse[]> {
    return this.api.get<EmailTemplateResponse[]>('/reports/email-templates');
  }

  getById(id: string): Observable<EmailTemplateResponse> {
    return this.api.get<EmailTemplateResponse>(`/reports/email-templates/${id}`);
  }

  getByKey(key: string): Observable<EmailTemplateResponse> {
    return this.api.get<EmailTemplateResponse>(`/reports/email-templates/key/${key}`);
  }

  create(req: CreateEmailTemplateRequest): Observable<EmailTemplateResponse> {
    return this.api.post<EmailTemplateResponse>('/reports/email-templates', req);
  }

  update(id: string, req: UpdateEmailTemplateRequest): Observable<EmailTemplateResponse> {
    return this.api.put<EmailTemplateResponse>(`/reports/email-templates/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.api.delete<void>(`/reports/email-templates/${id}`);
  }
}
