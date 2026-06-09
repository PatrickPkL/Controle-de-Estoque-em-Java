# Checklist de Testes — Controle de Estoque

## ✅ Já testado (compilação estática)

| # | Item | Status |
|---|------|--------|
| 1 | Compilação 18 arquivos Java | ✅ OK |
| 2 | Dependência MySQL Connector resolvida | ✅ OK |
| 3 | MainView → LoginController wired corretamente | ✅ OK |
| 4 | ActionListeners vazios (designer) — ignorados | ✅ OK |
| 5 | jLabel6 órfão — não aparece na UI | ✅ OK |

## ❌ Falta testar (runtime com MySQL rodando)

| # | Item | Como testar | Resultado Esperado |
|---|------|-------------|--------------------|
| 6 | btnCadastrarse abre CadastroUsuarioView | Clicar "Cadastrar-se" no login | Abre tela de cadastro |
| 7 | Cadastro de usuário com login duplicado | Cadastrar com login já existente | "Este login já está em uso!" |
| 8 | Login admin:admin | Digitar admin + admin | Loga e abre dashboard |
| 9 | Login com senha errada | Digitar admin + senha_errada | "Usuário ou senha inválidos!" |
| 10 | CRUD Produto — cadastrar | Preencher nome/qtd/preço/categoria + Cadastrar | Insere no banco e recarrega |
| 11 | CRUD Produto — campos obrigatórios | Clicar "Cadastrar" sem preencher | "Preencha todos os campos!" |
| 12 | CRUD Produto — preço com vírgula | Digitar "10,50" no preço | Converte e salva |
| 13 | CRUD Produto — atualizar | Selecionar + alterar campos + Atualizar | Atualiza no banco |
| 14 | CRUD Produto — excluir | Selecionar + Excluir + confirmar | Remove do banco |
| 15 | INNER JOIN — produto sem categoria | Inserir manualmente produto com id_categoria=NULL | ❌ Some da listagem |
| 16 | INNER JOIN — produto COM categoria | Fluxo normal de cadastro | ✅ Aparece normalmente |
| 17 | Movimentação ENTRADA | Selecionar + qtd + "Registrar Entrada" | Soma ao estoque |
| 18 | Movimentação SAIDA | Selecionar + qtd + "Registrar Saída" | Subtrai do estoque |
| 19 | Estoque negativo — SAÍDA > estoque | Produto c/ 5 unids, saída de 10 | ❌ Não valida — fica negativo |
| 20 | Transação com rollback | Forçar erro durante movimentação | Reverte ambas operações |
| 21 | Histórico de movimentações | Registrar entrada/saída + ver aba | Lista em ordem decrescente |
| 22 | Fechar aplicação | Fechar janela | Encerra conexão |

## Riscos Confirmados

| Risco | Item | Prioridade |
|-------|------|-----------|
| Estoque pode ficar negativo | #19 | 🔴 Alta |
| Produto sem categoria some da lista | #15 | 🟡 Média |
