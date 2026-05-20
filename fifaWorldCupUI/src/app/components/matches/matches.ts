import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-matches',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './matches.html',
  styleUrl: './matches.css'
})
export class MatchesComponent implements OnInit {
  matches: any[] = [];
  teams: any[] = [];
  selectedGroupId = 'A';
  loading = false;
  error = '';
  success = '';
  groupIds = ['A','B','C','D','E','F','G','H','I','J','K','L'];

  editingMatch: any = null;
  matchForm: any = {
    id: '',
    groupId: '',
    homeTeamId: '',
    awayTeamId: '',
    homeGoals: 0,
    awayGoals: 0,
    homeYellowCards: 0,
    awayYellowCards: 0,
    homeRedCards: 0,
    awayRedCards: 0,
    played: true
  };

  constructor(public apiService: ApiService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadTeams();
    this.loadMatches();
  }

  loadTeams() {
    this.apiService.getTeams().subscribe({
      next: (teams) => { this.teams = teams; this.cdr.detectChanges(); },
      error: () => {}
    });
  }

  loadMatches() {
    this.loading = true;
    this.error = '';
    this.success = '';
    this.cdr.detectChanges();
    this.apiService.getMatchesByGroup(this.selectedGroupId).subscribe({
      next: (matches) => {
        this.matches = matches;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'Error al cargar partidos.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  getTeamName(id: string): string {
    const team = this.teams.find(t => t.id === id);
    return team ? team.name : id;
  }

  onGroupChange() {
    this.editingMatch = null;
    this.loadMatches();
  }

  generateMatches() {
  this.error = '';
  this.success = '';
  this.apiService.generateMatches(this.selectedGroupId).subscribe({
    next: () => {
      this.success = `Partidos del Grupo ${this.selectedGroupId} generados correctamente.`;
      this.loadMatches();
      this.cdr.detectChanges();
    },
    error: (err) => {
      try {
        const body = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.error = body?.message || 'Error al generar partidos.';
      } catch {
        this.error = 'Error al generar partidos.';
      }
      this.cdr.detectChanges();
    }
  });
}

  openEditMatch(match: any) {
    this.editingMatch = match;
    this.matchForm = {
      id: match.id,
      groupId: match.groupId,
      homeTeamId: match.homeTeamId,
      awayTeamId: match.awayTeamId,
      homeGoals: match.homeGoals || 0,
      awayGoals: match.awayGoals || 0,
      homeYellowCards: match.homeYellowCards || 0,
      awayYellowCards: match.awayYellowCards || 0,
      homeRedCards: match.homeRedCards || 0,
      awayRedCards: match.awayRedCards || 0,
      played: true
    };
  }

  saveMatch() {
  this.apiService.updateMatch(this.matchForm.id, this.matchForm).subscribe({
    next: () => {
      this.success = 'Resultado registrado correctamente.';
      this.editingMatch = null;
      this.loadMatches();
      this.cdr.detectChanges();
    },
    error: (err) => {
      this.error = err.error?.message || 'Error al guardar resultado.';
      this.cdr.detectChanges();
    }
  });
}

  cancelEdit() {
    this.editingMatch = null;
    this.error = '';
  }
}