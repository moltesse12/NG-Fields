import { Component, Input, signal , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Attachment {
  id: number;
  filename: string;
  size: number;
  uploadedAt: string;
  uploadedBy: string;
}

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-intervention-files',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="rounded-lg border bg-card p-4 md:p-6">
      <div class="text-sm font-medium mb-4">Fichiers attachés</div>

      @if (attachments.length === 0) {
        <p class="text-xs text-muted-foreground">Aucun fichier attaché.</p>
      } @else {
        <div class="space-y-2">
          @for (file of attachments; track file.id) {
            <div class="flex items-center justify-between rounded-md border p-2 hover:bg-muted/50">
              <div class="flex items-center gap-2 min-w-0">
                <svg class="size-4 text-muted-foreground flex-shrink-0" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
                <div class="min-w-0">
                  <p class="text-xs font-medium truncate">{{ file.filename }}</p>
                  <p class="text-xs text-muted-foreground">{{ formatSize(file.size) }} • {{ file.uploadedBy }}</p>
                </div>
              </div>
              <button class="ml-2 flex-shrink-0 text-muted-foreground hover:text-foreground">
                <svg class="size-4" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
              </button>
            </div>
          }
        </div>
      }

      <div class="mt-4 border-t pt-4">
        <div (click)="fileInput.click()" class="rounded-md border-2 border-dashed border-muted-foreground/30 p-4 text-center hover:border-muted-foreground/60 cursor-pointer transition-colors">
          <p class="text-xs text-muted-foreground">Déposer fichiers ici ou cliquer pour sélectionner</p>
          <input #fileInput type="file" class="hidden" (change)="onFileSelected($event)" />
        </div>
      </div>
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class InterventionFilesComponent {
  @Input() attachments: Attachment[] = [];
  files = signal<Attachment[]>([]);

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    // ponytail: log selected files, real upload when API exists
    console.log('Fichiers sélectionnés:', Array.from(input.files).map(f => f.name));
    input.value = '';
  }

  formatSize(bytes: number): string {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i];
  }
}
