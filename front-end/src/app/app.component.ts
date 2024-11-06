import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { SettingsComponent } from './components/settings/settings.component';
import { HistoryComponent } from './components/history/history.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HomeComponent, SettingsComponent, HistoryComponent],
  templateUrl: './app.component.html',
})
export class AppComponent {
  title = 'front-end';
}
