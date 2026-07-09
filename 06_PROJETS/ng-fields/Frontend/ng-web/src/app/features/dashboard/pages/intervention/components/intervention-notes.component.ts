import { Component, Input, Output, EventEmitter , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-intervention-notes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="rounded-lg border bg-card p-4 md:p-6">
      <div class="text-sm font-medium mb-4">Notes et observations</div>

      @if (notes) {
        <div class="bg-muted p-3 rounded-md mb-4 border-l-2 border-blue-500">
          <p class="text-sm whitespace-pre-wrap">{{ notes }}</p>
        </div>
      }

      <div class="space-y-2">
        <textarea
          [(ngModel)]="newNote"
          placeholder="Ajouter une note interne..."
          class="w-full rounded-md border bg-background p-2 text-sm placeholder-muted-foreground focus:border-primary focus:outline-none"
          rows="3"
        ></textarea>
        <div class="flex gap-2 justify-end">
          <button (click)="cancel()" class="rounded-md px-3 py-1 text-xs font-medium text-foreground hover:bg-muted">
            Annuler
          </button>
          <button (click)="addNote()" [disabled]="!newNote.trim()" class="rounded-md bg-primary px-3 py-1 text-xs font-medium text-primary-foreground hover:bg-primary/90 disabled:opacity-50">
            Ajouter
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class InterventionNotesComponent {
  @Input() notes = '';
  @Output() onNoteAdded = new EventEmitter<string>();

  newNote = '';

  addNote(): void {
    if (this.newNote.trim()) {
      this.onNoteAdded.emit(this.newNote);
      this.newNote = '';
    }
  }

  cancel(): void {
    this.newNote = '';
  }
}
