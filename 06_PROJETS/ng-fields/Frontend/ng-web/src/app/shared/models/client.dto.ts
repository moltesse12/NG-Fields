export interface CreateClientRequest {
  companyName: string;
  contactName?: string;
  email: string;
  phone?: string;
  address?: string;
  latitude?: number;
  longitude?: number;
}

export type UpdateClientRequest = CreateClientRequest;

export interface ClientResponse {
  id: string;
  reference: string;
  companyName: string;
  contactName: string | null;
  email: string;
  phone: string | null;
  address: string | null;
  latitude: number | null;
  longitude: number | null;
  active: boolean;
  createdAt: string;
}
