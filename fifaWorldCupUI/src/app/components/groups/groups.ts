import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-groups',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './groups.html',
  styleUrl: './groups.css'
})
export class GroupsComponent implements OnInit {
  groupIds = ['A','B','C','D','E','F','G','H','I','J','K','L'];
  groupsData: { id: string, standings: any[] }[] = [];
  loading = true;
  error = '';

  constructor(private apiService: ApiService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadGroups();
  }

  loadGroups() {
    this.loading = true;
    this.error = '';
    this.groupsData = [];
    this.cdr.detectChanges();

    const requests = this.groupIds.map(id =>
      this.apiService.getGroupStandings(id).pipe(
        catchError(() => of([]))
      )
    );

    forkJoin(requests).subscribe({
      next: (results) => {
        this.groupsData = this.groupIds.map((id, i) => ({
          id,
          standings: results[i] || []
        }));
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'Error al cargar los grupos.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}