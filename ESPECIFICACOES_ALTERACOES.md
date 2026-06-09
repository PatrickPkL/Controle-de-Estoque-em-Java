# 📋 ESPECIFICAÇÕES DE ALTERAÇÕES — Sistema de Controle de Estoque

**Projeto:** Controle-de-Estoque-em-Java  
**Data:** 2026-06-09  
**Versão Atual:** 1.0.0 → 1.1.0  

---

## 1. VISÃO GERAL DAS ALTERAÇÕES

Este documento especifica todas as alterações de código a serem realizadas para corrigir os bugs identificados no diagnóstico técnico.

**Total de PRs planejados:** 5

---

## 2. PR #1: Corrigir INNER JOIN → LEFT JOIN

### 2.1 Arquivo Alvo
```
src/main/java/com/sistema/dao/ProdutoDAO.java
```

### 2.2 Problema
O método `listar()` usa `JOIN` que exclui produtos sem categoria. Se `id_categoria = NULL`, o produto não aparece na listagem.

### 2.3 Código Atual (PROBLEMA)
```java
// Linhas 85-89
String sql = "SELECT p.*, c.nome as cat_nome, u.nome_completo as usu_nome " +
             "FROM produtos p " +
             "JOIN categorias c ON p.id_categoria = c.id " +
             "JOIN usuarios u ON p.id_usuario_cad = u.id " +
             "ORDER BY p.nome";
```

### 2.4 Código Corrigido
```java
// Linhas 85-89
String sql = "SELECT p.*, c.nome as cat_nome, u.nome_completo as usu_nome " +
             "FROM produtos p " +
             "LEFT JOIN categorias c ON p.id_categoria = c.id " +
             "LEFT JOIN usuarios u ON p.id_usuario_cad = u.id " +
             "ORDER BY p.nome";
```

### 2.5 Impacto
- Produtos com `id_categoria = NULL` agora aparecem
- Coluna "Categoria" mostrará "Sem Categoria" para produtos sem categorização

### 2.6 Também corrigir em `buscarPorNome()`
```java
// Linha 108-113 — ANTES:
"JOIN categorias c ON p.id_categoria = c.id " +
"JOIN usuarios u ON p.id_usuario_cad = u.id " +

// DEPOIS:
"LEFT JOIN categorias c ON p.id_categoria = c.id " +
"LEFT JOIN usuarios u ON p.id_usuario_cad = u.id " +
```

### 2.7 Atualizar `mapResultSetToProduto()`
```java
// ANTES:
Categoria cat = new Categoria(rs.getInt("id_categoria"), rs.getString("cat_nome"));

// DEPOIS:
// Verificar se cat_nome é NULL
String catNome = rs.getString("cat_nome");
Categoria cat = catNome != null ? new Categoria(rs.getInt("id_categoria"), catNome) : null;
```

---

## 3. PR #2: Adicionar Validação de Sessão

### 3.1 Arquivo Alvo
```
src/main/java/com/sistema/controller/ProdutoController.java
```

### 3.2 Problema
Se `SessaoUsuario.get()` retornar NULL durante uma movimentação, ocorre NullPointerException no DAO.

### 3.3 Código Atual (PROBLEMA)
```java
// Linhas 241-246
private void registrarMovimentacao(Movimentacao.TipoMovimentacao tipo) {
    int row = view.getTableProdutos().getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(view, "Selecione um produto...");
        return;
    }
    // ...continua...
    m.setUsuarioMov(SessaoUsuario.get());  // ← Pode ser NULL
```

### 3.4 Código Corrigido
```java
// Linhas 241-252
private void registrarMovimentacao(Movimentacao.TipoMovimentacao tipo) {
    // Validação de sessão primeiro
    if (!SessaoUsuario.isLogado()) {
        JOptionPane.showMessageDialog(view, "Sessão expirada! Faça login novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
        view.dispose();
        SwingUtilities.invokeLater(() -> {
            LoginView login = new LoginView();
            new LoginController(login);
            login.setVisible(true);
        });
        return;
    }

    int row = view.getTableProdutos().getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(view, "Selecione um produto...", "Aviso", JOptionPane.WARNING_MESSAGE);
        return;
    }
    // ...continua normalmente...
```

### 3.5 Impacto
- Se sessão expirar, usuário é redirecionado para tela de login
- Não ocorre NullPointerException
- Sessão é protegida em todo o controller

### 3.6 Adicionar import se necessário
```java
import javax.swing.SwingUtilities;
```

---

## 4. PR #3: Adicionar Verificação de Estoque

### 4.1 Arquivo Alvo
```
src/main/java/com/sistema/dao/MovimentacaoDAO.java
```

### 4.2 Problema
Movimentações de SAIDA não verificam se há estoque suficiente, permitindo estoque negativo.

### 4.3 Código Atual (PROBLEMA)
```java
// Linhas 34-66
public boolean registrar(Movimentacao mov) throws SQLException {
    String sqlEstoque = mov.getTipo() == Movimentacao.TipoMovimentacao.ENTRADA
        ? "UPDATE produtos SET quantidade = quantidade + ? WHERE id = ?"
        : "UPDATE produtos SET quantidade = quantidade - ? WHERE id = ?";  // ← Sem check
```

### 4.4 Código Corrigido
```java
// Linhas 34-80
public boolean registrar(Movimentacao mov) throws SQLException {
    String sqlMov = "INSERT INTO movimentacoes (id_produto, tipo, quantidade, id_usuario_mov) VALUES (?, ?, ?, ?)";
    String sqlEstoque = mov.getTipo() == Movimentacao.TipoMovimentacao.ENTRADA
        ? "UPDATE produtos SET quantidade = quantidade + ? WHERE id = ?"
        : "UPDATE produtos SET quantidade = quantidade - ? WHERE id = ?";

    // Validação de estoque para SAIDA
    if (mov.getTipo() == Movimentacao.TipoMovimentacao.SAIDA) {
        String checkSql = "SELECT quantidade FROM produtos WHERE id = ?";
        try (Connection checkConn = ConnectionUtil.getConnection();
             PreparedStatement checkPs = checkConn.prepareStatement(checkSql)) {
            checkPs.setInt(1, mov.getProduto().getId());
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    int estoqueAtual = rs.getInt("quantidade");
                    if (estoqueAtual < mov.getQuantidade()) {
                        throw new SQLException("Estoque insuficiente! Disponível: " + estoqueAtual);
                    }
                } else {
                    throw new SQLException("Produto não encontrado.");
                }
            }
        }
    }

    Connection conn = ConnectionUtil.getConnection();
    try {
        conn.setAutoCommit(false);
        // ... resto do código inalterado ...
```

### 4.5 Impacto
- SAIDA com quantidade maior que estoque disponível lança SQLException
- Transação é revertida (rollback)
- Usuário vê mensagem de erro clara

---

## 5. PR #4: Remover jLabel6 Orfão

### 5.1 Arquivo Alvo
```
src/main/java/com/sistema/view/TelaPrincipalView.java
```

### 5.2 Problema
`jLabel6` tem texto placeholder "jLabel6" visível na interface.

### 5.3 Código Atual (PROBLEMA)
```java
// Linha 36 (declaração)
private javax.swing.JLabel jLabel6;

// Linha 88 (inicialização)
jLabel6.setText("jLabel6");
```

### 5.4 Código Corrigido
```java
// OPCÃO 1: Remover completamente o label (se não usado)
// Linha 88 — simplesmente remova a linha:
// jLabel6.setText("jLabel6");

// OPCÃO 2: Substituir por texto útil (se o label for necessário)
// jLabel6.setText("Sistema de Controle de Estoque");

// Recomendamos OPÇÃO 1 — remover completamente
```

### 5.5 Também remover da declaração de variáveis (linha 475)
```java
// ANTES:
private javax.swing.JLabel jLabel6;

// DEPOIS:
// Remover esta linha completamente
```

### 5.6 Impacto
- Interface limpa sem textos placeholder

---

## 6. PR #5: Conectar ENTER do Campo de Busca

### 6.1 Arquivo Alvo
```
src/main/java/com/sistema/view/TelaPrincipalView.java
```

### 6.2 Problema
Pressionar ENTER no campo de busca não executa a busca.

### 6.3 Código Atual (PROBLEMA)
```java
// Linhas 96-100 — ActionListener não faz nada
jTextField1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        jTextField1ActionPerformed(evt);  // ← Método vazio!
    }
});

// Linhas 412-414 — Método vazio
private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {
    // TODO add your handling  here:
}
```

### 6.4 Código Corrigido
```java
// ANTES (linha 96-100):
jTextField1.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        jTextField1ActionPerformed(evt);
    }
});

// DEPOIS:
// Remover este ActionListener (não usar) — controller faz bind via getBtnBuscar()
// O jTextField1ActionPerformed no initComponents deve ficar vazio
// MAS precisamos de um getter para o campo de texto para o controller
```

### 6.5 Solução Alternativa: Adicionar getter para JTextField de busca

Em `TelaPrincipalView.java`, adicionar método:

```java
// Nas variáveis declaradas (procure jTextField1):
private javax.swing.JTextField jTextField1;

// Adicionar getter:
public javax.swing.JTextField getTxtBuscaField() {
    return jTextField1;
}
```

Em `ProdutoController.java`, modificar `initListeners()`:

```java
// ANTES (linha 52):
view.getBtnBuscar().addActionListener(e -> buscarProdutos());

// DEPOIS:
view.getBtnBuscar().addActionListener(e -> buscarProdutos());
view.getTxtBuscaField().addActionListener(e -> buscarProdutos());  // ENTER funciona!
```

### 6.6 Impacto
- Usuário pode digitar e pressionar ENTER para buscar

---

## 7. PR #6: Corrigir Typos e Validações (Menores)

### 7.1 Arquivo: LoginView.java — Typo "Usúario"

```java
// Linha 56 — ANTES:
jLabel2.setText("Usúario");

// DEPOIS:
jLabel2.setText("Usuário");
```

### 7.2 Arquivo: ProdutoController.java — Mensagem de Validação Confusa

```java
// Linhas 156-165 — ANTES:
if (nome.isEmpty() || qtdStr.isEmpty() || precoStr.isEmpty()) {
    JOptionPane.showMessageDialog(view, "Preencha todos os campos!", "Aviso", JOptionPane.WARNING_MESSAGE);
    return;
}
try {
    qtd = Integer.parseInt(qtdStr.trim());
    // ...

// DEPOIS:
// Primeiro converte, depois valida campos específicos
try {
    qtd = Integer.parseInt(qtdStr.trim());
} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(view, "Quantidade inválida! Digite um número.", "Erro", JOptionPane.ERROR_MESSAGE);
    return;
}

// Validação de campos vazios APÓS parse
if (nome.isEmpty()) {
    JOptionPane.showMessageDialog(view, "Nome do produto é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
    return;
}
if (qtdStr.trim().isEmpty()) {
    JOptionPane.showMessageDialog(view, "Quantidade é obrigatória!", "Aviso", JOptionPane.WARNING_MESSAGE);
    return;
}
if (precoStr.trim().isEmpty()) {
    JOptionPane.showMessageDialog(view, "Preço é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
    return;
}
```

---

## 8. RESUMO DE ARQUIVOS A MODIFICAR

| PR | Arquivo | Mudanças |
|----|---------|----------|
| #1 | `ProdutoDAO.java` | INNER JOIN → LEFT JOIN (2 queries) |
| #2 | `ProdutoController.java` | Adicionar validação de sessão |
| #3 | `MovimentacaoDAO.java` | Adicionar check de estoque |
| #4 | `TelaPrincipalView.java` | Remover jLabel6 |
| #5 | `TelaPrincipalView.java` + `ProdutoController.java` | Conectar ENTER |
| #6 | `LoginView.java` + `ProdutoController.java` | Typos + validações |

---

## 9. ORDEM DE IMPLEMENTAÇÃO RECOMENDADA

1. **PR #4** (mais simples — só remover linha)
2. **PR #1** (corrige bug de dados invisíveis)
3. **PR #2** (previne crash)
4. **PR #3** (previne corrupção de dados)
5. **PR #5** (melhora UX)
6. **PR #6** (polimento)

---

## 10. VERIFICAÇÃO PÓS-CORREÇÃO

Após cada PR, executar:

```bash
mvn clean compile
```

Se compilar sem erros, proceed.

Teste funcional:
1. Login com admin/admin
2. Cadastrar produto sem categoria (via MySQL se necessário)
3. Verificar se aparece na lista
4. Tentar saída maior que estoque
5. Verificar mensagem de erro

---

*Especificações criadas — 2026-06-09*