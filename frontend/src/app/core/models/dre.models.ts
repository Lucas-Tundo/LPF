export type GroupKind = 'RECEITA' | 'DESP_FIXA' | 'DESP_VAR';
export type PaymentStatus = 'PG' | 'ABERTO';

export interface MoneyTotals {
  forecast: number;
  paid: number;
  difference: number;
}

export interface DreTotals {
  revenue: MoneyTotals;
  subtotalA: MoneyTotals;
  subtotalB: MoneyTotals;
  totalAbPaid: number;
  expectedRemaining: number;
  actualRemaining: number | null;
  closingDifference: number | null;
}

export interface DreLine {
  id: string;
  name: string;
  status: PaymentStatus;
  forecast: number;
  paidAmount: number | null;
  difference: number | null;
}

export interface DreCategory {
  id: string;
  name: string;
  lines: DreLine[];
}

export interface DreGroup {
  id: string;
  kind: GroupKind;
  name: string;
  totals: MoneyTotals;
  categories: DreCategory[];
}

export interface DreMonth {
  year: number;
  month: number;
  groups: DreGroup[];
  totals: DreTotals;
}

export interface YearMonth {
  month: number;
  totals: DreTotals;
}

export interface CatalogItem {
  id: string;
  groupId: string;
  groupName: string;
  groupKind: GroupKind;
  categoryId: string;
  categoryName: string;
  name: string;
  active: boolean;
  sortOrder: number;
}

export interface CategoryLookup {
  id: string;
  groupId: string;
  groupName: string;
  groupKind: GroupKind;
  name: string;
}

export interface GroupLookup {
  id: string;
  name: string;
  kind: GroupKind;
}

export function groupKindLabel(kind: GroupKind): string {
  switch (kind) {
    case 'RECEITA':
      return 'Receita';
    case 'DESP_FIXA':
      return 'Despesa fixa';
    case 'DESP_VAR':
      return 'Despesa variável';
    default: {
      const neverKind: never = kind;
      return neverKind;
    }
  }
}
