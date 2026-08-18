import { CurrencyPipe } from '@angular/common';
import { Component, OnInit, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { ApiService } from '../../core/api/api.service';
import { DreGroup, DreLine, DreMonth } from '../../core/models/dre.models';

@Component({
  selector: 'app-dre',
  imports: [
    FormsModule,
    CurrencyPipe,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatButtonModule,
    MatProgressBarModule
  ],
  templateUrl: './dre.component.html',
  styleUrl: './dre.component.scss'
})
export class DreComponent implements OnInit {
  readonly months = [
    'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
    'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
  ];
  readonly years = [2025, 2026, 2027, 2028];
  year = 2026;
  month = 8;
  remainingInput = '';
  readonly dre = signal<DreMonth | null>(null);
  readonly loading = signal(false);
  readonly error = signal('');
  readonly title = computed(() => {
    const current = this.dre();
    if (!current) {
      return 'DRE';
    }
    return `DRE — ${this.months[current.month - 1]} ${current.year}`;
  });

  constructor(private readonly api: ApiService) {}

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.loading.set(true);
    this.error.set('');
    this.api.loadDre(this.year, this.month).subscribe({
      next: (data) => {
        this.dre.set(data);
        this.remainingInput = data.totals.actualRemaining == null ? '' : String(data.totals.actualRemaining);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Não foi possível carregar o DRE. Confira o backend e as chaves do Supabase.');
        this.loading.set(false);
      }
    });
  }

  saveLine(line: DreLine, forecastRaw: string, paidRaw: string): void {
    const forecast = this.parseMoney(forecastRaw) ?? 0;
    const paidAmount = paidRaw.trim() === '' ? null : this.parseMoney(paidRaw);
    this.api.updateEntry(line.id, this.year, this.month, forecast, paidAmount).subscribe({
      next: (data) => this.dre.set(data),
      error: () => this.error.set('Falha ao salvar a linha.')
    });
  }

  saveClosing(): void {
    const actualRemaining = this.remainingInput.trim() === '' ? null : this.parseMoney(this.remainingInput);
    this.api.updateClosing(this.year, this.month, actualRemaining).subscribe({
      next: (data) => this.dre.set(data),
      error: () => this.error.set('Falha ao salvar o fechamento do mês.')
    });
  }

  statusClass(status: string): string {
    return status === 'PG' ? 'pg' : 'aberto';
  }

  paidText(line: DreLine): string {
    return line.paidAmount == null ? '' : String(line.paidAmount);
  }

  forecastText(line: DreLine): string {
    return String(line.forecast);
  }

  groupClass(group: DreGroup): string {
    switch (group.kind) {
      case 'RECEITA':
        return 'receita';
      case 'DESP_FIXA':
        return 'fixa';
      case 'DESP_VAR':
        return 'variavel';
      default: {
        const neverKind: never = group.kind;
        return neverKind;
      }
    }
  }

  private parseMoney(value: string): number | null {
    const normalized = value.replace(/\s/g, '').replace(',', '.');
    if (normalized === '') {
      return null;
    }
    const parsed = Number(normalized);
    return Number.isFinite(parsed) ? parsed : null;
  }
}
