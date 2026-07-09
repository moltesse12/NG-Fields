export type TechnicianStatus = 'AVAILABLE' | 'BUSY' | 'ON_LEAVE' | 'INACTIVE';
export type SkillLevel = 'EXPERT' | 'ADVANCED' | 'INTERMEDIATE' | 'BEGINNER';

export interface CreateTechnicianRequest {
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  status?: TechnicianStatus;
}

export interface TechnicianResponse {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string | null;
  status: TechnicianStatus;
  skills: { name: string; level: SkillLevel }[];
  active: boolean;
  createdAt: string;
  updatedAt: string;
}
