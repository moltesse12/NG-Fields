import { Component, signal, computed, OnInit, inject, ChangeDetectionStrategy, ElementRef, ViewChild, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { IconComponent } from '../../../../shared/ui/icon/icon.component';
import { EmailTemplateService } from '../../../../core/services/email-template.service';
import {
  EmailTemplateResponse,
  EMAIL_VARIABLES,
} from '../../../../shared/models/email-template.dto';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-email-templates',
  standalone: true,
  imports: [CommonModule, FormsModule, IconComponent],
  template: `
    <div class="flex flex-col gap-6 p-4 md:p-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold tracking-tight">Templates Email</h1>
          <p class="text-sm text-muted-foreground">Editer les emails envoyes aux utilisateurs</p>
        </div>
        <button class="inline-flex items-center gap-2 rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90"
          (click)="openCreate()">
          <app-icon name="circle-plus" />
          Nouveau template
        </button>
      </div>

      @if (editing()) {
        <div class="grid gap-5 xl:grid-cols-2">
          <!-- LEFT: Form -->
          <div class="flex flex-col gap-4 rounded-xl border bg-card p-4">
            <div class="flex items-center justify-between">
              <h2 class="font-medium text-lg">{{ editName() || 'Nouveau template' }}</h2>
              <div class="flex items-center gap-2">
                <button class="rounded-md border px-3 py-1.5 text-xs font-medium hover:bg-muted"
                  (click)="cancelEdit()">Annuler</button>
                <button class="rounded-md bg-primary px-3 py-1.5 text-xs font-medium text-primary-foreground hover:bg-primary/90"
                  (click)="save()">Enregistrer</button>
              </div>
            </div>

            <!-- Tabs -->
            <div class="flex gap-1 rounded-lg border p-1">
              @for (tab of tabs(); track tab.id) {
                <button class="flex-1 rounded-md px-3 py-1.5 text-sm font-medium transition-colors"
                  [class.bg-muted]="activeTab() === tab.id"
                  [class.text-foreground]="activeTab() === tab.id"
                  [class.text-muted-foreground]="activeTab() !== tab.id"
                  (click)="activeTab.set(tab.id)">
                  {{ tab.label }}
                </button>
              }
            </div>

            @switch (activeTab()) {
              @case ('content') {
                <div class="space-y-4">
                  <div class="grid gap-2">
                    <label class="text-xs font-medium text-muted-foreground">Nom</label>
                    <input type="text" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                      [ngModel]="editName()" (ngModelChange)="editName.set($event)" />
                  </div>
                  <div class="grid gap-2">
                    <label class="text-xs font-medium text-muted-foreground">Cle technique</label>
                    <input type="text" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                      [ngModel]="editKey()" (ngModelChange)="editKey.set($event)"
                      [disabled]="!!editingId()" />
                  </div>
                  <div class="grid gap-2">
                    <label class="text-xs font-medium text-muted-foreground">Sujet</label>
                    <input type="text" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                      [ngModel]="editSubject()" (ngModelChange)="editSubject.set($event)" />
                  </div>
                  <div class="grid gap-2">
                    <label class="text-xs font-medium text-muted-foreground">Description</label>
                    <input type="text" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                      [ngModel]="editDescription()" (ngModelChange)="editDescription.set($event)" />
                  </div>
                </div>
              }
              @case ('html') {
                <div class="space-y-4">
                  <div class="grid gap-2">
                    <label class="text-xs font-medium text-muted-foreground">Corps HTML</label>
                    <textarea class="flex min-h-[400px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm font-mono"
                      [ngModel]="editHtml()" (ngModelChange)="editHtml.set($event)"></textarea>
                  </div>
                  <div class="grid gap-2">
                    <label class="text-xs font-medium text-muted-foreground">Variables disponibles</label>
                    <div class="flex flex-wrap gap-2">
                      @for (v of availableVariables(); track v) {
                        <button class="inline-flex items-center gap-1 rounded-md border px-2 py-1 text-xs font-mono hover:bg-muted"
                          (click)="insertVariable(v)">
                          {{ varSyntax(v) }}
                        </button>
                      }
                    </div>
                  </div>
                </div>
              }
            }
          </div>

          <!-- RIGHT: Live Preview -->
          <div class="flex flex-col rounded-xl border bg-card">
            <div class="flex items-center justify-between px-4 py-4 border-b">
              <h2 class="font-medium text-lg">Apercu</h2>
              <span class="text-xs text-muted-foreground">Sujet : {{ editSubject() }}</span>
            </div>
            <div class="flex-1 p-4 overflow-auto bg-stone-100 dark:bg-stone-800 rounded-b-xl">
              <div class="mx-auto max-w-[600px] bg-white shadow-lg rounded-lg overflow-hidden">
                <!-- Email Header -->
                <div class="px-6 py-4 border-b">
                  <div class="flex items-center gap-3">
                    <div class="h-10 w-10 rounded-full bg-primary flex items-center justify-center text-primary-foreground font-bold text-sm">NG</div>
                    <div>
                      <p class="text-sm font-medium">NG-STARs</p>
                      <p class="text-xs text-muted-foreground">noreply&#64;ng-fields.ngs.tg</p>
                    </div>
                  </div>
                </div>
                <!-- Email Body -->
                <div class="px-6 py-6" [innerHTML]="sanitizedHtml()"></div>
                <!-- Email Footer -->
                <div class="px-6 py-3 border-t bg-gray-50">
                  <p class="text-xs text-gray-400 text-center">NG-STARs - Email automatique</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      }

      <!-- Templates List -->
      @if (!editing()) {
        <div class="rounded-lg border bg-card shadow-sm">
          <div class="p-6 border-b">
            <h3 class="text-lg font-semibold">Templates existants</h3>
            <p class="text-sm text-muted-foreground">{{ templates().length }} template(s)</p>
          </div>
          <div class="overflow-x-auto">
            <table class="w-full text-sm">
              <thead>
                <tr class="border-b bg-muted/50 text-left">
                  <th class="px-6 py-3 text-xs font-medium text-muted-foreground uppercase">Nom</th>
                  <th class="px-6 py-3 text-xs font-medium text-muted-foreground uppercase">Cle</th>
                  <th class="px-6 py-3 text-xs font-medium text-muted-foreground uppercase">Sujet</th>
                  <th class="px-6 py-3 text-xs font-medium text-muted-foreground uppercase">Statut</th>
                  <th class="px-6 py-3 text-xs font-medium text-muted-foreground uppercase text-right">Actions</th>
                </tr>
              </thead>
              <tbody class="divide-y">
                @for (tpl of templates(); track tpl.id) {
                  <tr class="hover:bg-muted/30 transition-colors">
                    <td class="px-6 py-4">
                      <p class="font-medium">{{ tpl.name }}</p>
                      @if (tpl.description) {
                        <p class="text-xs text-muted-foreground">{{ tpl.description }}</p>
                      }
                    </td>
                    <td class="px-6 py-4">
                      <code class="rounded bg-muted px-2 py-0.5 text-xs">{{ tpl.templateKey }}</code>
                    </td>
                    <td class="px-6 py-4 text-muted-foreground max-w-xs truncate">{{ tpl.subject }}</td>
                    <td class="px-6 py-4">
                      <span class="rounded-full px-2 py-0.5 text-xs font-medium"
                        [class.bg-green-100]="tpl.isActive" [class.text-green-800]="tpl.isActive"
                        [class.bg-gray-100]="!tpl.isActive" [class.text-gray-800]="!tpl.isActive">
                        {{ tpl.isActive ? 'Actif' : 'Inactif' }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-right">
                      <div class="flex items-center justify-end gap-2">
                        <button class="rounded-md border px-3 py-1.5 text-xs font-medium hover:bg-muted"
                          (click)="edit(tpl)">Modifier</button>
                        <button class="rounded-md border px-3 py-1.5 text-xs font-medium text-destructive hover:bg-destructive/10"
                          (click)="remove(tpl)">Supprimer</button>
                      </div>
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="5" class="px-6 py-12 text-center text-muted-foreground">
                      Aucun template email. Creez votre premier template.
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class EmailTemplatesComponent implements OnInit {
  private tplService = inject(EmailTemplateService);
  private sanitizer = inject(DomSanitizer);

  templates = signal<EmailTemplateResponse[]>([]);
  editing = signal(false);
  editingId = signal<string | null>(null);
  editName = signal('');
  editDescription = signal('');
  editKey = signal('');
  editSubject = signal('');
  editHtml = signal(this.defaultHtml());
  activeTab = signal('content');

  tabs = signal([
    { id: 'content', label: 'Contenu' },
    { id: 'html', label: 'HTML' },
  ]);

  availableVariables = computed(() => {
    const key = this.editKey();
    return EMAIL_VARIABLES[key] ?? EMAIL_VARIABLES['CUSTOM'] ?? [];
  });

  sanitizedHtml = computed(() => {
    return this.sanitizer.bypassSecurityTrustHtml(this.previewHtml());
  });

  private previewHtml = computed(() => {
    let html = this.editHtml();
    html = html.replace(/\{\{firstName\}\}/g, '<strong>John</strong>');
    html = html.replace(/\{\{email\}\}/g, 'john&#64;example.com');
    html = html.replace(/\{\{tempPassword\}\}/g, '<code>TempPass123!</code>');
    html = html.replace(/\{\{loginUrl\}\}/g, 'http://localhost:4200/login');
    html = html.replace(/\{\{resetLink\}\}/g, 'http://localhost:4200/reset-password?token=xxx');
    return html || '<p class="text-gray-400 italic">Apercu de l email...</p>';
  });

  ngOnInit(): void {
    this.tplService.list().subscribe(t => this.templates.set(t));
  }

  openCreate(): void {
    this.editingId.set(null);
    this.editing.set(true);
    this.editName.set('');
    this.editDescription.set('');
    this.editKey.set('');
    this.editSubject.set('');
    this.editHtml.set(this.defaultHtml());
  }

  edit(tpl: EmailTemplateResponse): void {
    this.editingId.set(tpl.id);
    this.editing.set(true);
    this.editName.set(tpl.name);
    this.editDescription.set(tpl.description ?? '');
    this.editKey.set(tpl.templateKey);
    this.editSubject.set(tpl.subject);
    this.editHtml.set(tpl.bodyHtml);
  }

  cancelEdit(): void {
    this.editing.set(false);
    this.editingId.set(null);
  }

  save(): void {
    if (this.editingId()) {
      this.tplService.update(this.editingId()!, {
        name: this.editName(),
        description: this.editDescription(),
        subject: this.editSubject(),
        bodyHtml: this.editHtml(),
      }).subscribe({ next: () => { this.cancelEdit(); this.load(); } });
    } else {
      this.tplService.create({
        name: this.editName(),
        description: this.editDescription(),
        templateKey: this.editKey(),
        subject: this.editSubject(),
        bodyHtml: this.editHtml(),
      }).subscribe({ next: () => { this.cancelEdit(); this.load(); } });
    }
  }

  remove(tpl: EmailTemplateResponse): void {
    if (confirm(`Supprimer "${tpl.name}" ?`)) {
      this.tplService.delete(tpl.id).subscribe(() => this.load());
    }
  }

  insertVariable(varName: string): void {
    this.editHtml.update(h => h + `{{${varName}}}`);
  }

  varSyntax(v: string): string {
    return `{{${v}}}`;
  }

  private load(): void {
    this.tplService.list().subscribe(t => this.templates.set(t));
  }

  private defaultHtml(): string {
    return `<div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
  <h2 style="color: #40946e;">Titre de l email</h2>
  <p>Bonjour {{firstName}},</p>
  <p>Contenu de l email ici...</p>
  <hr style="border: none; border-top: 1px solid #ddd; margin: 24px 0;" />
  <p style="font-size: 12px; color: #888;">NG-STARs - Email automatique</p>
</div>`;
  }
}
