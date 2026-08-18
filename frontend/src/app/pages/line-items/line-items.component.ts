import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { ApiService } from '../../core/api/api.service';
import { CatalogItem, CategoryLookup, GroupLookup, groupKindLabel } from '../../core/models/dre.models';

@Component({
  selector: 'app-line-items',
  imports: [FormsModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, MatSlideToggleModule],
  templateUrl: './line-items.component.html',
  styleUrl: './line-items.component.scss'
})
export class LineItemsComponent implements OnInit {
  readonly items = signal<CatalogItem[]>([]);
  readonly categories = signal<CategoryLookup[]>([]);
  readonly groups = signal<GroupLookup[]>([]);
  newItemName = '';
  newItemCategory = '';
  newCategoryName = '';
  newCategoryGroup = '';
  readonly error = signal('');
  readonly kindLabel = groupKindLabel;

  constructor(private readonly api: ApiService) {}

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.api.lineItems().subscribe((items) => this.items.set(items));
    this.api.categories().subscribe((categories) => this.categories.set(categories));
    this.api.groups().subscribe((groups) => this.groups.set(groups));
  }

  addLine(): void {
    if (!this.newItemCategory || !this.newItemName.trim()) {
      return;
    }
    this.api.createLineItem(this.newItemCategory, this.newItemName.trim()).subscribe({
      next: () => {
        this.newItemName = '';
        this.reload();
      },
      error: () => this.error.set('Não foi possível criar a linha.')
    });
  }

  addCategory(): void {
    if (!this.newCategoryGroup || !this.newCategoryName.trim()) {
      return;
    }
    this.api.createCategory(this.newCategoryGroup, this.newCategoryName.trim()).subscribe({
      next: () => {
        this.newCategoryName = '';
        this.reload();
      },
      error: () => this.error.set('Não foi possível criar a categoria.')
    });
  }

  rename(item: CatalogItem, name: string): void {
    const trimmed = name.trim();
    if (!trimmed || trimmed === item.name) {
      return;
    }
    this.api.updateLineItem(item.id, { name: trimmed }).subscribe({
      next: () => this.reload(),
      error: () => this.error.set('Não foi possível renomear.')
    });
  }

  toggle(item: CatalogItem, active: boolean): void {
    this.api.updateLineItem(item.id, { active }).subscribe({
      next: () => this.reload(),
      error: () => this.error.set('Não foi possível atualizar a linha.')
    });
  }
}
