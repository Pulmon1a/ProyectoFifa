import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-knockout',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './knockout.html',
  styleUrl: './knockout.css'
})
export class KnockoutComponent implements OnInit {
  bracket: any = {};
  teams: any[] = [];
  loading = true;
  error = '';
  success = '';
  closing = false;
  bracketStarted = false;

  editingMatch: any = null;
  knockoutForm = { homeGoals: 0, awayGoals: 0, played: true };

  rounds = ['Round of 32', 'Round of 16', 'Quarter Finals', 'Semi Finals', 'Third Place', 'Final'];

  constructor(public apiService: ApiService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadTeams();
    this.loadBracket();
  }

  loadTeams() {
    this.apiService.getTeams().subscribe({
      next: (teams) => { this.teams = teams; this.cdr.detectChanges(); },
      error: () => {}
    });
  }

  loadBracket() {
    this.loading = true;
    this.cdr.detectChanges();
    this.apiService.getBracket().subscribe({
      next: (bracket) => {
        this.bracket = bracket;
        this.bracketStarted = true;
        this.error = '';
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.bracketStarted = false;
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  closeGroupStage() {
    this.closing = true;
    this.error = '';
    this.success = '';
    this.apiService.closeGroupStage().subscribe({
      next: () => {
        this.success = 'Fase de grupos cerrada. ¡Bracket generado!';
        this.closing = false;
        this.loadBracket();
      },
      error: (err) => {
  try {
    const body = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
    this.error = body?.message || 'Error al cerrar la fase de grupos.';
  } catch {
    this.error = err.error || 'Error al cerrar la fase de grupos.';
  }
  this.closing = false;
  this.cdr.detectChanges();
}
    });
  }

  getTeamName(id: string): string {
    const team = this.teams.find(t => t.id === id);
    return team ? team.name : id;
  }

  openEditMatch(match: any) {
    this.editingMatch = match;
    this.knockoutForm = { homeGoals: 0, awayGoals: 0, played: true };
  }

  saveKnockoutResult() {
    this.apiService.updateKnockoutMatch(this.editingMatch.id, this.knockoutForm).subscribe({
      next: () => {
        this.success = 'Resultado registrado correctamente.';
        this.editingMatch = null;
        this.loadBracket();
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

  get champion(): string {
    const final = this.bracket['Final'];
    if (!final || final.length === 0) return '';
    const finalMatch = final[0];
    return finalMatch.played ? finalMatch.winnerId : '';
  }

  get runnerUp(): string {
    const final = this.bracket['Final'];
    if (!final || final.length === 0) return '';
    const finalMatch = final[0];
    if (!finalMatch.played) return '';
    return finalMatch.winnerId === finalMatch.homeTeamId ? finalMatch.awayTeamId : finalMatch.homeTeamId;
  }

  get thirdPlace(): string {
    const third = this.bracket['Third Place'];
    if (!third || third.length === 0) return '';
    const thirdMatch = third[0];
    return thirdMatch.played ? thirdMatch.winnerId : '';
  }

  get tournamentFinished(): boolean {
    return !!this.champion && !!this.thirdPlace;
  }
}