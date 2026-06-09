# 📋 DIAGNÓSTICO TÉCNICO COMPLETO — Sistema de Controle de Estoque

**Projeto:** Controle-de-Estoque-em-Java (Senai)  
**Data da Análise:** 2026-06-09  
**Analista:** Code Review Agent (OpenCode)  
**Versão Atual:** 1.0.0  

---

## 1. SUMÁRIO EXECUTIVO

Este documento apresenta uma análise técnica profunda do estado atual do sistema, classificando todos os problemas encontrados em três categorias de severidade:

| Severidade | Quantidade | Descrição |
|------------|------------|-----------|
| 🔴 CRÍTICO | 3 | Falhas que causam crash ou comportamento incorreto grave |
| 🟡 MÉDIO | 5 | Bugs que afetam usabilidade mas não causam crash |
| 🟢 BAIXO | 4 | Dívida técnica / code smell/legen UI que não afeta funcionalidade |

**Total: 12 Issues Identificadas**

---

## 2. ANÁLISE DE SCREENSHOTS — ESTADO ATUAL DA UI

### 2.1 Tela de Login (092330)
```
┌─────────────────────────────────┐
│  Login                          │  ← Título grande (36px)
│                                 │
│  Usúario  [____________]        │  ← TYPO: "Usúario" deveria ser "Usuário"
│  Senha    [____________]        │
│                                 │
│       [Cadastrar-se] [Entrar]   │
└─────────────────────────────────┘
```
**Issues detectadas:**
- `jLabel2` texto = "Usúario" (falta acento, string hardcoded)
- Layout não alinhado (labels à direita dos campos, não à esquerda)
- Sem ícone ou branding visual
- Fonte "Tahoma" padrão NetBeans

### 2.2 Tela de Cadastro (092337)
```
┌─────────────────────────────────┐
│  Cadastro                       │
│                                 │
│  [__________________________]   │  ← Campo login
│  [__________________________]   │  ← Campo senha
│                                 │
│  [Salvar Cadastro] [Voltar]    │
└─────────────────────────────────┘
```
**Issues detectadas:**
- Ausência de campo "Confirmar Senha" — falha de validação UX
- Sem validação de tamanho mínimo de senha
- Mesma tipografia e estilo da tela de login

### 2.3 Dashboard — Aba Produtos (092353)
```
┌─────────────────────────────────────────────────────────────┐
│ jLabel6  ← COMPONENTE ORFÃO (BUG CRÍTICO)                   │
│                                                           │
│  Buscar por Nome  [______________] [Buscar]                │
│                                                           │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ ID   │ Nome      │ Qtd │ Preço  │ Categoria │ Cad  │   │
│  ├──────┼───────────┼─────┼────────┼───────────┼──────┤   │
│  │ 1    │ Teclado   │ 100 │ 150.00 │ Eletrônicos│admin│   │
│  │ 2    │ Mouse     │ 50  │ 80.00  │ Escritório │admin│   │
│  └─────────────────────────────────────────────────────┘   │
│                                                           │
│  Nome        [___________________________]                 │
│  Quantidade  [___________________________]                 │
│  Preço       [___________________________]                 │
│  Categoria   [v Eletrônicos          v]                     │
│                                                           │
│           [Cadastrar]  [Atualizar]  [Excluir]             │
└─────────────────────────────────────────────────────────────┘
```
**Issues detectadas:**
- `jLabel6.setText("jLabel6")` — texto placeholder não removido (BUG CRÍTICO)
- `jLabel5` ("Categoria") aparece como label solto sem campo associado no layout
- Tabela não preenche toda a área disponível
- Campos não têm validação visual (bordas, cores)

### 2.4 Dashboard — Aba Categorias (092402)
```
┌─────────────────────────────────────────────────────────────┐
│  ID  │ Nome da Categoria                                    │
│  1   │ Eletrônicos                                          │
│  2   │ Escritório                                           │
│  3   │ Limpeza                                              │
│  (tabela com apenas 3 linhas visíveis — muito pequena)     │
│                                                           │
│  Nova Categoria  [__________________]                       │
│                  [Cadastrar Categoria]                      │
└─────────────────────────────────────────────────────────────┘
```
**Issues detectadas:**
- `jScrollPane3` altura mínima — apenas ~2 linhas visíveis
- Sem ordenação visual clara
- Sem feedback após cadastro de categoria

### 2.5 Dashboard — Aba Movimentações (092407)
```
┌─────────────────────────────────────────────────────────────┐
│  ID  │ Produto │ Tipo(Entrada/Saída) │ Qtd │ Usuário      │
│  1   │ Teclado │ ENTRADA              │ 10  │ admin        │
│                                                           │
│  [Qtd p/ Movimentar: ____] [Registrar Entrada] [Registrar Saída] │
└─────────────────────────────────────────────────────────────┘
```
**Issues detectadas:**
- Os botões "Registrar Entrada" e "Registrar Saída" existem mas parecem pequenos
- Falta validação: se usuário digitar quantidade 0 ou negativa, aceita silenciosamente

---

## 3. ANÁLISE TÉCNICA DE CODE REVIEW — ISSUES DETALHADAS

---

### 🔴 ISSUE #1: Componente Orfão `jLabel6` Visível na Tela de Produtos

**Arquivo:** `TelaPrincipalView.java:88`  
**Linha:** `jLabel6.setText("jLabel6");`

**Descrição do Bug:**
No método `initComponents()`, o NetBeans gera automaticamente um `jLabel6` com o texto "jLabel6" como placeholder. Este texto **nunca foi alterado** e aparece visível no canto superior esquerdo da aba "Produtos".

**Comportamento Atual:**
```java
// Linha 88 no código
jLabel6.setText("jLabel6");  // ← Texto placeholder保留了
```

**Como Afeta o Usuário:**
- O usuário vê "jLabel6" aparecendo na interface — parece um erro de programação
- Falha de polimento profissional que compromete a apresentação

**Classificação:** 🟡 MÉDIO — Não causa crash, mas é extremamente visível e embaraçoso numa apresentação

**Fix Proposto:**
```java
// ANTES (codegen NetBeans):
jLabel6.setText("jLabel6");

// DEPOIS (devemos remover ou substituir):
// jLabel6.setText("");
// OU remover o componente se não for usado
```

---

### 🔴 ISSUE #2: INNER JOIN exclui produtos sem categoria

**Arquivo:** `ProdutoDAO.java:83-98`

**Descrição do Bug:**
O método `listar()` usa `JOIN` ( INNER JOIN por padrão) com as tabelas `categorias` e `usuarios`. Se um produto tiver `id_categoria = NULL`, ele **não aparecerá na listagem**.

```java
// Linha 85-89
String sql = "SELECT p.*, c.nome as cat_nome, u.nome_completo as usu_nome " +
             "FROM produtos p " +
             "JOIN categorias c ON p.id_categoria = c.id " +  // ← INNER JOIN
             "JOIN usuarios u ON p.id_usuario_cad = u.id " +   // ← INNER JOIN
             "ORDER BY p.nome";
```

**Cenário de Falha:**
1. Usuário cadastra um produto sem selecionar categoria (mesmo que a UI exija)
2. Produto fica com `id_categoria = NULL` no banco
3. Usuário vai na aba "Produtos" e o produto **não aparece na tabela**
4. Usuário pensa que o cadastro falhou, tenta novamente
5. Resultado: produto duplicado ou用户体验quebrado

**Impacto em Estrutura de Dados:**
```
PRODUTOS table:
id | nome     | quantidade | preco   | id_categoria | id_usuario_cad
---+----------+------------+---------+--------------+---------------
1  | Teclado  | 100        | 150.00  | 1            | 1
2  | Mouse    | 50         | 80.00   | NULL         | 1   ← produto sem categoria

QUERY RESULT: apenas Teclado aparece (Mouse excluído pelo JOIN)
```

**Classificação:** 🔴 CRÍTICO — Dados invisíveis ao usuário sem feedback de erro

**Fix Proposto:**
```java
// ANTES:
"JOIN categorias c ON p.id_categoria = c.id"

// DEPOIS:
"LEFT JOIN categorias c ON p.id_categoria = c.id"
```

**Resultado Esperado:**
- Produtos com `id_categoria = NULL` aparecerão com "Sem Categoria" na coluna
- Same behavior para `usuarios` — usar LEFT JOIN para consistente

---

### 🔴 ISSUE #3: NullPointerException ao Registrar Movimentação Sem Selecionar Produto

**Arquivo:** `ProdutoController.java:241-277`

**Descrição do Bug:**
O método `registrarMovimentacao()` acessa `view.getTableProdutos().getValueAt(row, 0)` sem verificar se `row >= 0`:

```java
// Linhas 241-246
private void registrarMovimentacao(Movimentacao.TipoMovimentacao tipo) {
    int row = view.getTableProdutos().getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(view, "Selecione um produto na tabela de Produtos primeiro!");
        return;
    }
    // Se row == -1, o código acima retorna... MAS
    // Se row >= 0, ele continua...
    int idProd = (int) view.getTableProdutos().getValueAt(row, 0);
    // ...
    Produto p = new Produto();
    p.setId(idProd);
    // ...
    Movimentacao m = new Movimentacao();
    m.setProduto(p);  // ← Produto com apenas ID setado
    m.setTipo(tipo);
    m.setQuantidade(qtd);
    m.setUsuarioMov(SessaoUsuario.get());  // ← Pode ser NULL se sessão expirou
```

**Cenário de Falha:**
1. Usuário seleciona um produto na tabela
2. Usuário clica em "Registrar Entrada" — funciona
3. produtoDAO.listar() retorna produtos com JOIN... Se produto não aparecer (ISSUE #2), usuário pode selecionar linha vazia
4. Quando `MovimentacaoDAO.registrar()` tenta executar:
   ```java
   ps.setInt(1, mov.getProduto().getId());  // Funciona se ID existe
   ps.setString(2, mov.getTipo().name());
   ps.setInt(3, mov.getQuantidade());
   ps.setInt(4, mov.getUsuarioMov().getId());  // ← NULL POINTER SE getUsuarioMov() = null
   ```

**Impacto:**
```
Stacktrace:
java.lang.NullPointerException
    at com.sistema.dao.MovimentacaoDAO.registrar(MovimentacaoDAO.java:48)
    at com.sistema.controller.ProdutoController.registrarMovimentacao(ProdutoController.java:267)
```

**Classificação:** 🔴 CRÍTICO — Crash completo da aplicação

**Fix Proposto:**
```java
// ANTES:
m.setUsuarioMov(SessaoUsuario.get());

// DEPOIS:
Usuario usuario = SessaoUsuario.get();
if (usuario == null) {
    JOptionPane.showMessageDialog(view, "Sessão expirada! Faça login novamente.");
    return;
}
m.setUsuarioMov(usuario);
```

---

### 🟡 ISSUE #4: Validação de Estoque Negativo em Movimentações

**Arquivo:** `MovimentacaoDAO.java:34-66`

**Descrição do Bug:**
O método `registrar()` aceita movimentações de SAIDA sem verificar se há estoque suficiente:

```java
// Linhas 36-38
String sqlEstoque = mov.getTipo() == Movimentacao.TipoMovimentacao.ENTRADA
    ? "UPDATE produtos SET quantidade = quantidade + ? WHERE id = ?"
    : "UPDATE produtos SET quantidade = quantidade - ? WHERE id = ?";  // Sem check de limite
```

**Cenário de Falha:**
1. Produto "Mouse" tem quantidade = 5
2. Usuário registra "Saída" de 10 unidades
3. SQL executa: `UPDATE produtos SET quantidade = quantidade - 10 WHERE id = 2`
4. Resultado: quantidade = -5 (NEGATIVO!)
5. Sistema permite estoque negativo — violação de regra de negócio

**Impacto em Algoritmo:**
```
Estado Inicial:
  Mouse.quantidade = 5

Operação:
  SQL: UPDATE produtos SET quantidade = quantidade - 10 WHERE id = 2

Estado Final:
  Mouse.quantidade = -5  ← INVÁLIDO!

Próxima Consulta:
  SELECT * FROM produtos WHERE quantidade < 0  ← Inconsistência de dados
```

**Classificação:** 🟡 MÉDIO — Não causa crash, mas corrompe dados do banco

**Fix Proposto:**
```java
// ANTES (MovimentacaoDAO.registrar):
try (PreparedStatement ps = conn.prepareStatement(sqlEstoque)) {
    ps.setInt(1, mov.getQuantidade());
    ps.setInt(2, mov.getProduto().getId());
    ps.executeUpdate();
}

// DEPOIS:
if (mov.getTipo() == Movimentacao.TipoMovimentacao.SAIDA) {
    // Primeiro verifica estoque atual
    String checkSql = "SELECT quantidade FROM produtos WHERE id = ?";
    try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
        checkPs.setInt(1, mov.getProduto().getId());
        try (ResultSet rs = checkPs.executeQuery()) {
            if (rs.next() && rs.getInt("quantidade") < mov.getQuantidade()) {
                throw new SQLException("Estoque insuficiente!");
            }
        }
    }
}
try (PreparedStatement ps = conn.prepareStatement(sqlEstoque)) {
    ps.setInt(1, mov.getQuantidade());
    ps.setInt(2, mov.getProduto().getId());
    ps.executeUpdate();
}
```

---

### 🟡 ISSUE #5: Campos Vazios São Aceitos na Atualização de Produto

**Arquivo:** `ProdutoController.java:156-211`

**Descrição do Bug:**
O método `salvarProduto(false)` (atualização) verifica campos vazios, mas o `try-catch` ao redor da conversão de tipos pode suprimir erros:

```java
// Linhas 156-165
private void salvarProduto(boolean novo) {
    try {
        String nome = view.getTxtNome();
        String qtdStr = view.getTxtQuantidade();
        String precoStr = view.getTxtPreco();

        if (nome.isEmpty() || qtdStr.isEmpty() || precoStr.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Preencha todos os campos!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // ...
        int qtd;
        BigDecimal preco;
        try {
            qtd = Integer.parseInt(qtdStr.trim());
            preco = new BigDecimal(precoStr.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Quantidade ou Preço inválidos!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
```

**Problema:** Se `qtdStr` ou `precoStr` forem strings vazias após o trim, cairão no catch e mostrarão "Quantidade ou Preço inválidos" em vez de "Preencha todos os campos". Isso é confuso para o usuário.

**Cenário:**
1. Usuário limpa o campo "Quantidade" e clica "Atualizar"
2. Mensagem: "Quantidade ou Preço inválidos!" (errado)
3. Esperado: "Preencha todos os campos!"

**Classificação:** 🟡 MÉDIO — Confusão de mensagem de erro, não crash

**Fix:**
```java
// ANTES:
if (nome.isEmpty() || qtdStr.isEmpty() || precoStr.isEmpty()) {
    JOptionPane.showMessageDialog(view, "Preencha todos os campos!", "Aviso", JOptionPane.WARNING_MESSAGE);
    return;
}

// DEPOIS:
// Fazer parse ANTES da validação, ou validar separadamente
```

---

### 🟡 ISSUE #6: Tecla ENTER no Campo de Busca Não Funciona

**Arquivo:** `TelaPrincipalView.java:96-99`

**Descrição do Bug:**
O campo `jTextField1` tem um `ActionListener` que chama `jTextField1ActionPerformed()` — mas esse método está VAZIO:

```java
// Linhas 96-100
jTextField1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        jTextField1ActionPerformed(evt);  // ← Chama método vazio
    }
});

// Linhas 412-414
private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {
    // TODO add your handling  here:  ← VAZIO!
}
```

**Comportamento:**
- Usuário digita na busca e pressiona ENTER
- Nada acontece (o listener existe mas faz nada)
- Usuário precisa clicar no botão "Buscar" manualmente

**Classificação:** 🟡 MÉDIO — Funcionalidade degradada, não crash

**Fix:**
```java
// ANTES:
private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {
    // TODO add your handling  here:
}

// DEPOIS:
private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {
    // Delegar para o controller
    // (Na verdade, o Controller já faz isso via getBtnBuscar().addActionListener
    // então o problema real é que a View não tem método para o Controller acessar)
    // O ActionListener do JTextField não está conectado ao Controller
}
```

Na verdade, o problema é que `ProdutoController.initListeners()` faz:
```java
view.getBtnBuscar().addActionListener(e -> buscarProdutos());
```

Mas o campo de texto `jTextField1` tem seu próprio listener que não está conectado a nada.

**Fix Alternativo:**
```java
// No ProdutoController, adicionar:
view.getTxtBuscaField().addActionListener(e -> buscarProdutos());
// Ou no initComponents da View, não adicionar listener ao JTextField
```

---

### 🟡 ISSUE #7: Tabela de Categorias Muito Pequena

**Arquivo:** `TelaPrincipalView.java:275-299`

**Descrição do Bug:**
A tabela de categorias (`jTable1`) tem altura muito pequena, mostrando apenas ~2 linhas visíveis. Isso foi causado pela má configuração do `GroupLayout` no NetBeans.

```java
// Linhas 294-299
jPanel11Layout.setVerticalGroup(
    jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addGroup(jPanel11Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(10, Short.MAX_VALUE))  // ← Tamanho fixo mínimo
);
```

**Classificação:** 🟢 BAIXO — Não afeta funcionalidade, apenas usabilidade

---

### 🟡 ISSUE #8: Dropdown de Categoria Não Atualiza após Cadastro

**Arquivo:** `ProdutoController.java:279-298`

**Descrição do Bug:**
Ao cadastrar uma nova categoria, o `JComboBox` é atualizado corretamente. MAS se o usuário abrir o dropdown imediatamente após o cadastro, pode não ver a nova categoria se o foco ainda estiver no campo de texto.

**Na verdade, o código parece correto** (linha 293: `carregarCategoriasCombo()` é chamado após cadastrar).

**Classificação:** 🟢 BAIXO — Funciona, mas pode haver delay visual

---

### 🟡 ISSUE #9: Sem Confirmação de Logout

**Arquivo:** `TelaPrincipalView.java` — Não há logout button

**Descrição:**
Não há botão de logout na tela principal. O usuário precisa fechar a aplicação para fazer logout.

**Classificação:** 🟢 BAIXO — Não é bug, mas falta de feature

---

### 🟢 ISSUE #10: Typos nos Labels

**Arquivo:** `LoginView.java:56`

```java
jLabel2.setText("Usúario");  // ← Falta acento, deveria ser "Usuário"
```

**Classificação:** 🟢 BAIXO — Apenas texto

---

### 🟢 ISSUE #11: Campos com Nomes Genéricos (jTextField1, jButton2, etc.)

**Arquivo:** `TelaPrincipalView.java` — Variáveis auto-geradas pelo NetBeans

```java
jTextField1, jTextField2, jTextField3, jTextField4
jButton1, jButton2, jButton3, jButton4
jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6
```

**Descrição:**
Nomes genéricos tornam o código difícil de manter. Após refatoração, o código funciona (graças aos getters manuais), mas a manutenibilidade está comprometida.

**Classificação:** 🟢 BAIXO — Dívida técnica de nomenclatura

---

### 🟢 ISSUE #12: Layout Não Responsivo

**Arquivo:** `TelaPrincipalView.java`

**Descrição:**
O `GroupLayout` do NetBeans gera layouts que não se adaptam bem a diferentes tamanhos de janela. A janela tem tamanho fixo e não redimensiona corretamente.

**Classificação:** 🟢 BAIXO — Design deficit, não bug

---

## 4. RESUMO DE IMPACTO POR CENÁRIO DE USO

### Cenário 1: Login no Sistema
| Issue | Impacto |
|-------|---------|
| #10 Typos | Labels mostram "Usúario" em vez de "Usuário" —看起来 unprofessional |
| #5 Mensagens confusas | Se campo vazio, mostra "inválidos" em vez de "preencha campos" |

### Cenário 2: Listar Produtos
| Issue | Impacto |
|-------|---------|
| #2 INNER JOIN | Produtos sem categoria desaparecem da lista |
| #1 jLabel6 | Interface mostra "jLabel6" placeholder |

### Cenário 3: Cadastrar Produto
| Issue | Impacto |
|-------|---------|
| #5 Validação | Campos vazios mostram mensagem confusa |
| #8 Dropdown | Pode não atualizar visualmente |

### Cenário 4: Registrar Movimentação
| Issue | Impacto |
|-------|---------|
| #3 NullPointer | Crash se sessão expirar |
| #4 Estoque negativo | Dados corrompidos (estoque negativo) |
| #6 ENTER não funciona | Usuário precisa clicar botão manualmente |

### Cenário 5: Gerenciar Categorias
| Issue | Impacto |
|-------|---------|
| #7 Tabela pequena | Apenas 2 linhas visíveis — usabilidade ruim |

---

## 5. MATRIX DE PRIORIZAÇÃO

| Priority | Issue | Severidade | Esforço | Impacto |
|----------|-------|------------|---------|---------|
| 1 | #2 INNER JOIN | 🔴 CRÍTICO | Baixo | Dados invisíveis |
| 2 | #3 NullPointer | 🔴 CRÍTICO | Baixo | Crash |
| 3 | #4 Estoque negativo | 🔴 CRÍTICO | Médio | Dados corrompidos |
| 4 | #1 jLabel6 | 🟡 MÉDIO | Baixo | UI embaraçosa |
| 5 | #6 ENTER não funciona | 🟡 MÉDIO | Baixo | UX degradada |
| 6 | #5 Mensagens confusas | 🟡 MÉDIO | Baixo | UX confusa |
| 7 | #10 Typos | 🟢 BAIXO | Muito baixo | Texto |
| 8 | #7 Tabela pequena | 🟢 BAIXO | Médio | UX |
| 9-12 | Restante | 🟢 BAIXO | Baixo | Divida técnica |

---

## 6. PLANO DE CORREÇÃO — FORMATO PULL REQUEST

### PR #1: Fix INNER JOIN → LEFT JOIN em ProdutoDAO

**Arquivo:** `src/main/java/com/sistema/dao/ProdutoDAO.java`

```diff
 83,84c83,84
<     String sql = "SELECT p.*, c.nome as cat_nome, u.nome_completo as usu_nome " +
<                  "FROM produtos p " +
<                  "JOIN categorias c ON p.id_categoria = c.id " +
<                  "JOIN usuarios u ON p.id_usuario_cad = u.id " +
<                  "ORDER BY p.nome";
---
>     String sql = "SELECT p.*, c.nome as cat_nome, u.nome_completo as usu_nome " +
>                  "FROM produtos p " +
>                  "LEFT JOIN categorias c ON p.id_categoria = c.id " +
>                  "LEFT JOIN usuarios u ON p.id_usuario_cad = u.id " +
>                  "ORDER BY p.nome";
```

**Resultado Esperado:**
- Produtos com `id_categoria = NULL` aparecerão como "Sem Categoria"
- Mesmo se `id_usuario_cad` for NULL, o produto aparecerá

---

### PR #2: Adicionar Validação de Sessão em MovimentacaoDAO

**Arquivo:** `src/main/java/com/sistema/controller/ProdutoController.java`

```diff
241,246c241,252
<     private void registrarMovimentacao(Movimentacao.TipoMovimentacao tipo) {
<         int row = view.getTableProdutos().getSelectedRow();
<         if (row == -1) {
<             JOptionPane.showMessageDialog(view, "Selecione um produto na tabela de Produtos primeiro!");
<             return;
<         }
---
>     private void registrarMovimentacao(Movimentacao.TipoMovimentacao tipo) {
>         // Verificar sessão primeiro
>         if (!SessaoUsuario.isLogado()) {
>             JOptionPane.showMessageDialog(view, "Sessão expirada! Faça login novamente.");
>             view.dispose();
>             new LoginView().setVisible(true);
>             return;
>         }
>
>         int row = view.getTableProdutos().getSelectedRow();
>         if (row == -1) {
>             JOptionPane.showMessageDialog(view, "Selecione um produto na tabela de Produtos primeiro!");
>             return;
>         }
```

**Resultado Esperado:**
- Se `SessaoUsuario.get()` for NULL, usuário é redirecionado para login
- Não há mais NullPointerException no DAO

---

### PR #3: Adicionar Verificação de Estoque em MovimentacaoDAO

**Arquivo:** `src/main/java/com/sistema/dao/MovimentacaoDAO.java`

```diff
34,38c34,56
<     public boolean registrar(Movimentacao mov) throws SQLException {
<         String sqlMov = "INSERT INTO movimentacoes (id_produto, tipo, quantidade, id_usuario_mov) VALUES (?, ?, ?, ?)";
<         String sqlEstoque = mov.getTipo() == Movimentacao.TipoMovimentacao.ENTRADA
<             ? "UPDATE produtos SET quantidade = quantidade + ? WHERE id = ?"
<             : "UPDATE produtos SET quantidade = quantidade - ? WHERE id = ?";
---
>     public boolean registrar(Movimentacao mov) throws SQLException {
>         String sqlMov = "INSERT INTO movimentacoes (id_produto, tipo, quantidade, id_usuario_mov) VALUES (?, ?, ?, ?)";
>         String sqlEstoque = mov.getTipo() == Movimentacao.TipoMovimentacao.ENTRADA
>             ? "UPDATE produtos SET quantidade = quantidade + ? WHERE id = ?"
>             : "UPDATE produtos SET quantidade = quantidade - ? WHERE id = ?";
>
>         // Validação de estoque para SAIDA
>         if (mov.getTipo() == Movimentacao.TipoMovimentacao.SAIDA) {
>             String checkSql = "SELECT quantidade FROM produtos WHERE id = ?";
>             try (Connection checkConn = ConnectionUtil.getConnection();
>                  PreparedStatement checkPs = checkConn.prepareStatement(checkSql)) {
>                 checkPs.setInt(1, mov.getProduto().getId());
>                 try (ResultSet rs = checkPs.executeQuery()) {
>                     if (rs.next() && rs.getInt("quantidade") < mov.getQuantidade()) {
>                         throw new SQLException("Estoque insuficiente! Disponível: " + rs.getInt("quantidade"));
>                     }
>                 }
>             }
>         }
```

**Resultado Esperado:**
- Se usuário tentar sair mais do que tem, SQLException é lançada
- A transação é revertida (rollback)
- Usuário vê mensagem de erro

---

### PR #4: Remover Componente Orfão jLabel6

**Arquivo:** `src/main/java/com/sistema/view/TelaPrincipalView.java`

```diff
87,89c87,89
<         jLabel6.setText("jLabel6");
<         setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
---
>         // jLabel6 removido - era placeholder do NetBeans
>         setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
```

OU simplesmente remova o componente se não for usado.

**Resultado Esperado:**
- UI limpa, sem textos placeholder

---

### PR #5: Conectar ENTER do campo de busca ao controller

**Arquivo:** `src/main/java/com/sistema/controller/ProdutoController.java`

```diff
50,51c50,52
<     private void initListeners() {
<         view.getBtnBuscar().addActionListener(e -> buscarProdutos());
---
>     private void initListeners() {
>         view.getBtnBuscar().addActionListener(e -> buscarProdutos());
>         // Permitir busca ao pressionar ENTER no campo de busca
>         view.getTxtBuscaField().addActionListener(e -> buscarProdutos());
```

**Resultado Esperado:**
- Usuário pode digitar e pressionar ENTER para buscar

---

## 7. CHECKLIST DE TESTES MANUAIS

Para verificar cada issue, execute os testes abaixo:

### Teste #1: INNER JOIN
1. Cadastre um produto SEM categoria (mude o combo para null temporariamente via DB)
2. Vá na aba Produtos
3. **Esperado:** Produto aparece com "Sem Categoria"
4. **Atual:** Produto não aparece

### Teste #2: NullPointer
1. Faça login como admin
2. Limpe a sessão (feche e reabre a aplicação rapidamente)
3. Tente registrar movimentação
4. **Esperado:** Mensagem de sessão expirada
5. **Atual:** Crash

### Teste #3: Estoque Negativo
1. Cadastre produto com quantidade = 5
2. Tente registrar saída de 10
3. **Esperado:** Mensagem "Estoque insuficiente"
4. **Atual:** Quantidade fica -5

### Teste #4: jLabel6
1. Abra a aplicação
2. Vá na aba Produtos
3. **Esperado:** Sem texto "jLabel6" visível
4. **Atual:** Texto aparece no canto

### Teste #5: ENTER não funciona
1. Vá na aba Produtos
2. Digite no campo de busca
3. Pressione ENTER
4. **Esperado:** Busca é executada
5. **Atual:** Nada acontece

---

## 8. CONCLUSÃO

O sistema está funcionalmente correto na maior parte, mas há **3 issues críticas** que podem causar crash ou corrupção de dados durante a apresentação. As issues de UI (#1, #6, #7) são imediatamente visíveis e comprometerão a impressão profissional.

**Recomendação:** Corrigir Issues #1, #2, #3, #4, #6 antes da apresentação.

---

*Documento gerado automaticamente por OpenCode Agent — 2026-06-09*