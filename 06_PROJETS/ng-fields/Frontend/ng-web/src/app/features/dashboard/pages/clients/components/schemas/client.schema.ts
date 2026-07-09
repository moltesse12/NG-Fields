export interface Client {
  id: number;
  name: string;
  type: 'Entreprise' | 'Indépendant' | 'Administration' | 'Autre';
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
  email: string;
  phone: string;
  website?: string;
  address: string;
  city: string;
  postalCode: string;
  country: string;
  registrationNumber: string;
  taxId: string;
  createdAt: string;
  lastInterventionAt?: string;
  contact: ClientContact;
  interventions: {
    total: number;
    resolved: number;
    pending: number;
  };
}

export interface ClientContact {
  name: string;
  title: string;
  email: string;
  phone: string;
  mobile: string;
}

export interface ClientDetailView extends Client {
  notes: string;
  interventionsHistory: InterventionSummary[];
  interventionsUpcoming: InterventionSummary[];
  allContacts: ClientContact[];
}

export interface InterventionSummary {
  id: number;
  reference: string;
  type: string;
  status: string;
  date: string;
  technician: string;
  duration?: number;
}
