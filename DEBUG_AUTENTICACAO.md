# 🔍 Debug de Autenticação - Erro 401

## Problema
Ao tentar criar uma reserva pelo frontend (`http://localhost:3000`), está retornando **401 Unauthorized**.

## Causa Provável
O Next.js está fazendo um **rewrite** de `/api/*` para `http://localhost:8080/api/*`, mas os **cookies de sessão HTTP não estão sendo passados** através do rewrite.

## Soluções Implementadas

### 1. ✅ CORS Configurado no Backend
- Criado `CorsConfig.java` para aceitar requisições de `http://localhost:3000`
- `allowCredentials(true)` habilitado para permitir cookies

### 2. ✅ Interceptor de Autenticação
- Verifica sessão HTTP em rotas protegidas
- Retorna 401 se não houver sessão válida

## Como Testar

### Via Browser (Frontend)
1. Acesse: `http://localhost:3000/alugar/login`
2. Faça login com: `joao.silva` / `senha123`
3. Navegue para `/alugar` e faça uma busca
4. Clique em "Reservar"
5. Preencha as datas e confirme

### Via Terminal (Backend direto)
```bash
# 1. Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"joao.silva","senha":"senha123"}' \
  -c cookies.txt

# 2. Criar reserva (usando cookies)
curl -X POST http://localhost:8080/api/v1/reservas \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "categoriaCodigo":"SUV",
    "cidadeRetirada":"Rio de Janeiro",
    "periodo":{
      "dataRetirada":"2025-12-20T10:00:00",
      "dataDevolucao":"2025-12-25T10:00:00"
    }
  }'
```

## Possíveis Problemas

### 1. Cookies não estão sendo enviados
**Sintoma**: 401 mesmo após login
**Solução**: Verificar se `withCredentials: true` está configurado no Axios

### 2. Sessão expirou
**Sintoma**: 401 após algum tempo
**Solução**: Fazer login novamente

### 3. Next.js rewrite não passa cookies
**Sintoma**: 401 mesmo com login válido
**Solução**: Verificar se o Next.js está passando cookies através do rewrite

## Verificar no Browser

1. Abra DevTools (F12)
2. Vá em **Application** > **Cookies** > `http://localhost:3000`
3. Verifique se há um cookie `JSESSIONID`
4. Se não houver, o problema é que os cookies não estão sendo salvos

## Verificar Requisições

1. Abra DevTools (F12)
2. Vá em **Network**
3. Tente criar uma reserva
4. Veja a requisição `POST /api/v1/reservas`
5. Verifique se há um header `Cookie: JSESSIONID=...`
6. Se não houver, os cookies não estão sendo enviados

