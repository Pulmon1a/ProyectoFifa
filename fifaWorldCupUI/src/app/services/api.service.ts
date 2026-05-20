import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private baseUrl = '/api/v1';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const credentials = localStorage.getItem('fifa_credentials');
    if (credentials) {
      return new HttpHeaders({
        'Authorization': 'Basic ' + credentials,
        'Content-Type': 'application/json'
      });
    }
    return new HttpHeaders({ 'Content-Type': 'application/json' });
  }

  // ── TEAMS ─────────────────────────────────────────────
  getTeams(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/teams`);
  }

  getTeamById(id: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/teams/${id}`);
  }

  createTeam(team: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/teams`, team, {
      headers: this.getAuthHeaders()
    });
  }

  updateTeam(id: string, team: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/teams/${id}`, team, {
      headers: this.getAuthHeaders()
    });
  }

  deleteTeam(id: string): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/teams/${id}`, {
      headers: this.getAuthHeaders()
    });
  }

  // ── MATCHES ───────────────────────────────────────────
  getMatchesByTeam(teamId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/matches/teams/${teamId}`);
  }

  getMatchesByGroup(groupId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/matches/groups/${groupId}`);
  }

  createMatch(match: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/matches`, match, {
      headers: this.getAuthHeaders()
    });
  }

  updateMatch(id: string, match: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/matches/${id}`, match, {
      headers: this.getAuthHeaders()
    });
  }

  deleteMatch(id: string): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/matches/${id}`, {
      headers: this.getAuthHeaders()
    });
  }

  // ── GROUPS ────────────────────────────────────────────
  getGroups(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/groups`);
  }

  getGroupById(id: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/groups/${id}`);
  }

  getGroupStandings(id: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/groups/${id}/standings`);
  }

  generateMatches(groupId: string): Observable<any> {
  return this.http.post(`${this.baseUrl}/groups/${groupId}/generate-matches`, {}, {
    headers: this.getAuthHeaders(),
    responseType: 'text'
  });
}


  // ── KNOCKOUT ──────────────────────────────────────────
  getBracket(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/knockout/bracket`);
  }

  closeGroupStage(): Observable<any> {
  return this.http.post(`${this.baseUrl}/knockout/close-group-stage`, {}, {
    headers: this.getAuthHeaders(),
    responseType: 'text'
  });
}

  updateKnockoutMatch(id: string, match: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/knockout/${id}`, match, {
      headers: this.getAuthHeaders()
    });
  }

  // ── AUTH ──────────────────────────────────────────────
  login(username: string, password: string): Observable<any> {
    const credentials = btoa(`${username}:${password}`);
    const headers = new HttpHeaders({
      'Authorization': 'Basic ' + credentials,
      'Content-Type': 'application/json'
    });
    return this.http.get<any[]>(`${this.baseUrl}/teams`, { headers });
  }

  saveCredentials(username: string, password: string): void {
    const credentials = btoa(`${username}:${password}`);
    localStorage.setItem('fifa_credentials', credentials);
    localStorage.setItem('fifa_username', username);
    const role = username === 'fifa' ? 'FIFA' : 'FAN';
    localStorage.setItem('fifa_role', role);
  }

  logout(): void {
    localStorage.removeItem('fifa_credentials');
    localStorage.removeItem('fifa_username');
    localStorage.removeItem('fifa_role');
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('fifa_credentials');
  }

  getUsername(): string {
    return localStorage.getItem('fifa_username') || '';
  }

  getRole(): string {
    return localStorage.getItem('fifa_role') || '';
  }

  isAdmin(): boolean {
    return localStorage.getItem('fifa_role') === 'FIFA';
  }
}