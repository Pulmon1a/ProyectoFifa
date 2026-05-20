import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-teams',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './teams.html',
  styleUrl: './teams.css'
})
export class TeamsComponent implements OnInit {
  teams: any[] = [];
  loading = false;
  error = '';
  success = '';

  showForm = false;
  editMode = false;

  groupIds = ['A','B','C','D','E','F','G','H','I','J','K','L'];
  confederations = ['UEFA','CONMEBOL','CONCACAF','CAF','AFC','OFC'];

  form = {
    id: '',
    name: '',
    confederation: '',
    groupId: ''
  };

  constructor(public apiService: ApiService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadTeams();
  }

  loadTeams() {
    this.loading = true;
    this.cdr.detectChanges();
    this.apiService.getTeams().subscribe({
      next: (teams) => {
        this.teams = teams;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'Error al cargar equipos.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  openCreate() {
    this.editMode = false;
    this.form = { id: '', name: '', confederation: '', groupId: '' };
    this.showForm = true;
    this.error = '';
    this.success = '';
  }

  openEdit(team: any) {
    this.editMode = true;
    this.form = { ...team };
    this.showForm = true;
    this.error = '';
    this.success = '';
  }

  save() {
    if (!this.form.id.trim() || !this.form.name.trim() || !this.form.confederation || !this.form.groupId) {
      this.error = 'Todos los campos son obligatorios.';
      this.cdr.detectChanges();
      return;
    }

    if (this.editMode) {
      this.apiService.updateTeam(this.form.id, this.form).subscribe({
        next: () => {
          this.success = 'Equipo actualizado correctamente.';
          this.showForm = false;
          this.loadTeams();
        },
        error: (err) => {
          this.error = err.error?.message || 'Error al actualizar.';
          this.cdr.detectChanges();
        }
      });
    } else {
      this.apiService.createTeam(this.form).subscribe({
        next: () => {
          this.success = 'Equipo creado correctamente.';
          this.showForm = false;
          this.loadTeams();
        },
        error: (err) => {
          this.error = err.error?.message || 'Error al crear.';
          this.cdr.detectChanges();
        }
      });
    }
  }

  delete(id: string) {
    if (!confirm('¿Seguro que deseas eliminar este equipo?')) return;
    this.apiService.deleteTeam(id).subscribe({
      next: () => {
        this.success = 'Equipo eliminado.';
        this.loadTeams();
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al eliminar.';
        this.cdr.detectChanges();
      }
    });
  }

  cancel() {
    this.showForm = false;
    this.error = '';
  }
}