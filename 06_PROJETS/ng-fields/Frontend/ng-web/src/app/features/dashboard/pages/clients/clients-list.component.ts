import { Component, OnInit, signal, ChangeDetectionStrategy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ClientResponse } from '../../../../shared/models/client.dto';
import { ClientService } from '../../../../core/services/client.service';
import { ClientsTableComponent } from './components/clients-table.component';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-clients-list',
  standalone: true,
  imports: [CommonModule, ClientsTableComponent],
  templateUrl: './clients-list.component.html',
  styleUrl: './clients-list.component.css',
})
export class ClientsListComponent implements OnInit {
  private clientService = inject(ClientService);
  private router = inject(Router);

  clients = signal<ClientResponse[]>([]);
  isLoading = signal(true);
  error = signal<string | null>(null);

  ngOnInit(): void {
    this.clientService.getClients(0, 100).subscribe({
      next: res => { this.clients.set(res.content); this.isLoading.set(false); },
      error: () => { this.error.set('Erreur lors du chargement des clients'); this.isLoading.set(false); },
    });
  }

  onClientClick(clientId: string): void {
    this.router.navigate(['/dashboard/clients', clientId]);
  }

  onCreateClient(): void {
    this.router.navigate(['/dashboard/clients/new']);
  }
}
