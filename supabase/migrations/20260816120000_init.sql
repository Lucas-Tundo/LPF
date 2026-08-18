-- Espelho de backend/src/main/resources/db/migration/V1__init.sql
-- Use este arquivo no SQL Editor do Supabase se não quiser que o Flyway rode no primeiro boot.

create schema if not exists auth;

do $$
begin
  if not exists (select 1 from pg_roles where rolname = 'authenticated') then
    create role authenticated nologin;
  end if;
  if not exists (select 1 from pg_roles where rolname = 'anon') then
    create role anon nologin;
  end if;
end
$$;

do $$
begin
  if to_regprocedure('auth.uid()') is null then
    execute $fn$
      create function auth.uid()
      returns uuid
      language sql
      stable
      as $body$
        select nullif(current_setting('request.jwt.claim.sub', true), '')::uuid;
      $body$;
    $fn$;
  end if;
end
$$;

create or replace function public.set_updated_at()
returns trigger
language plpgsql
as $$
begin
  new.updated_at = now();
  return new;
end;
$$;

create table if not exists public.profiles (
  id uuid primary key,
  email text,
  display_name text,
  onboarded_at timestamptz,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.category_groups (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles (id) on delete cascade,
  kind text not null check (kind in ('RECEITA', 'DESP_FIXA', 'DESP_VAR')),
  name text not null,
  sort_order integer not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.categories (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles (id) on delete cascade,
  group_id uuid not null references public.category_groups (id) on delete cascade,
  name text not null,
  sort_order integer not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.line_items (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles (id) on delete cascade,
  category_id uuid not null references public.categories (id) on delete cascade,
  name text not null,
  sort_order integer not null,
  active boolean not null default true,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.monthly_entries (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles (id) on delete cascade,
  line_item_id uuid not null references public.line_items (id) on delete cascade,
  year integer not null,
  month integer not null check (month between 1 and 12),
  forecast numeric(14, 2) not null default 0,
  paid_amount numeric(14, 2),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (line_item_id, year, month)
);

create table if not exists public.month_closings (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles (id) on delete cascade,
  year integer not null,
  month integer not null check (month between 1 and 12),
  actual_remaining numeric(14, 2),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (user_id, year, month)
);

create index if not exists idx_category_groups_user on public.category_groups (user_id, sort_order);
create index if not exists idx_categories_user_group on public.categories (user_id, group_id, sort_order);
create index if not exists idx_line_items_user_category on public.line_items (user_id, category_id, sort_order);
create index if not exists idx_monthly_entries_user_period on public.monthly_entries (user_id, year, month);
create index if not exists idx_month_closings_user_period on public.month_closings (user_id, year, month);

drop trigger if exists trg_profiles_updated_at on public.profiles;
create trigger trg_profiles_updated_at
  before update on public.profiles
  for each row execute function public.set_updated_at();

drop trigger if exists trg_category_groups_updated_at on public.category_groups;
create trigger trg_category_groups_updated_at
  before update on public.category_groups
  for each row execute function public.set_updated_at();

drop trigger if exists trg_categories_updated_at on public.categories;
create trigger trg_categories_updated_at
  before update on public.categories
  for each row execute function public.set_updated_at();

drop trigger if exists trg_line_items_updated_at on public.line_items;
create trigger trg_line_items_updated_at
  before update on public.line_items
  for each row execute function public.set_updated_at();

drop trigger if exists trg_monthly_entries_updated_at on public.monthly_entries;
create trigger trg_monthly_entries_updated_at
  before update on public.monthly_entries
  for each row execute function public.set_updated_at();

drop trigger if exists trg_month_closings_updated_at on public.month_closings;
create trigger trg_month_closings_updated_at
  before update on public.month_closings
  for each row execute function public.set_updated_at();

alter table public.profiles enable row level security;
alter table public.category_groups enable row level security;
alter table public.categories enable row level security;
alter table public.line_items enable row level security;
alter table public.monthly_entries enable row level security;
alter table public.month_closings enable row level security;

drop policy if exists profiles_owner on public.profiles;
create policy profiles_owner on public.profiles
  for all
  using (id = auth.uid())
  with check (id = auth.uid());

drop policy if exists category_groups_owner on public.category_groups;
create policy category_groups_owner on public.category_groups
  for all
  using (user_id = auth.uid())
  with check (user_id = auth.uid());

drop policy if exists categories_owner on public.categories;
create policy categories_owner on public.categories
  for all
  using (user_id = auth.uid())
  with check (user_id = auth.uid());

drop policy if exists line_items_owner on public.line_items;
create policy line_items_owner on public.line_items
  for all
  using (user_id = auth.uid())
  with check (user_id = auth.uid());

drop policy if exists monthly_entries_owner on public.monthly_entries;
create policy monthly_entries_owner on public.monthly_entries
  for all
  using (user_id = auth.uid())
  with check (user_id = auth.uid());

drop policy if exists month_closings_owner on public.month_closings;
create policy month_closings_owner on public.month_closings
  for all
  using (user_id = auth.uid())
  with check (user_id = auth.uid());

grant usage on schema public to authenticated;
grant select, insert, update, delete on public.profiles to authenticated;
grant select, insert, update, delete on public.category_groups to authenticated;
grant select, insert, update, delete on public.categories to authenticated;
grant select, insert, update, delete on public.line_items to authenticated;
grant select, insert, update, delete on public.monthly_entries to authenticated;
grant select, insert, update, delete on public.month_closings to authenticated;

revoke all on public.profiles from anon;
revoke all on public.category_groups from anon;
revoke all on public.categories from anon;
revoke all on public.line_items from anon;
revoke all on public.monthly_entries from anon;
revoke all on public.month_closings from anon;
