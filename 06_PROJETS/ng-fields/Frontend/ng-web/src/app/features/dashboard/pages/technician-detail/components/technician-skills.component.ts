import { Component, Input , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TechnicianSkill, Certification } from '../../technicians/components/schemas/technician.schema';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-technician-skills',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="rounded-lg border bg-card p-4 md:p-6">
      <div class="text-sm font-medium mb-4">Compétences et certifications</div>

      <div class="mb-6">
        <h3 class="text-xs font-medium text-muted-foreground uppercase mb-3">Compétences</h3>
        <div class="space-y-2">
          @for (skill of skills; track skill.name) {
            <div class="flex items-center justify-between">
              <span class="text-sm">{{ skill.name }}</span>
              <span class="text-xs rounded-full px-2 py-0.5 {{ skillBadgeClass(skill.level) }}">
                {{ skillLevelLabel(skill.level) }}
              </span>
            </div>
          }
        </div>
      </div>

      @if (certifications.length > 0) {
        <div class="border-t pt-4">
          <h3 class="text-xs font-medium text-muted-foreground uppercase mb-3">Certifications</h3>
          <div class="space-y-2">
            @for (cert of certifications; track cert.name) {
              <div class="rounded-md border p-3">
                <p class="font-medium text-sm">{{ cert.name }}</p>
                <p class="text-xs text-muted-foreground mt-1">{{ cert.issuer }}</p>
                <div class="flex items-center justify-between mt-2">
                  <span class="text-xs text-muted-foreground">Délivrée : {{ formatDate(cert.issuedAt) }}</span>
                  @if (cert.expiresAt) {
                    <span class="text-xs" [class.text-green-600]="!isExpired(cert.expiresAt)" [class.text-red-600]="isExpired(cert.expiresAt)">
                      @if (isExpired(cert.expiresAt)) { ⚠ Expirée }
                      @else { Expire : {{ formatDate(cert.expiresAt) }} }
                    </span>
                  }
                </div>
              </div>
            }
          </div>
        </div>
      }
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class TechnicianSkillsComponent {
  @Input() skills: TechnicianSkill[] = [];
  @Input() certifications: Certification[] = [];

  skillBadgeClass(level: string): string {
    const map: Record<string, string> = {
      'EXPERT': 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      'ADVANCED': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
      'INTERMEDIATE': 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400',
      'BEGINNER': 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400',
    };
    return map[level] || 'bg-gray-100 text-gray-700';
  }

  skillLevelLabel(level: string): string {
    const map: Record<string, string> = {
      'EXPERT': 'Expert', 'ADVANCED': 'Confirmé', 'INTERMEDIATE': 'Intermédiaire', 'BEGINNER': 'Débutant',
    };
    return map[level] || level;
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR', { year: 'numeric', month: 'short', day: 'numeric' });
  }

  isExpired(date: string): boolean {
    return new Date(date) < new Date();
  }
}
