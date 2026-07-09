import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../api/api.service';
import { Page } from '../../shared/models/page';
import {
  CreateInterventionRequest, InterventionResponse,
  ItemRequest, UpdateScheduleRequest, UpdateEquipmentRequest,
  UpdateDiagnosisRequest, UpdateResultRequest, UpdateRecommendationsRequest,
  UpdateBillingRequest, SyncRequest,
} from '../../shared/models/intervention.dto';

@Injectable({ providedIn: 'root' })
export class InterventionService {
  private api = inject(ApiService);

  getInterventions(params?: { status?: string; technicianId?: string; page?: number; size?: number }): Observable<Page<InterventionResponse>> {
    return this.api.get<Page<InterventionResponse>>('/interventions', params as Record<string, string | number | boolean | undefined>);
  }

  getIntervention(id: string): Observable<InterventionResponse> {
    return this.api.get<InterventionResponse>(`/interventions/${id}`);
  }

  createIntervention(req: CreateInterventionRequest): Observable<InterventionResponse> {
    return this.api.post<InterventionResponse>('/interventions', req);
  }

  updateIntervention(id: string, req: CreateInterventionRequest): Observable<InterventionResponse> {
    return this.api.put<InterventionResponse>(`/interventions/${id}`, req);
  }

  deleteIntervention(id: string): Observable<void> {
    return this.api.delete<void>(`/interventions/${id}`);
  }

  getInterventionsByClient(clientId: string): Observable<InterventionResponse[]> {
    return this.api.get<InterventionResponse[]>(`/interventions/by-client/${clientId}`);
  }

  getInterventionPdf(id: string): Observable<Blob> {
    return this.api.get<Blob>(`/interventions/${id}/pdf`);
  }

  updateSchedule(id: string, req: UpdateScheduleRequest): Observable<InterventionResponse> {
    return this.api.patch<InterventionResponse>(`/interventions/${id}/schedule`, req);
  }

  updateEquipment(id: string, req: UpdateEquipmentRequest): Observable<InterventionResponse> {
    return this.api.patch<InterventionResponse>(`/interventions/${id}/equipment`, req);
  }

  updateDiagnosis(id: string, req: UpdateDiagnosisRequest): Observable<InterventionResponse> {
    return this.api.patch<InterventionResponse>(`/interventions/${id}/diagnosis`, req);
  }

  updateResult(id: string, req: UpdateResultRequest): Observable<InterventionResponse> {
    return this.api.patch<InterventionResponse>(`/interventions/${id}/result`, req);
  }

  updateRecommendations(id: string, req: UpdateRecommendationsRequest): Observable<InterventionResponse> {
    return this.api.patch<InterventionResponse>(`/interventions/${id}/recommendations`, req);
  }

  updateBilling(id: string, req: UpdateBillingRequest): Observable<InterventionResponse> {
    return this.api.patch<InterventionResponse>(`/interventions/${id}/billing`, req);
  }

  addItem(id: string, req: ItemRequest): Observable<InterventionResponse> {
    return this.api.post<InterventionResponse>(`/interventions/${id}/items`, req);
  }

  updateItem(id: string, itemId: string, req: ItemRequest): Observable<InterventionResponse> {
    return this.api.put<InterventionResponse>(`/interventions/${id}/items/${itemId}`, req);
  }

  deleteItem(id: string, itemId: string): Observable<InterventionResponse> {
    return this.api.delete<InterventionResponse>(`/interventions/${id}/items/${itemId}`);
  }

  closeIntervention(id: string): Observable<InterventionResponse> {
    return this.api.post<InterventionResponse>(`/interventions/${id}/close`, {});
  }

  syncIntervention(req: SyncRequest): Observable<InterventionResponse> {
    return this.api.post<InterventionResponse>('/sync/interventions', req);
  }
}
