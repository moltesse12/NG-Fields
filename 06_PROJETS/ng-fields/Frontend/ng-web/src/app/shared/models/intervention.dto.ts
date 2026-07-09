export type PhotoType = 'BEFORE' | 'AFTER';

export interface CreateItemRequest {
  type: string;
  description: string;
  quantity?: number;
  unitPrice?: number;
}

export interface CreateInterventionRequest {
  reference: string;
  clientId: string;
  clientName?: string;
  clientEmail?: string;
  clientPhone?: string;
  clientAddress?: string;
  equipmentType?: string;
  equipmentBrand?: string;
  equipmentModel?: string;
  equipmentSerial?: string;
  equipmentLocation?: string;
  reportedIssue?: string;
  openprojectTicketId?: string;
  openprojectTicketUrl?: string;
  diagnosis?: string;
  workDone?: string;
  status?: string;
  interventionDate?: string;
  assignedTo?: string;
  siteAddress?: string;
  siteCity?: string;
  estimatedCost?: number;
  notes?: string;
  items?: CreateItemRequest[];
}

export interface ItemResponse {
  id: string;
  type: string;
  description: string;
  quantity: number;
  unitPrice: number;
  total: number;
  createdAt: string;
}

export interface InterventionResponse {
  id: string;
  reference: string;
  clientId: string;
  clientName: string | null;
  clientEmail: string | null;
  clientPhone: string | null;
  clientAddress: string | null;
  equipmentType: string | null;
  equipmentBrand: string | null;
  equipmentModel: string | null;
  equipmentSerial: string | null;
  equipmentLocation: string | null;
  reportedIssue: string | null;
  openprojectTicketId: string | null;
  openprojectTicketUrl: string | null;
  diagnosis: string | null;
  workDone: string | null;
  status: string;
  interventionDate: string | null;
  createdBy: string | null;
  assignedTo: string | null;
  siteAddress: string | null;
  siteCity: string | null;
  estimatedCost: number | null;
  totalCost: number | null;
  clientSignature: string | null;
  technicianSignature: string | null;
  managerSignature: string | null;
  signedAt: string | null;
  departureTime: string | null;
  arrivalTime: string | null;
  startTime: string | null;
  endTime: string | null;
  durationMinutes: number | null;
  result: string | null;
  recommendations: string | null;
  billable: boolean | null;
  billingAmount: number | null;
  billingNotes: string | null;
  localId: string | null;
  notes: string | null;
  active: boolean;
  createdAt: string;
  updatedAt: string;
  items: ItemResponse[];
}

export interface ItemRequest {
  type: string;
  description: string;
  quantity: number;
  unitPrice?: number;
}

export interface UpdateScheduleRequest {
  departureTime?: string;
  arrivalTime?: string;
  startTime?: string;
  endTime?: string;
}

export interface UpdateEquipmentRequest {
  brand?: string;
  model?: string;
  serial?: string;
  location?: string;
  problemDescription?: string;
  openprojectTicketId?: string;
  openprojectTicketUrl?: string;
}

export interface UpdateDiagnosisRequest {
  diagnosis?: string;
  workDone?: string;
}

export interface UpdateResultRequest {
  result: string;
}

export interface UpdateRecommendationsRequest {
  recommendations?: string;
}

export interface UpdateBillingRequest {
  billable: boolean;
  billingAmount?: number;
  billingNotes?: string;
}

export interface SignatureRequest {
  imageBase64: string;
  signatoryName?: string;
}

export interface SyncRequest {
  reference: string;
  clientId: string;
  clientName?: string;
  clientEmail?: string;
  clientPhone?: string;
  clientAddress?: string;
  equipmentType?: string;
  equipmentBrand?: string;
  equipmentModel?: string;
  equipmentSerial?: string;
  reportedIssue?: string;
  status?: string;
  interventionDate?: string;
  siteAddress?: string;
  siteCity?: string;
  localId: string;
}

export interface PhotoResponse {
  id: string;
  url: string;
  type: PhotoType;
  latitude: number | null;
  longitude: number | null;
  takenAt: string;
  originalFilename: string;
  createdAt: string;
}
