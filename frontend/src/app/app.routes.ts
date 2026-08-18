import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { ShellComponent } from './layout/shell.component';
import { LoginComponent } from './pages/login/login.component';
import { DreComponent } from './pages/dre/dre.component';
import { LineItemsComponent } from './pages/line-items/line-items.component';
import { AnnualComponent } from './pages/annual/annual.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dre' },
      { path: 'dre', component: DreComponent },
      { path: 'anual', component: AnnualComponent },
      { path: 'linhas', component: LineItemsComponent }
    ]
  }
];
