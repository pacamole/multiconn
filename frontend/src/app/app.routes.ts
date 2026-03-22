import { Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { ChatComponent } from './features/chat/chat.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },

  { path: 'chat', component: ChatComponent },

  { path: '', redirectTo: '/login', pathMatch: 'full' },
];
