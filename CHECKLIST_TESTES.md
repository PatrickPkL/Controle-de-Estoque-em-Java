# Checklist de Testes - Controle de Estoque

## CORRIGIDO no commit 0eca66c

| # | Problema | O que foi feito | Arquivo |
|---|----------|----------------|---------|
| 1 | FK constraint ao excluir produto com movimentacoes | Adicionado ON DELETE CASCADE + delecao em transacao | ConnectionUtil.java:69, ProdutoDAO.java:58-80 |
| 2 | Estoque negativo em SAIDA | Valida se qtd <= estoqueAtual antes de registrar | ProdutoController.java:233-238 |
| 3 | INNER JOIN excluia produtos sem categoria | Mudado para LEFT JOIN em listar() e buscarPorNome() | ProdutoDAO.java:58-62,78-82 |
| 4 | jLabel6 com texto "jLabel6" | Removido o texto (setText vazio) | TelaPrincipalView.java:88 |
| 5 | Aviso -source 11 -target 11 | Substituido por --release 11 no pom.xml | pom.xml:11 |
| 6 | init.sql sem ON DELETE CASCADE | Adicionado CASCADE na FK de movimentacoes | init.sql:34 |

## Compilacao estatica

| # | Item | Status |
|---|------|--------|
| 1 | Compilacao 18 arquivos Java | OK |
| 2 | Dependencia MySQL Connector resolvida | OK |
| 3 | MainView para LoginController wired corretamente | OK |
| 4 | ActionListeners vazios (designer) ignorados | OK |
| 5 | jLabel6 orfao nao aparece na UI | CORRIGIDO |
| 6 | Build sem warnings (--release 11) | CORRIGIDO |

## Falta testar (runtime com MySQL rodando)

| # | Item | Como testar | Resultado Esperado |
|---|------|-------------|--------------------|
| 7 | btnCadastrarse abre CadastroUsuarioView | Clicar "Cadastrar-se" no login | Abre tela de cadastro |
| 8 | Cadastro usuario login duplicado | Cadastrar com login existente | "Este login ja esta em uso!" |
| 9 | Login admin:admin | Digitar admin + admin | Loga e abre dashboard |
| 10 | Login com senha errada | Digitar admin + senha_errada | "Usuario ou senha invalidos!" |
| 11 | CRUD Produto cadastrar | Preencher + Cadastrar | Insere no banco e recarrega |
| 12 | CRUD Produto campos obrigatorios | Clicar "Cadastrar" sem preencher | "Preencha todos os campos!" |
| 13 | CRUD Produto preco com virgula | Digitar "10,50" no preco | Converte e salva |
| 14 | CRUD Produto atualizar | Selecionar + alterar + Atualizar | Atualiza no banco |
| 15 | CRUD Produto excluir com movimentacoes | Produto com historico, Excluir | Remove produto + movs (CASCADE) |
| 16 | CRUD Produto excluir sem movimentacoes | Produto sem historico, Excluir | Remove apenas o produto |
| 17 | Produto sem categoria | Inserir manualmente id_categoria=NULL | Aparece na listagem (LEFT JOIN) |
| 18 | Movimentacao ENTRADA | Selecionar + qtd + "Registrar Entrada" | Soma ao estoque |
| 19 | Movimentacao SAIDA estoque suficiente | Produto c/ 10 unids, saida de 3 | Subtrai e permite |
| 20 | SAIDA com estoque insuficiente | Produto c/ 5 unids, saida de 10 | Bloqueia com aviso |
| 21 | Transacao com rollback | Forcar erro durante movimentacao | Reverte ambas operacoes |
| 22 | Historico de movimentacoes | Registrar entrada/saida + ver aba | Lista em ordem decrescente |
| 23 | Fechar aplicacao | Fechar janela | Encerra conexao |

## Riscos

| Risco | Status |
|-------|--------|
| Estoque pode ficar negativo | CORRIGIDO - validado no controller |
| Produto sem categoria some da listagem | CORRIGIDO - LEFT JOIN |
| FK constraint ao excluir | CORRIGIDO - CASCADE + transacao |
| Aviso de compilacao --release | CORRIGIDO |
| jLabel6 lixo visual | CORRIGIDO |
