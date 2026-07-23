export interface EmailTemplateResponse {
  id: string;
  name: string;
  description: string | null;
  templateKey: string;
  subject: string;
  bodyHtml: string;
  isActive: boolean;
  createdBy: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateEmailTemplateRequest {
  name: string;
  description?: string;
  templateKey: string;
  subject: string;
  bodyHtml: string;
}

export interface UpdateEmailTemplateRequest {
  name?: string;
  description?: string;
  subject?: string;
  bodyHtml?: string;
  isActive?: boolean;
}

export const EMAIL_VARIABLES: Record<string, string[]> = {
  WELCOME_CREDENTIALS: ['firstName', 'email', 'tempPassword', 'loginUrl'],
  PASSWORD_RESET: ['firstName', 'resetLink'],
  CUSTOM: [],
};
