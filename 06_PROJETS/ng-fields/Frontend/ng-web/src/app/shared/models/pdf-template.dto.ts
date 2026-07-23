export interface PdfTemplateConfig {
  title: string;
  subtitle?: string;
  orientation: 'LANDSCAPE' | 'PORTRAIT';
  pageSize: 'A4' | 'LETTER';
  margins: { top: number; bottom: number; left: number; right: number };
  header: {
    showLogo: boolean;
    companyName: string;
    backgroundColor: string;
    textColor: string;
    fontSize: number;
  };
  columns: PdfTemplateColumn[];
  footer: {
    text: string;
    fontSize: number;
    textColor: string;
  };
  fonts: {
    titleSize: number;
    headerSize: number;
    cellSize: number;
    titleFont: string;
    headerFont: string;
    cellFont: string;
  };
}

export interface PdfTemplateColumn {
  field: string;
  label: string;
  width: number;
  visible: boolean;
}

export interface PdfTemplateResponse {
  id: string;
  name: string;
  description: string | null;
  templateType: string;
  config: string;
  isDefault: boolean;
  createdBy: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreatePdfTemplateRequest {
  name: string;
  description?: string;
  templateType?: string;
  config: string;
}

export interface UpdatePdfTemplateRequest {
  name?: string;
  description?: string;
  config?: string;
  isDefault?: boolean;
}

export const AVAILABLE_FIELDS = [
  { field: 'reference', label: 'Référence' },
  { field: 'clientName', label: 'Client' },
  { field: 'clientEmail', label: 'Email' },
  { field: 'clientPhone', label: 'Téléphone' },
  { field: 'equipmentType', label: 'Équipement' },
  { field: 'equipmentBrand', label: 'Marque' },
  { field: 'equipmentModel', label: 'Modèle' },
  { field: 'reportedIssue', label: 'Problème' },
  { field: 'diagnosis', label: 'Diagnostic' },
  { field: 'workDone', label: 'Travail' },
  { field: 'status', label: 'Statut' },
  { field: 'result', label: 'Résultat' },
];
