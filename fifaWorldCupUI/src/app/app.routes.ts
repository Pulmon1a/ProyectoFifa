import { Routes } from '@angular/router';
import { GroupsComponent } from './components/groups/groups';
import { MatchesComponent } from './components/matches/matches';
import { KnockoutComponent } from './components/knockout/knockout';
import { TeamsComponent } from './components/teams/teams';
import { LoginComponent } from './components/login/login';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent, title: 'Login' },
  { path: 'groups', component: GroupsComponent, canActivate: [AuthGuard], title: 'Grupos' },
  { path: 'matches', component: MatchesComponent, canActivate: [AuthGuard], title: 'Partidos' },
  { path: 'knockout', component: KnockoutComponent, canActivate: [AuthGuard], title: 'Eliminatoria' },
  { path: 'teams', component: TeamsComponent, canActivate: [AuthGuard], title: 'Equipos' },
  { path: '**', redirectTo: 'login' }
];