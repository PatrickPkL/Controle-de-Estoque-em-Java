# 📝 PULL REQUESTS — Sistema de Controle de Estoque

**Data:** 2026-06-09  
**Versão:** 1.0.0 → 1.1.0  
**Total de PRs:** 6  

---

## PULL REQUEST #1: Corrigir INNER JOIN → LEFT JOIN em ProdutoDAO

**Arquivo:** `src/main/java/com/sistema/dao/ProdutoDAO.java`

### Alterações:
```diff
- JOIN categorias c ON p.id_categoria = c.id
- JOIN usuarios u ON p.id_usuario_cad = u.id
+ LEFT JOIN categorias c ON p.id_categoria = c.id
+ LEFT JOIN usuarios u ON p.id_usuario_cad = u.id
```

### Impacto:
- Produtos com `id_categoria = NULL` agora aparecem na listagem
- Coluna "Categoria" mostrará o nome corretamente ou "Sem Categoria" no display

---

## PULL REQUEST #2: Adicionar Validação de Sessão em ProdutoController

**Arquivo:** `src/main/java/com/sistema/controller/ProdutoController.java`

### Alterações:
```diff
  private void registrarMovimentacao(Movimentacao.TipoMovimentacao tipo) {
+     if (!SessaoUsuario.isLogado()) {
+         JOptionPane.showMessageDialog(view, "Sessão expirada! Faça login novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
+         view.dispose();
+         javax.swing.SwingUtilities.invokeLater(() -> {
+             LoginView login = new LoginView();
+             new LoginController(login);
+             login.setVisible(true);
+         });
+         return;
+     }
+
      int row = view.getTableProdutos().getSelectedRow();
```

### Impacto:
- Se sessão expirar, usuário é redirecionado para login
- Não ocorre NullPointerException durante movimentações

---

## PULL REQUEST #3: Adicionar Verificação de Estoque em MovimentacaoDAO

**Arquivo:** `src/main/java/com/sistema/dao/MovimentacaoDAO.java`

### Alterações:
```diff
  public boolean registrar(Movimentacao mov) throws SQLException {
      String sqlMov = "INSERT INTO movimentacoes...";
      String sqlEstoque = ...

+     if (mov.getTipo() == Movimentacao.TipoMovimentacao.SAIDA) {
+         String checkSql = "SELECT quantidade FROM produtos WHERE id = ?";
+         try (Connection checkConn = ConnectionUtil.getConnection();
+              PreparedStatement checkPs = checkConn.prepareStatement(checkSql)) {
+             checkPs.setInt(1, mov.getProduto().getId());
+             try (ResultSet rs = checkPs.executeQuery()) {
+                 if (rs.next()) {
+                     int estoqueAtual = rs.getInt("quantidade");
+                     if (estoqueAtual < mov.getQuantidade()) {
+                         throw new SQLException("Estoque insuficiente! Disponível: " + estoqueAtual);
+                     }
+                 } else {
+                     throw new SQLException("Produto não encontrado.");
+                 }
+             }
+         }
+     }
+
      Connection conn = ConnectionUtil.getConnection();
```

### Impacto:
- SAIDA com quantidade maior que estoque lança SQLException
- Transação é revertida (rollback)
- Usuário vê mensagem clara de erro

---

## PULL REQUEST #4: Remover jLabel6 Orfão

**Arquivo:** `src/main/java/com/sistema/view/TelaPrincipalView.java`

### Alterações:
```diff
-         jLabel6.setText("jLabel6");
+         // jLabel6 removido — era placeholder do NetBeans
```

### Impacto:
- Interface limpa sem textos placeholder visíveis

---

## PULL REQUEST #5: Conectar ENTER do Campo de Busca

**Arquivo:** `src/main/java/com/sistema/view/TelaPrincipalView.java`
**Arquivo:** `src/main/java/com/sistema/controller/ProdutoController.java`

### Alterações em TelaPrincipalView.java:
```diff
  public String getTxtBusca() { return jTextField1.getText(); }
+ public javax.swing.JTextField getTxtBuscaField() { return jTextField1; }
```

### Alterações em ProdutoController.java:
```diff
  private void initListeners() {
      view.getBtnBuscar().addActionListener(e -> buscarProdutos());
+     view.getTxtBuscaField().addActionListener(e -> buscarProdutos());
      view.getBtnCadastrar().addActionListener(e -> salvarProduto(true));
```

### Impacto:
- Usuário pode pressionar ENTER no campo de busca para executar

---

## PULL REQUEST #6: Correções Menores

### Arquivo: `src/main/java/com/sistema/view/LoginView.java`
```diff
- jLabel2.setText("Usúario");
+ jLabel2.setText("Usuário");
```

### Impacto:
- Label agora tem acento correto

---

## RESUMO DE ARQUIVOS MODIFICADOS

| PR | Arquivo | Linhas Alteradas |
|----|---------|------------------|
| #1 | `ProdutoDAO.java` | 85-89, 108-113, 127-129 |
| #2 | `ProdutoController.java` | 241-252 |
| #3 | `MovimentacaoDAO.java` | 40-56 |
| #4 | `TelaPrincipalView.java` | 88 |
| #5 | `TelaPrincipalView.java` + `ProdutoController.java` | 514, 53 |
| #6 | `LoginView.java` | 56 |

---

## BACKUP DOS ARQUIVOS ORIGINAIS

Local: `backup/`
- `ProdutoDAO.java.bak`
- `ProdutoController.java.bak`
- `MovimentacaoDAO.java.bak`
- `TelaPrincipalView.java.bak`
- `LoginView.java.bak`

---

## COMO VERIFICAR

1. Compile o projeto:
   ```bash
   mvn clean compile
   ```

2. Execute:
   ```bash
   mvn exec:java
   ```

3. Teste os cenários do DIAGNOSTICO_TECNICO_COMPLETO.md

---

## TESTES A EXECUTAR

| Teste | Esperado |
|-------|----------|
| Login | Funciona normalmente |
| Cadastrar produto sem categoria | Aparece na lista (não desaparece) |
| ENTER no campo de busca | Executa busca |
| Sair sem estoque suficiente | Mensagem de erro "Estoque insuficiente" |
| Sessão expirada | Redireciona para login |
| Verificar aba Produtos | Sem texto "jLabel6" |

---

*PRs criados — 2026-06-09*