import { Component, signal, computed, OnInit, inject, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IconComponent } from '../../../../shared/ui/icon/icon.component';
import { PdfTemplateService } from '../../../../core/services/pdf-template.service';
import {
  PdfTemplateResponse,
  PdfTemplateConfig,
  PdfTemplateColumn,
  AVAILABLE_FIELDS,
} from '../../../../shared/models/pdf-template.dto';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-pdf-templates',
  standalone: true,
  imports: [CommonModule, FormsModule, IconComponent],
  template: `
    <div class="flex flex-col gap-6 p-4 md:p-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold tracking-tight">Templates PDF</h1>
          <p class="text-sm text-muted-foreground">Personnaliser le format des rapports PDF</p>
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
              @for (tab of editorTabs(); track tab.id) {
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
              @case ('general') {
                <div class="space-y-4">
                  <div class="grid gap-2">
                    <label class="text-xs font-medium text-muted-foreground">Nom du template</label>
                    <input type="text" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                      [ngModel]="editName()" (ngModelChange)="editName.set($event)" />
                  </div>
                  <div class="grid gap-2">
                    <label class="text-xs font-medium text-muted-foreground">Description</label>
                    <input type="text" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                      [ngModel]="editDescription()" (ngModelChange)="editDescription.set($event)" />
                  </div>
                  <div class="grid gap-2">
                    <label class="text-xs font-medium text-muted-foreground">Titre du rapport</label>
                    <input type="text" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                      [ngModel]="config().title" (ngModelChange)="updateField('title', $event)" />
                  </div>
                  <div class="grid gap-2">
                    <label class="text-xs font-medium text-muted-foreground">Sous-titre</label>
                    <input type="text" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                      [ngModel]="config().subtitle" (ngModelChange)="updateField('subtitle', $event)" />
                  </div>
                  <div class="grid grid-cols-2 gap-4">
                    <div class="grid gap-2">
                      <label class="text-xs font-medium text-muted-foreground">Orientation</label>
                      <select class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                        [ngModel]="config().orientation" (ngModelChange)="updateField('orientation', $event)">
                        <option value="LANDSCAPE">Paysage</option>
                        <option value="PORTRAIT">Portrait</option>
                      </select>
                    </div>
                    <div class="grid gap-2">
                      <label class="text-xs font-medium text-muted-foreground">Format</label>
                      <select class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                        [ngModel]="config().pageSize" (ngModelChange)="updateField('pageSize', $event)">
                        <option value="A4">A4</option>
                        <option value="LETTER">Lettre</option>
                      </select>
                    </div>
                  </div>
                </div>
              }
              @case ('header') {
                <div class="space-y-4">
                  <div class="grid gap-2">
                    <label class="text-xs font-medium text-muted-foreground">Nom de l'entreprise</label>
                    <input type="text" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                      [ngModel]="config().header.companyName" (ngModelChange)="updateNested('header', 'companyName', $event)" />
                  </div>
                  <div class="grid gap-2">
                    <label class="text-xs font-medium text-muted-foreground">Taille du titre</label>
                    <input type="number" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                      [ngModel]="config().fonts.titleSize" (ngModelChange)="updateNested('fonts', 'titleSize', +$event)" />
                  </div>
                  <div class="grid grid-cols-2 gap-4">
                    <div class="grid gap-2">
                      <label class="text-xs font-medium text-muted-foreground">Couleur d'en-tête</label>
                      <div class="flex gap-2">
                        <input type="color" class="h-10 w-10 rounded-md border cursor-pointer"
                          [ngModel]="config().header.backgroundColor" (ngModelChange)="updateNested('header', 'backgroundColor', $event)" />
                        <input type="text" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                          [ngModel]="config().header.backgroundColor" (ngModelChange)="updateNested('header', 'backgroundColor', $event)" />
                      </div>
                    </div>
                    <div class="grid gap-2">
                      <label class="text-xs font-medium text-muted-foreground">Couleur du texte</label>
                      <div class="flex gap-2">
                        <input type="color" class="h-10 w-10 rounded-md border cursor-pointer"
                          [ngModel]="config().header.textColor" (ngModelChange)="updateNested('header', 'textColor', $event)" />
                        <input type="text" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                          [ngModel]="config().header.textColor" (ngModelChange)="updateNested('header', 'textColor', $event)" />
                      </div>
                    </div>
                  </div>
                </div>
              }
              @case ('columns') {
                <div class="space-y-4">
                  <p class="text-xs text-muted-foreground">Cochez pour afficher, up/down pour réordonner</p>
                  <div class="space-y-2 max-h-80 overflow-y-auto">
                    @for (col of config().columns; track col.field; let i = $index) {
                      <div class="flex items-center gap-2 rounded-md border p-2 bg-background">
                        <input type="checkbox" class="h-4 w-4" [checked]="col.visible" (change)="toggleCol(i)" />
                        <span class="text-sm flex-1">{{ col.label }}</span>
                        <input type="number" class="h-8 w-14 rounded border border-input bg-background px-1 text-xs text-center"
                          [ngModel]="col.width" (ngModelChange)="updateColWidth(i, +$event)" />
                        @if (i > 0) {
                          <button class="h-6 w-6 flex items-center justify-center rounded hover:bg-muted" (click)="moveCol(i, -1)">
                            <app-icon name="chevron-up" />
                          </button>
                        }
                        @if (i < config().columns.length - 1) {
                          <button class="h-6 w-6 flex items-center justify-center rounded hover:bg-muted" (click)="moveCol(i, 1)">
                            <app-icon name="chevron-down" />
                          </button>
                        }
                      </div>
                    }
                  </div>
                  <button class="inline-flex items-center gap-1 rounded-md border px-3 py-1.5 text-xs font-medium hover:bg-muted"
                    (click)="addCol()">
                    <app-icon name="plus" /> Ajouter une colonne
                  </button>
                </div>
              }
              @case ('footer') {
                <div class="space-y-4">
                  <div class="grid gap-2">
                    <label class="text-xs font-medium text-muted-foreground">Texte du pied de page</label>
                    <input type="text" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                      [ngModel]="config().footer.text" (ngModelChange)="updateNested('footer', 'text', $event)" />
                    <p class="text-xs text-muted-foreground">Utilisez {{ datePlaceholder() }} pour insérer la date</p>
                  </div>
                  <div class="grid grid-cols-2 gap-4">
                    <div class="grid gap-2">
                      <label class="text-xs font-medium text-muted-foreground">Taille police</label>
                      <input type="number" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                        [ngModel]="config().footer.fontSize" (ngModelChange)="updateNested('footer', 'fontSize', +$event)" />
                    </div>
                    <div class="grid gap-2">
                      <label class="text-xs font-medium text-muted-foreground">Couleur</label>
                      <div class="flex gap-2">
                        <input type="color" class="h-10 w-10 rounded-md border cursor-pointer"
                          [ngModel]="config().footer.textColor" (ngModelChange)="updateNested('footer', 'textColor', $event)" />
                        <input type="text" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                          [ngModel]="config().footer.textColor" (ngModelChange)="updateNested('footer', 'textColor', $event)" />
                      </div>
                    </div>
                  </div>
                </div>
              }
            }
          </div>

          <!-- RIGHT: Live Preview -->
          <div class="flex flex-col rounded-xl border bg-card">
            <div class="flex items-center justify-between px-4 py-4">
              <h2 class="font-medium text-lg">Aperçu</h2>
              <button class="inline-flex items-center gap-1.5 rounded-md border px-3 py-1.5 text-xs font-medium hover:bg-muted">
                <app-icon name="download" /> Télécharger PDF
              </button>
            </div>
            <div class="flex-1 rounded-b-xl bg-stone-200 dark:bg-stone-800 p-4 overflow-auto">
              <div class="mx-auto origin-top-left"
                [style.width]="paperWidth() + 'px'"
                [style.transform]="'scale(' + paperScale() + ')'">
                <!-- Paper -->
                <div class="bg-neutral-50 shadow-lg flex flex-col gap-6 p-8"
                  [style.width]="paperWidth() + 'px'"
                  [style.min-height]="paperHeight() + 'px'">
                  <!-- Header -->
                  <header class="flex flex-col gap-4">
                    <h2 class="text-xl font-bold uppercase tracking-widest"
                      [style.color]="config().header.textColor">
                      {{ config().title || 'Rapport' }}
                    </h2>
                    @if (config().subtitle) {
                      <p class="text-sm text-neutral-500">{{ config().subtitle }}</p>
                    }
                    <p class="text-xs text-neutral-400">Total: 42 interventions</p>
                  </header>

                  <!-- Table -->
                  <div class="flex flex-col text-xs">
                    <div class="grid px-2 py-2 font-semibold uppercase"
                      [style.background]="config().header.backgroundColor"
                      [style.color]="config().header.textColor"
                      [style.grid-template-columns]="gridTemplate()">
                      @for (col of visibleColumns(); track col.field) {
                        <span>{{ col.label }}</span>
                      }
                    </div>
                    @for (row of previewRows; track row) {
                      <div class="grid px-2 py-2 border-b border-neutral-200"
                        [style.grid-template-columns]="gridTemplate()">
                        @for (val of row; track val) {
                          <span class="truncate">{{ val }}</span>
                        }
                      </div>
                    }
                  </div>

                  <!-- Footer -->
                  @if (config().footer.text) {
                    <footer class="mt-auto pt-4 text-xs"
                      [style.color]="config().footer.textColor">
                      {{ formatFooterDate(config().footer.text) }}
                    </footer>
                  }
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
                  <th class="px-6 py-3 text-xs font-medium text-muted-foreground uppercase">Colonnes</th>
                  <th class="px-6 py-3 text-xs font-medium text-muted-foreground uppercase">Orientation</th>
                  <th class="px-6 py-3 text-xs font-medium text-muted-foreground uppercase">Défaut</th>
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
                    <td class="px-6 py-4 text-muted-foreground">{{ getColumnCount(tpl) }}</td>
                    <td class="px-6 py-4 text-muted-foreground">{{ getOrientation(tpl) }}</td>
                    <td class="px-6 py-4">
                      @if (tpl.isDefault) {
                        <span class="rounded-full px-2 py-0.5 text-xs font-medium bg-primary/10 text-primary">Défaut</span>
                      }
                    </td>
                    <td class="px-6 py-4 text-right">
                      <div class="flex items-center justify-end gap-2">
                        <button class="rounded-md border px-3 py-1.5 text-xs font-medium hover:bg-muted"
                          (click)="edit(tpl)">Modifier</button>
                        @if (!tpl.isDefault) {
                          <button class="rounded-md border px-3 py-1.5 text-xs font-medium hover:bg-muted"
                            (click)="setDefault(tpl)">Défaut</button>
                        }
                        <button class="rounded-md border px-3 py-1.5 text-xs font-medium text-destructive hover:bg-destructive/10"
                          (click)="remove(tpl)">Supprimer</button>
                      </div>
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="5" class="px-6 py-12 text-center text-muted-foreground">
                      Aucun template. Créez votre premier template pour personnaliser les PDF.
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
export class PdfTemplatesComponent implements OnInit {
  private tplService = inject(PdfTemplateService);

  templates = signal<PdfTemplateResponse[]>([]);
  editing = signal(false);
  editingId = signal<string | null>(null);
  editName = signal('');
  editDescription = signal('');
  config = signal<PdfTemplateConfig>(this.defaultConfig());
  activeTab = signal('general');

  editorTabs = signal([
    { id: 'general', label: 'Général' },
    { id: 'header', label: 'En-tête' },
    { id: 'columns', label: 'Colonnes' },
    { id: 'footer', label: 'Pied de page' },
  ]);

  previewRows = [
    ['INT-001', 'Entreprise Togo', 'contact@togo.tg', 'Climatiseur', 'Daikin', 'Panne compresseur', 'Compresseur défectueux', 'Remplacement compresseur', 'TERMINE'],
    ['INT-002', 'Société KARA', 'info@kara.tg', 'Groupe électrogène', 'Caterpillar', 'Mise en service', 'Conforme', 'Mise en service effectuée', 'TERMINE'],
    ['INT-003', 'Hotel Lome', 'hotel@lome.tg', 'Chaudière', 'Viessmann', 'Fuite d\'eau', 'Joint usé', 'Remplacement joint', 'EN_COURS'],
    ['INT-004', 'Usine AGARA', 'agara@aga.tg', 'Compresseur', 'Atlas Copco', 'Vibration anormale', 'Palier usé', 'En attente pièces', 'EN_ATTENTE'],
  ];

  paperWidth = computed(() => this.config().orientation === 'PORTRAIT' ? 595 : 842);
  paperHeight = computed(() => this.config().orientation === 'PORTRAIT' ? 842 : 595);
  paperScale = computed(() => 0.55);

  visibleColumns = computed(() => this.config().columns.filter(c => c.visible));

  gridTemplate = computed(() => {
    const cols = this.visibleColumns();
    if (cols.length === 0) return 'repeat(1, 1fr)';
    const total = cols.reduce((s, c) => s + c.width, 0);
    return cols.map(c => `${c.width}fr`).join(' ');
  });

  ngOnInit(): void {
    this.tplService.list().subscribe(t => this.templates.set(t));
  }

  openCreate(): void {
    this.editingId.set(null);
    this.editing.set(true);
    this.editName.set('');
    this.editDescription.set('');
    this.config.set(this.defaultConfig());
  }

  edit(tpl: PdfTemplateResponse): void {
    this.editingId.set(tpl.id);
    this.editing.set(true);
    this.editName.set(tpl.name);
    this.editDescription.set(tpl.description ?? '');
    try { this.config.set(JSON.parse(tpl.config)); } catch { this.config.set(this.defaultConfig()); }
  }

  cancelEdit(): void {
    this.editing.set(false);
    this.editingId.set(null);
  }

  save(): void {
    const json = JSON.stringify(this.config());
    const obs = this.editingId()
      ? this.tplService.update(this.editingId()!, { name: this.editName(), description: this.editDescription(), config: json })
      : this.tplService.create({ name: this.editName(), description: this.editDescription(), config: json });
    obs.subscribe({ next: () => { this.cancelEdit(); this.tplService.list().subscribe(t => this.templates.set(t)); } });
  }

  remove(tpl: PdfTemplateResponse): void {
    if (confirm(`Supprimer "${tpl.name}" ?`)) {
      this.tplService.delete(tpl.id).subscribe(() => this.tplService.list().subscribe(t => this.templates.set(t)));
    }
  }

  setDefault(tpl: PdfTemplateResponse): void {
    this.tplService.update(tpl.id, { isDefault: true }).subscribe(() => this.tplService.list().subscribe(t => this.templates.set(t)));
  }

  updateField(field: string, value: string): void {
    this.config.update(c => ({ ...c, [field]: value }));
  }

  updateNested(parent: string, field: string, value: string | number): void {
    this.config.update(c => ({ ...c, [parent]: { ...(c as any)[parent], [field]: value } }));
  }

  toggleCol(i: number): void {
    this.config.update(c => { const cols = [...c.columns]; cols[i] = { ...cols[i], visible: !cols[i].visible }; return { ...c, columns: cols }; });
  }

  updateColWidth(i: number, w: number): void {
    this.config.update(c => { const cols = [...c.columns]; cols[i] = { ...cols[i], width: w }; return { ...c, columns: cols }; });
  }

  moveCol(i: number, dir: number): void {
    this.config.update(c => { const cols = [...c.columns]; const j = i + dir; [cols[i], cols[j]] = [cols[j], cols[i]]; return { ...c, columns: cols }; });
  }

  addCol(): void {
    const used = new Set(this.config().columns.map(c => c.field));
    const avail = AVAILABLE_FIELDS.filter(f => !used.has(f.field));
    if (!avail.length) return;
    const f = avail[0];
    this.config.update(c => ({ ...c, columns: [...c.columns, { field: f.field, label: f.label, width: 10, visible: true }] }));
  }

  getColumnCount(tpl: PdfTemplateResponse): number {
    try { return (JSON.parse(tpl.config) as PdfTemplateConfig).columns.filter(c => c.visible).length; } catch { return 0; }
  }

  getOrientation(tpl: PdfTemplateResponse): string {
    try { return (JSON.parse(tpl.config) as PdfTemplateConfig).orientation === 'PORTRAIT' ? 'Portrait' : 'Paysage'; } catch { return 'Paysage'; }
  }

  formatFooterDate(text: string): string {
    return text.replace('{date}', new Date().toLocaleDateString('fr-FR'));
  }

  datePlaceholder(): string {
    return '{date}';
  }

  private defaultConfig(): PdfTemplateConfig {
    return {
      title: 'Rapport des Interventions - NG-STARs', subtitle: '', orientation: 'LANDSCAPE', pageSize: 'A4',
      margins: { top: 20, bottom: 20, left: 20, right: 20 },
      header: { showLogo: false, companyName: 'NG-STARs', backgroundColor: '#2C3E50', textColor: '#FFFFFF', fontSize: 16 },
      columns: AVAILABLE_FIELDS.filter(f => ['reference','clientName','clientEmail','equipmentType','equipmentBrand','reportedIssue','diagnosis','workDone','status'].includes(f.field))
        .map(f => ({ field: f.field, label: f.label, width: 10, visible: true })),
      footer: { text: 'NG-STARs - Rapport généré le {date}', fontSize: 8, textColor: '#888888' },
      fonts: { titleSize: 16, headerSize: 8, cellSize: 7, titleFont: 'HELVETICA_BOLD', headerFont: 'HELVETICA_BOLD', cellFont: 'HELVETICA' },
    };
  }
}
