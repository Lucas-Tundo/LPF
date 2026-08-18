# LPF — DRE pessoal

Aplicativo monolítico de finanças pessoais baseado na planilha **DRE-Demonstrativo de Resultado 2026**.

- Backend: Java 21 + Spring Boot (arquitetura hexagonal)
- Frontend: Angular 19
- Banco e login Google: Supabase Auth + Postgres

Em desenvolvimento o Angular roda em `http://localhost:4200` e o Spring em `http://localhost:8080`. Em produção o Spring serve o `dist` do Angular e a API no mesmo processo.

## Arquitetura

```
frontend/     Angular (login Google + grade DRE)
backend/      Spring Boot
  domain/     regras (PG/A, DIF, totais)
  application/casos de uso e ports
  adapter/    REST, JWT, JPA
supabase/     SQL igual ao Flyway, para aplicar no dashboard
```

O Angular **não** acessa o Postgres. Só o Java fala com o banco via JDBC. O frontend usa o Supabase somente para autenticar no Google e envia o JWT nas chamadas `/api/**`.

## 1. Criar o projeto no Supabase

1. Acesse https://supabase.com/dashboard e crie um projeto (região `sa-east-1`).
2. Em **Project Settings → API** copie a URL e a chave **anon/public** (nunca a `service_role`).
3. Em **Authentication → Sign In / Providers → Google**:
   - Crie credenciais OAuth **Web** no [Google Cloud Console](https://console.cloud.google.com/apis/credentials).
   - Authorized redirect URI: `https://<PROJECT_REF>.supabase.co/auth/v1/callback`
   - Cole Client ID e Secret no provider Google do Supabase.
4. Em **Authentication → URL Configuration**, adicione:
   - `http://localhost:4200/**`
   - a URL de produção, se houver
5. Ative **JWT signing keys assimétricas** (JWKS). O Spring valida em  
   `https://<PROJECT_REF>.supabase.co/auth/v1/.well-known/jwks.json`
6. Em **Project Settings → Database** copie a connection string **direta** (porta `5432`), não o pooler de transação. O Flyway precisa dela.

Pode aplicar o SQL em [`supabase/migrations/20260816120000_init.sql`](supabase/migrations/20260816120000_init.sql) no SQL Editor **ou** deixar o Flyway criar as tabelas no primeiro boot do backend — não rode os dois.

Em **Settings → API → Data API**, restrinja o schema público se quiser: o app Java já isola por `user_id`. RLS está ligado nas tabelas.

## 2. Configurar o backend

Requisitos: **JDK 21**. Maven Wrapper está em `backend/`.

```powershell
cd backend
copy .env.example .env
```

Preencha `.env` e exporte as variáveis (PowerShell):

```powershell
Get-Content .env | ForEach-Object {
  if ($_ -match '^\s*#' -or $_ -notmatch '=') { return }
  $k,$v = $_.Split('=',2)
  Set-Item -Path "Env:$k" -Value $v
}
.\mvnw.cmd spring-boot:run
```

Health check sem login: `GET http://localhost:8080/api/public/health`

## 3. Configurar o Angular

```powershell
cd frontend
npm install
```

Edite [`frontend/src/environments/environment.development.ts`](frontend/src/environments/environment.development.ts):

```ts
supabaseUrl: 'https://xxxxx.supabase.co',
supabaseAnonKey: 'eyJ...'
```

```powershell
npm start
```

O proxy em [`frontend/proxy.conf.json`](frontend/proxy.conf.json) encaminha `/api` para o Spring.

## 4. Monólito (API + UI no mesmo jar)

```powershell
cd frontend
npm run build
cd ..\backend
.\mvnw.cmd -Pwith-frontend package
java -jar target\lpf-backend-0.1.0.jar
```

Acesse `http://localhost:8080`. O perfil Maven `with-frontend` entra automaticamente se `frontend/dist/frontend/browser/index.html` existir.

## Regras iguais à planilha

- **Situação** = `PG` se `V. pago` está preenchido, senão `A`
- **Diferença** = `V. pago − Previsão` só quando `PG`
- **TOTAL A+B** = despesa fixa paga + despesa variável paga
- **Qnd deveria sobrar** = receita paga − TOTAL A+B
- **O que sobrou** = valor manual do mês
- **Diferença do fechamento** = o que sobrou − o que deveria sobrar

No primeiro login o sistema copia o catálogo da planilha e as previsões de **agosto/2026**.

## Testes de domínio

```powershell
cd backend
.\mvnw.cmd -Dtest=DreCalculatorTest test
```
