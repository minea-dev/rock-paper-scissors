import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { SettingsComponent } from './components/settings/settings.component';
import { HistoryComponent } from './components/history/history.component';
import {LoginComponent} from './components/login/login.component';
import {RegisterComponent} from './components/register/register.component';
import {PlayComponent} from './components/play/play.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'settings', component: SettingsComponent },
  { path: 'history', component: HistoryComponent },
  { path: 'play', component: PlayComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
];
