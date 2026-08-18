import { CurrencyPipe } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { ApiService } from '../../core/api/api.service';
import { YearMonth } from '../../core/models/dre.models';

@Component({
  selector: 'app-annual',
  imports: [CurrencyPipe, FormsModule, MatFormFieldModule, MatSelectModule],
  templateUrl: './annual.component.html',
  styleUrl: './annual.component.scss'
})
export class AnnualComponent implements OnInit {
  readonly months = [
    'Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun',
    'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'
  ];
  readonly years = [2025, 2026, 2027, 2028];
  year = 2026;
  readonly rows = signal<YearMonth[]>([]);
  readonly error = signal('');

  constructor(private readonly api: ApiService) {}

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.api.loadYear(this.year).subscribe({
      next: (rows) => this.rows.set(rows),
      error: () => this.error.set('Não foi possível carregar o consolidado anual.')
    });
  }
}
