import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  CatalogItem,
  CategoryLookup,
  DreMonth,
  GroupLookup,
  YearMonth
} from '../models/dre.models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly base = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  loadDre(year: number, month: number): Observable<DreMonth> {
    return this.http.get<DreMonth>(`${this.base}/dre`, { params: { year, month } });
  }

  loadYear(year: number): Observable<YearMonth[]> {
    return this.http.get<YearMonth[]>(`${this.base}/dre/year/${year}`);
  }

  updateEntry(
    lineItemId: string,
    year: number,
    month: number,
    forecast: number,
    paidAmount: number | null
  ): Observable<DreMonth> {
    return this.http.put<DreMonth>(`${this.base}/entries/${lineItemId}`, { forecast, paidAmount }, {
      params: { year, month }
    });
  }

  updateClosing(year: number, month: number, actualRemaining: number | null): Observable<DreMonth> {
    return this.http.put<DreMonth>(`${this.base}/months/${year}/${month}/closing`, { actualRemaining });
  }

  lineItems(): Observable<CatalogItem[]> {
    return this.http.get<CatalogItem[]>(`${this.base}/line-items`);
  }

  categories(): Observable<CategoryLookup[]> {
    return this.http.get<CategoryLookup[]>(`${this.base}/categories`);
  }

  groups(): Observable<GroupLookup[]> {
    return this.http.get<GroupLookup[]>(`${this.base}/groups`);
  }

  createLineItem(categoryId: string, name: string): Observable<CatalogItem> {
    return this.http.post<CatalogItem>(`${this.base}/line-items`, { categoryId, name });
  }

  updateLineItem(id: string, patch: { name?: string; active?: boolean }): Observable<CatalogItem> {
    return this.http.patch<CatalogItem>(`${this.base}/line-items/${id}`, patch);
  }

  createCategory(groupId: string, name: string): Observable<CategoryLookup> {
    return this.http.post<CategoryLookup>(`${this.base}/categories`, { groupId, name });
  }
}
