# GUIA COMPLETO — SISTEMA DE CONTROLE DE ESTOQUE
## Explicação para Leigos com Referências de Código

---

## ÍNDICE

1. [O que é o Sistema?](#1-o-que-é-o-sistema)
2. [Fluxo Completo: INPUT → PROCESS → OUTPUT](#2-fluxo-completo-input--process--output)
3. [Análise de Requisitos e Regras de Negócio](#3-análise-de-requisitos-e-regras-de-negócio)
4. [Validações de Dados — Onde Estão](#4-validações-de-dados--onde-estão)
5. [Glossário Completo de Termos Técnicos](#5-glossário-completo-de-termos-técnicos)

---

## 1. O QUE É O SISTEMA?

### 1.1 Definição

O **Sistema de Controle de Estoque** é uma aplicação desktop em Java que ajuda empresas a gerenciar seus produtos. Ele permite:

- Cadastrar usuários com login e senha
- Cadastrar, atualizar e excluir produtos
- Classificar produtos por categorias
- Registrar entradas e saídas de estoque
- Consultar histórico de movimentações

### 1.2 Tecnologias

| Tecnologia | O que é | Onde Está no Sistema |
|-----------|---------|---------------------|
| **Java** | Linguagem de programação | Todo o código |
| **Swing** | Biblioteca gráfica (botões, janelas, tabelas) | Pacote `com.sistema.view` |
| **MySQL** | Banco de dados relacional | Servidor externo |
| **JDBC** | Ponte Java ↔ MySQL | `ConnectionUtil.getConnection()` |
| **Maven** | Gerenciador de build e dependências | `pom.xml` |
| **DAO** | Padrão de acesso a dados | Pacote `com.sistema.dao` |

### 1.3 Arquitetura em Camadas

```
┌──────────────────────────────────────────────────────────────┐
│  CAMADA 1: VIEW (com.sistema.view)                         │
│  LoginView, CadastroUsuarioView, TelaPrincipalView         │
│  → INTERFACE: botões, campos, tabelas que o usuário vê    │
└────────────────────────────┬─────────────────────────────────┘
                             │ ação do usuário (clique, digitação)
                             ▼
┌──────────────────────────────────────────────────────────────┐
│  CAMADA 2: CONTROLLER (com.sistema.controller)             │
│  LoginController, CadastroUsuarioController, ProdutoController│
│  → LÓGICA: validações, regras de negócio                   │
└────────────────────────────┬─────────────────────────────────┘
                             │ objeto criado com dados
                             ▼
┌──────────────────────────────────────────────────────────────┐
│  CAMADA 3: MODEL (com.sistema.model)                       │
│  Usuario, Produto, Categoria, Movimentacao                  │
│  → ESTRUTURA: formato dos dados (como uma ficha)          │
└────────────────────────────┬─────────────────────────────────┘
                             │ operações CRUD
                             ▼
┌──────────────────────────────────────────────────────────────┐
│  CAMADA 4: DAO (com.sistema.dao)                           │
│  UsuarioDAO, ProdutoDAO, CategoriaDAO, MovimentacaoDAO       │
│  → PERSISTÊNCIA: converte para SQL e executa no banco     │
└────────────────────────────┬─────────────────────────────────┘
                             │ JDBC (Java Database Connectivity)
                             ▼
┌──────────────────────────────────────────────────────────────┐
│  CAMADA 5: UTIL (com.sistema.util)                         │
│  ConnectionUtil, HashUtil, SessaoUsuario                    │
│  → INFRAESTRUTURA: conexão, segurança, sessão              │
└────────────────────────────┬─────────────────────────────────┘
                             │ SQL (SELECT, INSERT, UPDATE, DELETE)
                             ▼
┌──────────────────────────────────────────────────────────────┐
│  BANCO DE DADOS: MySQL                                     │
│  Tabelas: categorias, usuarios, produtos, movimentacoes     │
└──────────────────────────────────────────────────────────────┘
```

---

## 2. FLUXO COMPLETO: INPUT → PROCESS → OUTPUT

### 2.1 Fluxo: Login de Usuário

```
INPUT (Usuário digita)                    PROCESS (Sistema processa)                   OUTPUT (Resultado)
─────────────────────────────────         ──────────────────────────────────────────   ──────────────────────────────────

login: "admin"                            LoginController.autenticar()                  ┌─────────────────────────────┐
senha: "admin"                            ├─ Valida campos vazios? (OK)               │  TELA PRINCIPAL (Dashboard)  │
                                          ├─ HashUtil.sha256("admin")               │                             │
┌────────────────────────┐                    → "8c6976e5b5410415bde..."            │  [ PRODUTOS ] [CATEGORIAS]  │
│  Sistema de Controle    │                  ├─ UsuarioDAO.autenticar()             │  [MOVIMENTAÇÕES]           │
│  de Estoque            │                  │   └─ SQL: SELECT WHERE login AND senha│                             │
│                        │                  │   └─ Banco retorna: Usuario(id=1)    │  ┌─────────────────────┐   │
│  Login: [admin       ] │                  ├─ SessaoUsuario.set(usuario)         │  │ Lista de Produtos    │   │
│  Senha: [********     ] │                  └─ TelaPrincipalView abre             │  │ em formato de tabela  │   │
│                        │                                                          │  └─────────────────────┘   │
│  [ENTRAR] [CADASTRAR] │                                                          └─────────────────────────────┘
└────────────────────────┘
```

**Código fonte completo do fluxo (LoginController.java, linhas 51-89):**

```java
private void autenticar() {
    // INPUT: Extrai valores dos campos da View
    String login = view.getLogin();      // "admin"
    String senha = view.getSenha();      // "admin"

    // PROCESS: Valida campos obrigatórios
    if (login.isEmpty() || senha.isEmpty()) {
        JOptionPane.showMessageDialog(view, "Preencha todos os campos!");
        return;
    }

    // PROCESS: Hash da senha (transforma "admin" em código de 64 chars)
    String senhaHash = HashUtil.sha256(senha);
    // Resultado: "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918"

    // PROCESS: Busca usuário no banco
    Optional<Usuario> userOpt = usuarioDAO.autenticar(login, senhaHash);
    // SQL executado: SELECT * FROM usuarios WHERE login='admin' AND senha='8c6976e5...'

    // OUTPUT: Verifica resultado
    if (userOpt.isPresent()) {
        // Sucesso: Salva sessão e abre dashboard
        SessaoUsuario.set(userOpt.get());
        abrirDashboard();  // Abre TelaPrincipalView
    } else {
        // Erro: Mostra mensagem
        JOptionPane.showMessageDialog(view, "Usuário ou senha inválidos!");
    }
}
```

### 2.2 Fluxo: Cadastro de Produto

```
INPUT (Formulário)                         PROCESS (Controller + DAO)                OUTPUT (Banco + Tela)
──────────────────────────────────          ─────────────────────────────────────────   ──────────────────────────────────

Nome: "Caneta Preta"                       ProdutoController.salvarProduto(true)       ┌─────────────────────────────┐
Quantidade: "100"                         ├─ Valida: nome não vazio? (OK)           │  ✓ Produto cadastrado!      │
Preço: "5,50"                            ├─ Valida: qtd é número? (OK)             │                             │
Categoria: "Escritório"                   ├─ Valida: preço é número? (OK)         │  Tabela atualizada:         │
                                          │   └─ "5,50" → 5.50 (vírgula→ponto)  │                             │
┌────────────────────────┐               ├─ Valida: categoria selecionada? (OK)    │  ID │ Nome         │ Qtd  │
│  Cadastrar Produto      │               ├─ ProdutoDAO.inserir(produto)          │  ────┼───────────────┼────  │
│                        │               │   └─ SQL: INSERT INTO produtos          │  22  │ Caneta Preta │ 100  │
│  Nome: [Caneta Preta ] │               └─ Carregar tabela novamente             └─────────────────────────────┘
│  Qtd:   [100        ]  │                                                         
│  Preço: [5,50       ]  │                                                         
│  Cat:   [Escritório▼]  │                                                         
│                        │                                                         
│  [CADASTRAR]           │                                                         
└────────────────────────┘                                                         
```

**Código fonte (ProdutoController.java, linhas 158-213):**

```java
private void salvarProduto(boolean novo) {
    // INPUT: Extrai dados do formulário
    String nome = view.getTxtNome();         // "Caneta Preta"
    String qtdStr = view.getTxtQuantidade(); // "100"
    String precoStr = view.getTxtPreco();    // "5,50"

    // PROCESS: Validação 1 - campos obrigatórios
    if (nome.isEmpty() || qtdStr.isEmpty() || precoStr.isEmpty()) {
        JOptionPane.showMessageDialog(view, "Preencha todos os campos!", "Aviso", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // PROCESS: Conversão de tipos (String → int, BigDecimal)
    int qtd;
    BigDecimal preco;
    try {
        qtd = Integer.parseInt(qtdStr.trim());  // "100" → 100
        // "5,50" → "5.50" → 5.50
        preco = new BigDecimal(precoStr.trim().replace(",", "."));
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(view, "Quantidade ou Preço inválidos!", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // PROCESS: Validação 2 - categoria selecionada
    int index = view.getCbCategoria().getSelectedIndex();
    if (index == -1) {
        JOptionPane.showMessageDialog(view, "Selecione uma categoria!");
        return;
    }

    // PROCESS: Criação do objeto Model
    Produto p = new Produto();
    p.setNome(nome);
    p.setQuantidade(qtd);
    p.setPreco(preco);
    p.setCategoria(listaCategoriasCarregadas.get(index));

    // PROCESS: Inserir no banco via DAO
    if (novo) {
        p.setUsuarioCad(SessaoUsuario.get());
        if (produtoDAO.inserir(p)) {  // INSERT INTO produtos VALUES(...)
            JOptionPane.showMessageDialog(view, "Produto cadastrado!");
        }
    }

    // OUTPUT: Recarrega tabela e limpa campos
    carregarTabelaProdutos(null);
    view.limparCamposProduto();
}
```

**Código SQL gerado (ProdutoDAO.java, linhas 29-39):**

```java
public boolean inserir(Produto produto) throws SQLException {
    String sql = "INSERT INTO produtos (nome, quantidade, preco, id_categoria, id_usuario_cad) VALUES (?, ?, ?, ?, ?)";
    //                    ↓         ↓           ↓        ↓              ↓
    //                   "Caneta"   100        5.50       2              1
    try (Connection conn = ConnectionUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, produto.getNome());           // "Caneta Preta"
        ps.setInt(2, produto.getQuantidade());       // 100
        ps.setBigDecimal(3, produto.getPreco());      // 5.50
        ps.setInt(4, produto.getCategoria().getId()); // 2 (Escritório)
        ps.setInt(5, produto.getUsuarioCad().getId());// 1 (admin)
        return ps.executeUpdate() > 0;  // Executa o INSERT
    }
}
```

### 2.3 Fluxo: Registrar Entrada de Estoque

```
INPUT (Seleção + Quantidade)              PROCESS (Transação)                        OUTPUT (Banco + Tela)
──────────────────────────────────          ─────────────────────────────────────────   ──────────────────────────────────

Produto: Mouse Sem Fio (estoque: 50)     MovimentacaoDAO.registrar(mov)             ┌─────────────────────────────┐
Quantidade: "20"                           ├─ conn.setAutoCommit(false)              │  ✓ Movimentação registrada! │
                                          ├─ Verifica tipo: ENTRADA                │                             │
┌────────────────────────┐               ├─ SQL 1: INSERT INTO movimentacoes       │  Estoque atualizado:        │
│  Produto: Mouse (50)   │               │   VALUES (1, 'ENTRADA', 20, 2)         │  50 + 20 = 70 unidades     │
│  Qtd: [20          ]  │               ├─ SQL 2: UPDATE produtos SET            │                             │
│                        │               │   quantidade = quantidade + 20         │  Histórico atualizado:     │
│  [REGISTRAR ENTRADA]  │               │   WHERE id = 1                         │  ENTRADA: +20 às 14:30    │
└────────────────────────┘               ├─ conn.commit()                          └─────────────────────────────┘
                                          └─ Recarregar tabelas
```

**Código fonte (MovimentacaoDAO.java, linhas 34-84):**

```java
public boolean registrar(Movimentacao mov) throws SQLException {
    String sqlMov = "INSERT INTO movimentacoes (id_produto, tipo, quantidade, id_usuario_mov) VALUES (?, ?, ?, ?)";
    // Para ENTRADA: soma no estoque, para SAIDA: subtrai
    String sqlEstoque = mov.getTipo() == TipoMovimentacao.ENTRADA
        ? "UPDATE produtos SET quantidade = quantidade + ? WHERE id = ?"
        : "UPDATE produtos SET quantidade = quantidade - ? WHERE id = ?";

    Connection conn = ConnectionUtil.getConnection();
    try {
        // INICIA TRANSAÇÃO (todas as ops juntas ou nada)
        conn.setAutoCommit(false);

        // OPERAÇÃO 1: Registrar a movimentação
        try (PreparedStatement ps = conn.prepareStatement(sqlMov)) {
            ps.setInt(1, mov.getProduto().getId());         // id_produto = 1
            ps.setString(2, mov.getTipo().name());         // tipo = "ENTRADA"
            ps.setInt(3, mov.getQuantidade());              // quantidade = 20
            ps.setInt(4, mov.getUsuarioMov().getId());     // id_usuario = 2
            ps.executeUpdate();
        }

        // OPERAÇÃO 2: Atualizar estoque do produto
        try (PreparedStatement ps = conn.prepareStatement(sqlEstoque)) {
            ps.setInt(1, mov.getQuantidade());              // +20
            ps.setInt(2, mov.getProduto().getId());        // WHERE id = 1
            ps.executeUpdate();
        }

        // TRANSAÇÃO BEM SUCEDIDA: Salva tudo
        conn.commit();
        return true;

    } catch (SQLException e) {
        // ERRO: Desfaz tudo que foi feito
        conn.rollback();
        throw e;  // Relança o erro para o Controller mostrar mensagem
    } finally {
        // Volta ao modo normal (cada SQL confirma sozinho)
        conn.setAutoCommit(true);
    }
}
```

### 2.4 Fluxo: Registrar Saída (com Validação de Estoque)

```
INPUT (Seleção + Quantidade)              PROCESS (Validação + Transação)            OUTPUT (Banco + Tela)
──────────────────────────────────          ─────────────────────────────────────────   ──────────────────────────────────

Produto: Chave Philips (estoque: 3)       MovimentacaoDAO.registrar(mov)             ┌─────────────────────────────┐
Quantidade: "5"                           ├─ Verifica: tipo = SAIDA                 │  ⚠ Estoque insuficiente!   │
                                          ├─ SQL: SELECT quantidade FROM produtos     │                             │
┌────────────────────────┐               │   WHERE id = 20                         │  Disponível: 3 unidades    │
│  Produto: Chave (3)    │               │   Resultado: estoqueAtual = 3           │  Solicitado: 5 unidades    │
│  Qtd: [5            ]  │               ├─ Validação: 3 < 5? SIM!                │                             │
│                        │               │   └─ THROW SQLException                  └─────────────────────────────┘
│  [REGISTRAR SAÍDA]    │                  "Estoque insuficiente!"
└────────────────────────┘               └─ conn.rollback() ← NADA muda
```

**Código fonte (MovimentacaoDAO.java, linhas 40-56):**

```java
if (mov.getTipo() == TipoMovimentacao.SAIDA) {
    // VALIDAÇÃO: Verifica se há estoque suficiente NO BANCO
    String checkSql = "SELECT quantidade FROM produtos WHERE id = ?";
    try (Connection checkConn = ConnectionUtil.getConnection();
         PreparedStatement checkPs = checkConn.prepareStatement(checkSql)) {
        checkPs.setInt(1, mov.getProduto().getId());
        try (ResultSet rs = checkPs.executeQuery()) {
            if (rs.next()) {
                int estoqueAtual = rs.getInt("quantidade");
                if (estoqueAtual < mov.getQuantidade()) {
                    // ERRO: Não tem estoque suficiente!
                    throw new SQLException("Estoque insuficiente! Disponível: " + estoqueAtual);
                }
            } else {
                throw new SQLException("Produto não encontrado.");
            }
        }
    }
}
```

### 2.5 Fluxo: Excluir Produto (com CASCADE)

```
INPUT (Seleção)                            PROCESS (Transação em 2 passos)            OUTPUT (Banco + Tela)
──────────────────────────────────          ─────────────────────────────────────────   ──────────────────────────────────

Produto selecionado na tabela:             ProdutoDAO.excluir(20)                       ┌─────────────────────────────┐
Chave Philips (ID=20)                     ├─ conn.setAutoCommit(false)              │  ✓ Produto excluído!        │
                                          ├─ PASSO 1: DELETE FROM movimentacoes      │                             │
┌────────────────────────┐               │   WHERE id_produto = 20                  │  Tabela atualizada:        │
│  ⚠ Deseja excluir?    │               │   → Remove 5 registros de movimentação   │  Chave Philips não aparece  │
│                        │               ├─ PASSO 2: DELETE FROM produtos            │  mais na lista             │
│  [SIM] [NÃO]          │               │   WHERE id = 20                         │                             │
└────────────────────────┘               ├─ conn.commit()                            └─────────────────────────────┘
                                          └─ Recarrega tabela
```

**Código fonte (ProdutoDAO.java, linhas 67-87):**

```java
public boolean excluir(int id) throws SQLException {
    Connection conn = ConnectionUtil.getConnection();
    try {
        conn.setAutoCommit(false);  // Inicia transação

        // PASSO 1: Remove todas as movimentações do produto
        // (ON DELETE CASCADE feito manualmente para garantir)
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM movimentacoes WHERE id_produto = ?")) {
            ps.setInt(1, id);  // id = 20
            ps.executeUpdate();
        }

        // PASSO 2: Remove o produto
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM produtos WHERE id = ?")) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            conn.commit();  // Salva a exclusão
            return rows > 0;
        }
    } catch (SQLException e) {
        conn.rollback();  // Se algo falhar, desfaz tudo
        throw e;
    } finally {
        conn.setAutoCommit(true);
    }
}
```

---

## 3. ANÁLISE DE REQUISITOS E REGRAS DE NEGÓCIO

### 3.1 Requisitos Funcionais

| ID | Requisito | Como Está Implementado | Onde no Código |
|----|-----------|----------------------|----------------|
| RF-01 | Autenticar usuários | Hash SHA-256 + busca no banco | `LoginController.autenticar()` |
| RF-02 | Cadastrar novos usuários | Verifica duplicidade + insere | `CadastroUsuarioController.cadastrar()` |
| RF-03 | CRUD de produtos | Insert, Select, Update, Delete | `ProdutoController` + `ProdutoDAO` |
| RF-04 | CRUD de categorias | Insert, Select | `ProdutoController.cadastrarCategoria()` |
| RF-05 | Registrar entradas | INSERT + UPDATE estoque | `MovimentacaoDAO.registrar()` |
| RF-06 | Registrar saídas | INSERT + UPDATE estoque (com validação) | `MovimentacaoDAO.registrar()` |
| RF-07 | Consultar histórico | SELECT com JOIN | `MovimentacaoDAO.listar()` |
| RF-08 | Buscar produto por nome | LIKE %termo% | `ProdutoDAO.buscarPorNome()` |
| RF-09 | Excluir produto (cascade) | DELETE em transação | `ProdutoDAO.excluir()` |
| RF-10 | Logout | Limpa sessão | `SessaoUsuario.clear()` |

### 3.2 Regras de Negócio (RN)

| ID | Regra | Explicação | Onde no Código |
|----|-------|-----------|----------------|
| **RN-01** | Autenticação Obrigatória | Sem login, não acessa nada | `LoginController.autenticar()` linha 55 |
| **RN-02** | Senha com Hash SHA-256 | Senha guardada como código, não como texto | `HashUtil.sha256()` |
| **RN-03** | Login Único | Não permite dois usuários com mesmo login | `UsuarioDAO.autenticar()` (UNIQUE no banco) |
| **RN-04** | Transação Atômica | Entrada/saída acontece por completo ou não acontece | `MovimentacaoDAO.registrar()` linhas 58-83 |
| **RN-05** | Entrada Soma, Saída Subtrai | ENTRADA: qtd += valor; SAIDA: qtd -= valor | `MovimentacaoDAO.registrar()` linha 36-38 |
| **RN-06** | Categoria Obrigatória | Produto sem categoria não é aceito | `ProdutoController.salvarProduto()` linha 180-183 |
| **RN-07** | Confirmação de Exclusão | Diálogo "Tem certeza?" antes de excluir | `ProdutoController.excluirProduto()` linha 224 |
| **RN-08** | Histórico com LEFT JOIN | Histórico visível mesmo se produto removido | `MovimentacaoDAO.listar()` linhas 136-140 |
| **RN-09** | Auto-init do Banco | Tabelas criadas na primeira conexão | `ConnectionUtil.initDatabase()` |
| **RN-10** | Dois Perfis Padrão | admin/admin e operador/1234 | `ConnectionUtil.initDatabase()` linhas 72-73 |

---

## 4. VALIDAÇÕES DE DADOS — ONDE ESTÃO

### 4.1 Mapa Completo de Validações

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    VALIDAÇÕES NO SISTEMA                                    │
│                                                                              │
│  ╔═══════════════════════════════════════════════════════════════════════════╗  │
│  ║  CLASSE: LoginController.java                                       ║  │
│  ╠═══════════════════════════════════════════════════════════════════════════╣  │
│  ║                                                                    ║  │
│  ║  VALIDAÇÃO 1: Campos de login obrigatórios (linha 55-58)           ║  │
│  ║  ┌────────────────────────────────────────────────────────────┐   ║  │
│  ║  │  if (login.isEmpty() || senha.isEmpty()) {                ║   │  │
│  ║  │      JOptionPane.showMessageDialog("Preencha todos os      ║   │  │
│  ║  │          campos!");                                         ║   │  │
│  ║  │  }                                                         ║   │  │
│  ║  └────────────────────────────────────────────────────────────┘   ║  │
│  ╚═══════════════════════════════════════════════════════════════════════════╝  │
│                                                                              │
│  ╔═══════════════════════════════════════════════════════════════════════════╗  │
│  ║  CLASSE: ProdutoController.java                                     ║  │
│  ╠═══════════════════════════════════════════════════════════════════════════╣  │
│  ║                                                                    ║  │
│  ║  VALIDAÇÃO 2: Campos obrigatórios (linha 164-167)                  ║  │
│  ║  ┌────────────────────────────────────────────────────────────┐   ║  │
│  ║  │  if (nome.isEmpty() || qtdStr.isEmpty() ||             ║   │  │
│  ║  │      precoStr.isEmpty()) {                               ║   │  │
│  ║  │      JOptionPane.showMessageDialog("Preencha todos os    ║   │  │
│  ║  │          campos!");                                      ║   │  │
│  ║  └────────────────────────────────────────────────────────────┘   ║  │
│  ║                                                                    ║  │
│  ║  VALIDAÇÃO 3: Formato numérico (linha 171-177)                  ║  │
│  ║  ┌────────────────────────────────────────────────────────────┐   ║  │
│  ║  │  try {                                                    ║   │  │
│  ║  │      qtd = Integer.parseInt(qtdStr.trim());              ║   │  │
│  ║  │      preco = new BigDecimal(precoStr.trim()               ║   │  │
│  ║  │          .replace(",", "."));                             ║   │  │
│  ║  │  } catch (NumberFormatException e) {                      ║   │  │
│  ║  │      JOptionPane.showMessageDialog("Quantidade ou Preço   ║   │  │
│  ║  │          inválidos!");                                    ║   │  │
│  ║  │  }                                                        ║   │  │
│  ║  └────────────────────────────────────────────────────────────┘   ║  │
│  ║                                                                    ║  │
│  ║  VALIDAÇÃO 4: Categoria selecionada (linha 180-183)              ║  │
│  ║  ┌────────────────────────────────────────────────────────────┐   ║  │
│  ║  │  int index = view.getCbCategoria().getSelectedIndex(); ║   │  │
│  ║  │  if (index == -1) {                                     ║   │  │
│  ║  │      JOptionPane.showMessageDialog("Selecione uma        ║   │  │
│  ║  │          categoria!");                                   ║   │  │
│  ║  │  }                                                        ║   │  │
│  ║  └────────────────────────────────────────────────────────────┘   ║  │
│  ║                                                                    ║  │
│  ║  VALIDAÇÃO 5: Produto selecionado na tabela (linha 198-202)       ║  │
│  ║  ┌────────────────────────────────────────────────────────────┐   ║  │
│  ║  │  int row = view.getTableProdutos().getSelectedRow();     ║   │  │
│  ║  │  if (row == -1) {                                        ║   │  │
│  ║  │      JOptionPane.showMessageDialog("Selecione um         ║   │  │
│  ║  │          produto!");                                      ║   │  │
│  ║  │  }                                                        ║   │  │
│  ║  └────────────────────────────────────────────────────────────┘   ║  │
│  ║                                                                    ║  │
│  ║  VALIDAÇÃO 6: Estoque insuficiente para saída (linha 272-277)  ║  │
│  ║  ┌────────────────────────────────────────────────────────────┐   ║  │
│  ║  │  if (tipo == SAIDA && qtd > estoqueAtual) {            ║   │  │
│  ║  │      JOptionPane.showMessageDialog("Estoque insuficiente! ║   │  │
│  ║  │          Disp: " + estoqueAtual);                        ║   │  │
│  ║  │  }                                                        ║   │  │
│  ║  └────────────────────────────────────────────────────────────┘   ║  │
│  ╚═══════════════════════════════════════════════════════════════════════════╝  │
│                                                                              │
│  ╔═══════════════════════════════════════════════════════════════════════════╗  │
│  ║  CLASSE: MovimentacaoDAO.java                                       ║  │
│  ╠═══════════════════════════════════════════════════════════════════════════╣  │
│  ║                                                                    ║  │
│  ║  VALIDAÇÃO 7: Estoque no banco antes de permitir saída            ║  │
│  ║     (linhas 40-56)                                                ║  │
│  ║  ┌────────────────────────────────────────────────────────────┐   ║  │
│  ║  │  if (mov.getTipo() == SAIDA) {                           ║   │  │
│  ║  │      String checkSql = "SELECT quantidade FROM produtos  ║   │  │
│  ║  │          WHERE id = ?";                                  ║   │  │
│  ║  │      try (Connection checkConn = ...;                      ║   │  │
│  ║  │           PreparedStatement checkPs = ...) {              ║   │  │
│  ║  │          checkPs.setInt(1, mov.getProduto().getId());    ║   │  │
│  ║  │          try (ResultSet rs = checkPs.executeQuery()) {   ║   │  │
│  ║  │              if (rs.next()) {                          ║   │  │
│  ║  │                  int estoqueAtual = rs.getInt(...);      ║   │  │
│  ║  │                  if (estoqueAtual < mov.getQuantidade())  ║   │  │
│  ║  │                      throw new SQLException("Estoque    ║   │  │
│  ║  │                          insuficiente!");                ║   │  │
│  ║  │              }                                           ║   │  │
│  ║  │          }                                               ║   │  │
│  ║  │      }                                                   ║   │  │
│  ║  └────────────────────────────────────────────────────────────┘   ║  │
│  ║                                                                    ║  │
│  ║  VALIDAÇÃO 8: Produto existe no banco (linha 51-53)             ║  │
│  ║  ┌────────────────────────────────────────────────────────────┐   ║  │
│  ║  │  if (!rs.next()) {  // ResultSet vazio                   ║   │  │
│  ║  │      throw new SQLException("Produto não encontrado.");   ║   │  │
│  ║  │  }                                                        ║   │  │
│  ║  └────────────────────────────────────────────────────────────┘   ║  │
│  ╚═══════════════════════════════════════════════════════════════════════════╝  │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 5. GLOSSÁRIO COMPLETO DE TERMOS TÉCNICOS

### 5.1 Termos de Programação e Java

---

#### 🔤 API (Application Programming Interface)

**O que é:** Conjunto de regras e métodos que permite comunicação entre programas.

**Onde está no sistema:**
```java
// JDBC é a API Java para bancos de dados
Connection conn = DriverManager.getConnection(url, user, pass);
PreparedStatement ps = conn.prepareStatement(sql);
ResultSet rs = ps.executeQuery();
```

**Explicação simples:** API é como um garçom. Você pede (chama método), o garçom leva até a cozinha (sistema) e traz a comida de volta (resposta).

---

#### 🔤 AUTO-INCREMENT

**O que é:** Recurso do banco que gera números únicos automaticamente para cada novo registro.

**Onde está no sistema (ConnectionUtil.java linha 67):**
```sql
CREATE TABLE IF NOT EXISTS categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE
);
-- "admin" → id=1 gerado automaticamente
-- "operador" → id=2 gerado automaticamente
```

**Explicação simples:** É como o número de senha de uma clínica. Cada pessoa recebe o próximo número disponível automaticamente.

---

#### 🔤 BIGDECIMAL

**O que é:** Tipo Java para números decimais com precisão total (evita erros de arredondamento).

**Onde está no sistema (Produto.java linha 19):**
```java
private BigDecimal preco;  // Guarda R$ 89.90 corretamente
```

**Exemplo (ProdutoController.java linha 173):**
```java
preco = new BigDecimal(precoStr.trim().replace(",", "."));
// "89,90" → "89.90" → BigDecimal(89.90)
```

**Explicação simples:** Double pode dar problema (0.1 + 0.2 = 0.30000000000000004). BigDecimal garante que R$ 89,90 seja exatamente R$ 89,90.

---

#### 🔤 BOOLEAN

**O que é:** Tipo que só pode ser `true` (verdadeiro) ou `false` (falso).

**Onde está no sistema (SessaoUsuario.java linha 30):**
```java
public static boolean isLogado() {
    return usuarioLogado != null;  // true se tem alguém, false se não
}
```

**Explicação simples:** É como um interruptor: ligado (true) ou desligado (false).

---

#### 🔤 CATCH

**O que é:** Bloco que "pega" erros que acontecem durante a execução.

**Onde está no sistema (LoginController.java linhas 70-72):**
```java
try {
    Optional<Usuario> userOpt = usuarioDAO.autenticar(login, senhaHash);
} catch (SQLException ex) {
    JOptionPane.showMessageDialog(view, "Erro ao conectar: " + ex.getMessage());
}
```

**Explicação simples:** É como o goleiro. O `try` é a jogada, o `catch` é quando o goleiro defende.

---

#### 🔤 CLASS (Classe)

**O que é:** Molde ou planta para criar objetos. Define dados e ações.

**Onde está no sistema (Usuario.java):**
```java
public class Usuario {           // Molde
    private Integer id;          // Dado
    private String login;         // Dado
    private String senha;         // Dado
    public Integer getId() { return id; }    // Ação
    public void setLogin(String login) { ... }  // Ação
}
Usuario admin = new Usuario();  // Objeto criado a partir do molde
```

**Explicação simples:** Classe é como uma ficha de cadastro. A classe define os campos, e o objeto é a ficha preenchida.

---

#### 🔤 COMBOBOX (JComboBox)

**O que é:** Menu suspenso para selecionar uma opção.

**Onde está no sistema (TelaPrincipalView — categoria no formulário de produto):**
```java
// Alimentar combobox (ProdutoController.java linha 135-146)
listaCategoriasCarregadas = categoriaDAO.listar();
DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
for (Categoria c : listaCategoriasCarregadas) {
    model.addElement(c.getNome());  // "Eletrônicos", "Escritório", etc.
}
view.getCbCategoria().setModel(model);

// Pegar seleção (ProdutoController.java linha 179)
int index = view.getCbCategoria().getSelectedIndex();  // ex: 1
Categoria cat = listaCategoriasCarregadas.get(index);   // Categoria selecionada
```

**Explicação simples:** É como o dropdown de país em formulários online.

---

#### 🔤 CONDITIONAL (IF/ELSE)

**O que é:** Estrutura que executa código diferente dependendo de uma condição.

**Onde está no sistema (ProdutoController.java linhas 164-167):**
```java
if (nome.isEmpty() || qtdStr.isEmpty() || precoStr.isEmpty()) {
    // SE campo vazio: mostra erro
    JOptionPane.showMessageDialog(view, "Preencha todos os campos!");
    return;  // Para aqui
}
// SENÃO: continua o cadastro
```

**Explicação simples:** É como um semáforo. Se vermelho → pare. Se verde → siga.

---

#### 🔤 CONSTANTS (Constantes)

**O que é:** Valores que não mudam durante a execução.

**Onde está no sistema (Movimentacao.java linha 16):**
```java
public enum TipoMovimentacao {
    ENTRADA,  // Nunca muda
    SAIDA     // Nunca muda
}
```

**Explicação simples:** São como as regras fixas de um jogo. No xadrez, o cavalo SEMPRE anda em L. Não pode mudar.

---

#### 🔤 CONSTRUCTOR (Construtor)

**O que é:** Método especial chamado automaticamente quando um objeto é criado.

**Onde está no sistema (Usuario.java linhas 27-32):**
```java
public Usuario(Integer id, String login, String senha, String nomeCompleto) {
    this.id = id;              // Inicializa id
    this.login = login;         // Inicializa login
    this.senha = senha;        // Inicializa senha
    this.nomeCompleto = nomeCompleto;
}
// Uso: new Usuario(1, "admin", "hash...", "Administrador")
```

**Explicação simples:** É como o ritual de formatação de um celular novo. Já vem com configurações iniciais.

---

#### 🔤 DAO (Data Access Object)

**O que é:** Padrão que separa a lógica de acesso ao banco do resto da aplicação.

**Onde está no sistema:**
```
com.sistema.dao/
├── UsuarioDAO.java        → acesso a usuários
├── ProdutoDAO.java        → acesso a produtos
├── CategoriaDAO.java      → acesso a categorias
└── MovimentacaoDAO.java   → acesso a movimentações
```

**Exemplo completo (UsuarioDAO.java — método autenticar):**
```java
public Optional<Usuario> autenticar(String login, String senhaHash) throws SQLException {
    String sql = "SELECT id, login, senha, nome_completo FROM usuarios WHERE login = ? AND senha = ?";
    try (Connection conn = ConnectionUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, login);
        ps.setString(2, senhaHash);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                // Converte linha do banco em objeto Usuario
                Usuario user = new Usuario();
                user.setId(rs.getInt("id"));
                user.setLogin(rs.getString("login"));
                user.setSenha(rs.getString("senha"));
                user.setNomeCompleto(rs.getString("nome_completo"));
                return Optional.of(user);
            }
        }
    }
    return Optional.empty();  // Não encontrou
}
```

**Explicação simples:** DAO é como um bibliotecário. Você não entra direto na estante — fala com o bibliotecário (DAO), que sabe onde tudo está.

---

#### 🔤 DDL (Data Definition Language)

**O que é:** Comandos SQL para criar/alterar estruturas de tabelas.

**Onde está no sistema (ConnectionUtil.java linhas 67-70):**
```sql
-- CREATE TABLE: cria tabela
CREATE TABLE IF NOT EXISTS categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(30) NOT NULL UNIQUE,
    senha VARCHAR(64) NOT NULL,
    nome_completo VARCHAR(100)
);
```

**Explicação simples:** DDL é o furniture da柜. CREATE TABLE é comprar uma gaveta nova. DML (INSERT/UPDATE/DELETE) é o que você PÕE dentro.

---

#### 🔤 DML (Data Manipulation Language)

**O que é:** Comandos SQL para manipular dados: INSERT, UPDATE, DELETE.

**Onde está no sistema:**

```java
// INSERT — ProdutoDAO.inserir() linha 30
String sql = "INSERT INTO produtos (nome, quantidade, preco, id_categoria, id_usuario_cad) VALUES (?, ?, ?, ?, ?)";

// UPDATE — ProdutoDAO.atualizar() linha 49
String sql = "UPDATE produtos SET nome = ?, quantidade = ?, preco = ?, id_categoria = ? WHERE id = ?";

// DELETE — ProdutoDAO.excluir() linha 71
String sql = "DELETE FROM movimentacoes WHERE id_produto = ?";
String sql = "DELETE FROM produtos WHERE id = ?";
```

---

#### 🔤 ENUM (Tipo Enumerado)

**O que é:** Tipo que só pode ter valores pré-definidos e fixos.

**Onde está no sistema (Movimentacao.java linha 16):**
```java
public enum TipoMovimentacao {
    ENTRADA,  // Só existe isso
    SAIDA     // ou isso
}
// Uso:
m.setTipo(TipoMovimentacao.ENTRADA);
m.setTipo(TipoMovimentacao.SAIDA);
```

**Explicação simples:** É como dias da semana. Só existem 7 valores possíveis. Não existe "sétima-feira".

---

#### 🔤 ENCAPSULATION (Encapsulamento)

**O que é:** Proteger dados de um objeto, deixando campos "privados" e acessíveis só por métodos.

**Onde está no sistema (Usuario.java):**
```java
public class Usuario {
    private Integer id;     // private = NINGUÉM acessa direto
    private String login;    // private = acesso PROIBIDO diretamente
    
    public Integer getId() {    // public = acesso PERMITIDO
        return this.id;
    }
    
    public void setLogin(String login) {  //可控 = Alteração controlada
        this.login = login;
    }
}
```

**Explicação simples:** É como um cofre. Você não abre diretamente — usa a combinação (métodos get/set).

---

#### 🔤 EXCEPTION (Exceção)

**O que é:** Erro que acontece durante a execução do programa.

**Onde está no sistema (LoginController.java linha 70):**
```java
try {
    Optional<Usuario> userOpt = usuarioDAO.autenticar(login, senhaHash);
} catch (SQLException ex) {
    // Algo deu errado (ex: banco fora do ar)
    JOptionPane.showMessageDialog(view, "Erro ao conectar: " + ex.getMessage());
}
```

**Explicação simples:** É como um acidente. Não é o normal, mas pode acontecer. O catch é o "relatório do acidente".

---

#### 🔤 FIELD (Campo/Atributo)

**O que é:** Variável dentro de uma classe que guarda dados.

**Onde está no sistema (Usuario.java):**
```java
public class Usuario {
    private Integer id;          // Campo 1
    private String login;        // Campo 2
    private String senha;        // Campo 3
    private String nomeCompleto; // Campo 4
}
```

**Explicação simples:** Numa ficha, cada linha é um campo: Nome, Endereço, Telefone.

---

#### 🔤 FOREIGN KEY (Chave Estrangeira)

**O que é:** Campo que referencia a PRIMARY KEY de outra tabela, criando relacionamento.

**Onde está no sistema (init.sql — produtos):**
```sql
CREATE TABLE produtos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100),
    id_categoria INT,                    -- Chave estrangeira
    FOREIGN KEY (id_categoria) REFERENCES categorias(id)
);
```

**Resultado prático:**
```
categorias              produtos
┌─────────────────┐     ┌─────────────────────────────┐
│ id=1 Eletrônicos│◄────│ id_categoria=1  (Mouse)     │
│ id=2 Escritório │     │ id_categoria=2  (Papel)    │
└─────────────────┘     └─────────────────────────────┘
```

**Explicação simples:** É como colar um post-it de referência. O post-it na pasta diz "categoria: 1", apontando para o índice "1 = Eletrônicos".

---

#### 🔤 GETTER E SETTER

**O que é:** Métodos para LER (getter) e ALTERAR (setter) um campo privado.

**Onde está no sistema (Usuario.java):**
```java
// GETTER — lê o valor
public String getLogin() {
    return this.login;
}

// SETTER — altera o valor
public void setLogin(String login) {
    this.login = login;
}

// Uso:
usuario.setLogin("novoLogin");          // Altera
String nome = usuario.getLogin();       // Lê
```

**Explicação simples:** Getter é olhar pela janela (só vê). Setter é ter a chave da porta (pode mudar).

---

#### 🔤 HASH / HASHING (SHA-256)

**O que é:** Função que transforma texto em código irreversível de 64 caracteres.

**Onde está no sistema (HashUtil.java):**
```java
public static String sha256(String base) {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(base.getBytes("UTF-8"));
    StringBuilder hexString = new StringBuilder();
    for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) hexString.append('0');
        hexString.append(hex);
    }
    return hexString.toString();
}
```

**Resultado:**
```
Entrada:  "admin"  →  "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918"
Entrada:  "1234"   →  "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4"
```

**Explicação simples:** É como misturar cores. Não consegue voltar às cores originais. Mesmo que roubem o banco, não descobrem a senha.

---

#### 🔤 ITERATION (Loop / FOR)

**O que é:** Repetir uma ação para cada item de uma lista.

**Onde está no sistema (ProdutoController.java linhas 83-91):**
```java
List<Produto> produtos = produtoDAO.listar();

// Para CADA produto na lista, faça:
for (Produto p : produtos) {
    model.addRow(new Object[]{
        p.getId(),
        p.getNome(),
        p.getQuantidade(),
        p.getPreco()
    });
}
```

**Explicação simples:** É como passar lista de chamada. Olha primeiro, anota, olha segundo, anota... até acabar.

---

#### 🔤 JDBC (Java Database Connectivity)

**O que é:** API Java que permite comunicar com bancos de dados SQL.

**Onde está no sistema (ProdutoDAO.java):**
```java
// 1. Pega conexão
Connection conn = ConnectionUtil.getConnection();

// 2. Prepara指令 (SQL)
PreparedStatement ps = conn.prepareStatement(sql);
ps.setString(1, "Mouse");  // Substitui 1º "?" por "Mouse"
ps.setInt(2, 50);          // Substitui 2º "?" por 50

// 3. Executa
ResultSet rs = ps.executeQuery();  // Para SELECT
// int rows = ps.executeUpdate();   // Para INSERT/UPDATE/DELETE

// 4. Lê resultados
while (rs.next()) {
    String nome = rs.getString("nome");
}
```

**Explicação simples:** JDBC é o tradutor. O programa fala Java, o banco fala SQL. O JDBC converte Java → SQL → Java.

---

#### 🔤 JOPTIONPANE

**O que é:** Componente Swing para mostrar mensagens ao usuário.

**Onde está no sistema (LoginController.java linha 56):**
```java
// Mensagem simples de aviso
JOptionPane.showMessageDialog(view, "Preencha todos os campos!");

// Pergunta de confirmação (ProdutoController.java linha 224)
int confirm = JOptionPane.showConfirmDialog(view,
    "Deseja realmente excluir?", "Confirmação",
    JOptionPane.YES_NO_OPTION);
// confirm = 0 se YES, confirm = 1 se NO
```

---

#### 🔤 JTABLE

**O que é:** Componente Swing que exibe dados em formato de tabela.

**Onde está no sistema (TelaPrincipalView):**
```java
// Popular tabela (ProdutoController.java linhas 74-95)
DefaultTableModel model = (DefaultTableModel) view.getTableProdutos().getModel();
model.setRowCount(0);  // Limpa

for (Produto p : produtos) {
    model.addRow(new Object[]{
        p.getId(),        // Coluna 0: ID
        p.getNome(),       // Coluna 1: Nome
        p.getQuantidade(), // Coluna 2: Quantidade
        p.getPreco()       // Coluna 3: Preço
    });
}
```

**Explicação simples:** É como uma planilha Excel dentro do programa.

---

#### 🔤 KEY-VALUE (Par Chave-Valor)

**O que é:** Estrutura onde cada item tem um identificador único (chave) e um valor.

**Onde está no sistema (db.properties):**
```properties
db.url=jdbc:mysql://localhost:3306/sistema_estoque
db.user=root
db.pass=
```

**Explicação simples:** É como uma agenda. Nome (chave) → Telefone (valor).

---

#### 🔤 LOCALDATETIME

**O que é:** Tipo Java para data e hora juntas.

**Onde está no sistema (Movimentacao.java linha 22):**
```java
private LocalDateTime dataMov;  // Guarda: 2026-04-01 08:00:00
```

---

#### 🔤 METADATA

**O que é:** Dados que descrevem outros dados.

**Onde está no sistema:**
```sql
DESCRIBE produtos;
-- Retorna: id (int), nome (varchar), quantidade (int), preco (decimal)...
-- Esses são os METADADOS da tabela
```

**Explicação simples:** É como a capa de um livro. A capa descreve o conteúdo (título, autor, páginas).

---

#### 🔤 METHOD (Método)

**O que é:** Função dentro de uma classe que define o que um objeto pode fazer.

**Onde está no sistema (Usuario.java):**
```java
public class Usuario {
    public String getLogin() {        // Método 1: lê login
        return this.login;
    }
    
    public void setLogin(String login) {  // Método 2: altera login
        this.login = login;
    }
    
    @Override
    public String toString() {      // Método 3: representação em texto
        return nomeCompleto != null ? nomeCompleto : login;
    }
}
```

**Explicação simples:** É como um botão no controle remoto. Cada botão (método) faz uma coisa quando você aperta.

---

#### 🔤 NULL

**O que é:** Valor especial que significa "nada" ou "não tem valor".

**Onde está no sistema (SessaoUsuario.java):**
```java
private static Usuario usuarioLogado;  // Começa como null

// Quando ninguém logou:
if (usuarioLogado == null) {
    // Mostra erro ou redireciona para login
}
```

**Explicação simples:** É como uma caixa vazia. É diferente de 0 (número) ou "" (texto vazio).

---

#### 🔤 OBJECT (Objeto)

**O que é:** Instância real de uma classe, criada na memória.

**Onde está no sistema:**
```java
// Usuario é a CLASSE (molde)
public class Usuario { ... }

// usuarioLogado é o OBJETO (coisa real)
Usuario usuarioLogado = new Usuario(1, "admin", "hash...", "Administrador");
```

**Explicação simples:** Classe é a receita. Objeto é o bolo real feito com ela.

---

#### 🔤 OPTIONAL

**O que é:** Wrapper que pode ou não conter um valor. Evita erros de null.

**Onde está no sistema (LoginController.java linha 62):**
```java
Optional<Usuario> userOpt = usuarioDAO.autenticar(login, senhaHash);

// Verifica se tem valor antes de usar
if (userOpt.isPresent()) {
    Usuario user = userOpt.get();  // Pega o usuário
    SessaoUsuario.set(user);
} else {
    JOptionPane.showMessageDialog("Login inválido!");
}
```

**Explicação simples:** É como uma caixa que pode estar vazia ou com algo dentro. Verifica se tem antes de usar.

---

#### 🔤 OVERRIDE

**O que é:** Reescrever um método da classe pai.

**Onde está no sistema (Usuario.java linha 46):**
```java
public class Usuario {
    @Override  // Indica que este método substitui o padrão do Java
    public String toString() {
        return nomeCompleto != null ? nomeCompleto : login;
    }
}
```

**Explicação simples:** É como um filho que herda uma habilidade mas faz do seu jeito.

---

#### 🔤 PACKAGE

**O que é:** Pasta que organiza classes relacionadas.

**Onde está no sistema:**
```
com/sistema/
├── view/           → Interface (LoginView, TelaPrincipalView...)
├── controller/     → Lógica (LoginController, ProdutoController...)
├── model/          → Dados (Usuario, Produto, Categoria...)
├── dao/            → Acesso a banco (UsuarioDAO, ProdutoDAO...)
└── util/           → Ferramentas (ConnectionUtil, HashUtil...)
```

**Explicação simples:** É como organizar arquivos em pastas no computador.

---

#### 🔤 PREPAREDSTATEMENT

**O que é:** Forma segura de executar SQL, evitando ataques de SQL Injection.

**Onde está no sistema (ProdutoDAO.java linhas 31-38):**
```java
String sql = "INSERT INTO produtos (nome, quantidade, preco, ...) VALUES (?, ?, ?, ?)";
// "?" são lugarholders (valores ainda não definidos)

try (PreparedStatement ps = conn.prepareStatement(sql)) {
    ps.setString(1, produto.getNome());        // Substitui 1º "?"
    ps.setInt(2, produto.getQuantidade());     // Substitui 2º "?"
    ps.setBigDecimal(3, produto.getPreco());    // Substitui 3º "?"
    ps.executeUpdate();
}
```

**Explicação simples:** É como um formulário com campos em branco. O valor é tratado como DADO, não como CÓDIGO SQL.

---

#### 🔤 PRIMARY KEY (Chave Primária)

**O que é:** Identificador único de cada registro.

**Onde está no sistema (init.sql):**
```sql
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,  -- id=1 é só do admin, id=2 só do operador
    ...
);
```

**Explicação simples:** É como o CPF. Cada brasileiro tem um único. Identifica uma pessoa específica.

---

#### 🔤 PRIVATE / PUBLIC

**O que é:** Controlam quem pode acessar cada parte do código.

**Onde está no sistema (Usuario.java):**
```java
public class Usuario {
    private Integer id;      // PRIVATE: só esta classe acessa
    public String getLogin() { ... }  // PUBLIC: qualquer lugar acessa
}
```

| Modificador | Própria Classe | Mesmo Pacote | Qualquer Lugar |
|-------------|----------------|--------------|----------------|
| `private` | ✅ | ❌ | ❌ |
| `public` | ✅ | ✅ | ✅ |

---

#### 🔤 PROPERTIES

**O que é:** Arquivo de configuração com pares chave-valor.

**Onde está no sistema (db.properties):**
```properties
db.url=jdbc:mysql://localhost:3306/sistema_estoque?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
db.user=root
db.pass=
```

**Lido em (ConnectionUtil.java linha 36-38):**
```java
Properties props = new Properties();
props.load(ConnectionUtil.class.getClassLoader().getResourceAsStream("db.properties"));
String url = props.getProperty("db.url");
```

---

#### 🔤 RETURN

**O que é:** Keyword que devolve um valor para quem chamou o método.

**Onde está no sistema (HashUtil.java linha 32):**
```java
public static String sha256(String base) {
    // ... faz cálculo ...
    return hexString.toString();  // Devolve o resultado
}

// Uso:
String hash = HashUtil.sha256("admin");  // hash recebe o valor retornado
```

---

#### 🔤 ROLLBACK

**O que é:** Desfazer todas as operações de uma transação quando algo dá errado.

**Onde está no sistema (MovimentacaoDAO.java linhas 78-80):**
```java
try {
    conn.setAutoCommit(false);
    // INSERT + UPDATE...
    conn.commit();  // TUDO OK → Salva
} catch (SQLException e) {
    conn.rollback();  // ERRO → Desfaz TUDO
    throw e;
}
```

**Explicação simples:** É como Ctrl+Z no Word. Volta ao estado anterior quando algo dá errado.

---

#### 🔤 SERIALIZATION (Serialização)

**O que é:** Converter objetos em formato que pode ser guardado ou transmitido.

**Onde está no sistema (JDBC faz implicitamente):**
```java
// Objeto Java (na memória)
produto.setNome("Mouse");

// JDBC converte para SQL (texto)
"INSERT INTO produtos VALUES ('Mouse', ...)"

// ResultSet (banco) volta para objetos Java
while (rs.next()) {
    Produto p = new Produto();
    p.setNome(rs.getString("nome"));
}
```

---

#### 🔤 SQL (Structured Query Language)

**O que é:** Linguagem para comunicar com bancos de dados.

**Onde está no sistema (variados em DAO):**
```sql
-- SELECT: buscar
SELECT * FROM produtos WHERE nome LIKE '%mouse%';

-- INSERT: inserir
INSERT INTO usuarios (login, senha) VALUES ('novo', 'hash...');

-- UPDATE: atualizar
UPDATE produtos SET quantidade = 100 WHERE id = 1;

-- DELETE: apagar
DELETE FROM produtos WHERE id = 5;
```

**Explicação simples:** SQL é como perguntar ao bibliotecário. "Me mostra todos os livros do autor X" é um SELECT.

---

#### 🔤 STATIC

**O que é:** Algo que pertence à CLASSE, não a um objeto. Existe em apenas uma cópia.

**Onde está no sistema (SessaoUsuario.java):**
```java
public class SessaoUsuario {
    private static Usuario usuarioLogado;  // STATIC: só existe UMA

    public static void set(Usuario usuario) {  // STATIC: acessa pela classe
        usuarioLogado = usuario;
    }
    
    public static Usuario get() {  // STATIC: não precisa new
        return usuarioLogado;
    }
}
```

**Explicação simples:** É como o quadro de avisos da empresa. Todos compartilham o MESMO quadro.

---

#### 🔤 STRING

**O que é:** Tipo de dado para texto.

**Onde está no sistema (Usuario.java):**
```java
private String login;        // "admin"
private String senha;        // "8c6976e5b5410415bde908bd4..."
private String nomeCompleto; // "Administrador"
```

**Operações comuns:**
```java
"admin".isEmpty();           // false
"admin".length();            // 5
"admin".toUpperCase();       // "ADMIN"
"5,50".replace(",", ".");    // "5.50"
```

---

#### 🔤 SWING

**O que é:** Biblioteca gráfica do Java para criar interfaces visuais.

**Onde está no sistema:** Todo o pacote `com.sistema.view`

**Componentes usados:**
- `JFrame` — janela principal
- `JTextField` — campo de texto
- `JPasswordField` — campo de senha
- `JButton` — botão
- `JTable` — tabela
- `JComboBox` — lista suspensa
- `JTabbedPane` — abas
- `JOptionPane` — caixas de mensagem

---

#### 🔤 THIS

**O que é:** Keyword que referencia o próprio objeto.

**Onde está no sistema (Usuario.java):**
```java
public void setLogin(String login) {
    this.login = login;
    // this.login = campo da CLASSE
    // login = parâmetro do MÉTODO
}
```

**Explicação simples:** É como dizer "EU MESMO". `this.login` é "o login DO OBJETO ATUAL".

---

#### 🔤 THROW

**O que é:** Disparar um erro manualmente.

**Onde está no sistema (MovimentacaoDAO.java linha 49):**
```java
if (estoqueAtual < mov.getQuantidade()) {
    throw new SQLException("Estoque insuficiente! Disponível: " + estoqueAtual);
    // Dispara erro que será pego pelo catch
}
```

**Explicação simples:** É como acionar o alarme de incêndio. Você detecta problema e Dispara (throw) o alarme.

---

#### 🔤 TRANSACTION (Transação)

**O que é:** Conjunto de operações que devem acontecer juntas. Se uma falhar, todas são desfeitas.

**Onde está no sistema (MovimentacaoDAO.java linhas 58-83):**
```java
conn.setAutoCommit(false);  // Modo manual

try {
    // OPERAÇÃO 1: Registrar movimentação
    INSERT INTO movimentacoes VALUES (...);
    
    // OPERAÇÃO 2: Atualizar estoque
    UPDATE produtos SET quantidade = quantidade + 20 WHERE id = ?;
    
    conn.commit();  // TUDO OK → Salva
} catch (SQLException e) {
    conn.rollback();  // ERRO → Desfaz tudo
}
```

**Explicação simples:** É como transferência bancária. Ou sai das duas contas ou não sai de nenhuma.

---

#### 🔤 TRY

**O que é:** Bloco que tenta executar código, capturando erros se acontecerem.

**Onde está no sistema (LoginController.java linhas 60-72):**
```java
try {
    // TENTA: fazer login
    String senhaHash = HashUtil.sha256(senha);
    Optional<Usuario> userOpt = usuarioDAO.autenticar(login, senhaHash);
} catch (SQLException ex) {
    // ERRO: mostra mensagem
    JOptionPane.showMessageDialog(view, "Erro ao conectar: " + ex.getMessage());
}
```

---

#### 🔤 UNIQUE

**O que é:** Regra que impede valores duplicados em um campo.

**Onde está no sistema (init.sql):**
```sql
CREATE TABLE usuarios (
    login VARCHAR(30) NOT NULL UNIQUE,  -- Não pode ter dois "admin"
    ...
);

CREATE TABLE categorias (
    nome VARCHAR(50) NOT NULL UNIQUE,  -- Não pode ter dois "Eletrônicos"
    ...
);
```

**Explicação simples:** É como CPF no Brasil. Só existe um com cada número.

---

#### 🔤 VARIABLE (Variável)

**O que é:** Espaço na memória que guarda um valor que pode mudar.

**Onde está no sistema (LoginController.java linhas 52-53):**
```java
String login = view.getLogin();   // Caixa etiquetada "login" contém "admin"
String senha = view.getSenha();   // Caixa etiquetada "senha" contém "admin"
int idade = 25;                   // Caixa etiquetada "idade" contém 25
boolean ativo = true;            // Caixa etiquetada "ativo" contém verdadeiro
```

---

#### 🔤 VIEW (Camada de Apresentação)

**O que é:** Camada responsável por exibir dados e capturar ações do usuário.

**Onde está no sistema:** Pacote `com.sistema.view`

**Responsabilidades:**
- Criar componentes gráficos (botões, campos, tabelas)
- Capturar eventos (cliques, digitação)
- Exibir mensagens e resultados
- NÃO valida nem acessa banco diretamente

---

### 5.2 Termos Específicos do Sistema

---

#### 🗄️ AUTO-INIT DO BANCO

**O que é:** Sistema cria tabelas e insere dados automaticamente na primeira execução.

**Onde está no sistema (ConnectionUtil.java linhas 65-96):**
```java
private static void initDatabase(Connection conn) {
    // CRIA TABELAS
    st.execute("CREATE TABLE IF NOT EXISTS categorias (...)");
    st.execute("CREATE TABLE IF NOT EXISTS usuarios (...)");
    st.execute("CREATE TABLE IF NOT EXISTS produtos (...)");
    st.execute("CREATE TABLE IF NOT EXISTS movimentacoes (...)");
    
    // INSERE DADOS INICIAIS
    st.execute("INSERT IGNORE INTO usuarios VALUES ('admin', '8c6976e5...', 'Administrador')");
    st.execute("INSERT IGNORE INTO categorias VALUES ('Eletrônicos'), ('Escritório'), ...");
    st.execute("INSERT IGNORE INTO produtos VALUES (1,'Mouse',50,89.90,1,1), ...");
    // INSERT IGNORE = só insere se ainda não existir
}
```

---

#### 🗄️ CADASTRO DE USUÁRIO

**Fluxo completo (CadastroUsuarioController):**
```
1. View: getNovoLogin() + getNovaSenha()
2. Controller: validar vazio, verificar duplicidade
3. Hash: HashUtil.sha256(senha)
4. DAO: usuarioDAO.inserir(novoUsuario)
5. SQL: INSERT INTO usuarios VALUES (...)
6. View: sucesso + volta para login
```

---

#### 🗄️ COMBOBOX (Categoria)

**Onde está (ProdutoController.java linhas 135-146):**
```java
private void carregarCategoriasCombo() {
    listaCategoriasCarregadas = categoriaDAO.listar();
    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
    for (Categoria c : listaCategoriasCarregadas) {
        model.addElement(c.getNome());  // "Eletrônicos", "Escritório", etc.
    }
    view.getCbCategoria().setModel(model);
}
```

---

#### 🗄️ CONNECTIONUTIL

**O que é:** Classe que gerencia a conexão com o MySQL.

**Onde está:** `com.sistema.util.ConnectionUtil`

**Código principal (linhas 33-56):**
```java
public static synchronized Connection getConnection() throws SQLException {
    if (conn == null || conn.isClosed()) {
        // Lê db.properties
        Properties props = new Properties();
        props.load(ConnectionUtil.class.getClassLoader()
            .getResourceAsStream("db.properties"));
        
        // Conecta ao MySQL
        conn = DriverManager.getConnection(
            props.getProperty("db.url"),
            props.getProperty("db.user"),
            props.getProperty("db.pass")
        );
        
        // Primeira vez? Cria banco e tabelas
        initDatabase(conn);
    }
    return conn;
}
```

---

#### 🗄️ CRUD

**O que é:** Acrônimo das 4 operações básicas: Create, Read, Update, Delete.

**Onde está no sistema (ProdutoDAO):**

| Operação | Método | SQL |
|----------|--------|-----|
| **C**reate | `inserir()` | INSERT |
| **R**ead | `listar()`, `buscarPorNome()` | SELECT |
| **U**pdate | `atualizar()` | UPDATE |
| **D**elete | `excluir()` | DELETE |

---

#### 🗄️ DB.PROPERTIES

**Onde está:** `src/main/resources/db.properties`

```properties
db.url=jdbc:mysql://localhost:3306/sistema_estoque?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
db.user=root
db.pass=
```

**Parâmetros:**
- `jdbc:mysql://localhost:3306/` — MySQL local na porta 3306
- `sistema_estoque` — nome do banco
- `createDatabaseIfNotExist=true` — cria banco se não existir
- `serverTimezone=UTC` — define fuso horário
- `useSSL=false` — desativa SSL (ambiente local)

---

#### 🗄️ ESTOQUE BAIXO

**Validação (MovimentacaoDAO.java linhas 40-56):**
```java
if (mov.getTipo() == TipoMovimentacao.SAIDA) {
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
            }
        }
    }
}
```

**Exemplo:**
```
Estoque atual: Mouse = 3 unidades
Tentativa: SAÍDA de 5 unidades
→ Sistema REJEITA: "Estoque insuficiente! Disponível: 3"
```

---

#### 🗄️ HASH DE SENHA

**Onde está (HashUtil.java):**
```
Entrada:  "admin"  →  "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918"
Entrada:  "1234"   →  "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4"
```

**Por que usar?**
- Se alguém roubar o banco, não descobre as senhas
- Não existe "desfazer" hash — é mão única

---

#### 🗄️ JOIN (LEFT JOIN)

**O que é:** Comando SQL que combina dados de duas ou mais tabelas.

**Onde está (MovimentacaoDAO.java linhas 136-140):**
```sql
SELECT m.*, u.nome_completo as usu_nome, u.login as usu_login, p.nome as prod_nome
FROM movimentacoes m
LEFT JOIN usuarios u ON m.id_usuario_mov = u.id
LEFT JOIN produtos p ON m.id_produto = p.id
ORDER BY m.data_mov DESC
```

**LEFT JOIN explica:** Pega tudo da tabela da esquerda (movimentações) e preenche dados do usuário/produto quando encontrar. Se não encontrar, preenche com NULL.

---

#### 🗄️ LOGIN E SENHA

**Usuários padrão:**
| Login | Senha | Nome |
|-------|-------|------|
| admin | admin | Administrador |
| operador | 1234 | Operador de Estoque |

**Fluxo (LoginController.autenticar()):**
```
1. Digita: login="admin", senha="admin"
2. Hash: sha256("admin") → "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918"
3. SQL: SELECT WHERE login='admin' AND senha='8c6976e5...'
4. Encontrou? → Abre dashboard
5. Não encontrou? → "Login inválido"
```

---

#### 🗄️ MOCK DATA

**Dados de demonstração inseridos automaticamente:**
- 2 usuários: admin + operador
- 8 categorias: Eletrônicos, Escritório, Limpeza, Alimentos, Bebidas, Higiene, Roupas, Ferramentas
- 21 produtos: Mouse, Teclado, Papel A4, Café, etc.
- 140 movimentações: Histórico de abril a julho/2026

---

#### 🗄️ MODEL (Camada)

**Onde está:** `com.sistema.model`

**Classes:**
```
Usuario.java       → id, login, senha, nomeCompleto
Produto.java      → id, nome, quantidade, preco, categoria, usuarioCad
Categoria.java    → id, nome
Movimentacao.java → id, produto, tipo, quantidade, dataMov, usuarioMov
```

---

#### 🗄️ SESSÃO DE USUÁRIO

**Onde está (SessaoUsuario.java):**
```java
private static Usuario usuarioLogado;  // Uma variável, compartilhada por toda app

public static void set(Usuario usuario) {
    usuarioLogado = usuario;  // Define quem está logado
}

public static Usuario get() {
    return usuarioLogado;  // Pega quem está logado
}

public static void clear() {
    usuarioLogado = null;  // Faz logout
}

public static boolean isLogado() {
    return usuarioLogado != null;
}
```

---

### 5.3 Termos de Banco de Dados

---

#### 📊 BANCO DE DADOS RELACIONAL

**O que é:** Sistema que organiza dados em tabelas com linhas e colunas.

**Tabelas do sistema:**
```
categorias      → 1 categoria tem N produtos
usuarios        → 1 usuário cadastra N produtos e N movimentações
produtos        → 1 produto tem N movimentações
movimentacoes   → N movimentações pertencem a 1 produto e 1 usuário
```

---

#### 📊 COLUNA

**O que é:** Uma propriedade específica de todos os registros de uma tabela.

**Exemplo (tabela produtos):**
```
┌─────────┬───────────────┬────────────┬─────────┐
│ id      │ nome          │ quantidade │ preco   │  ← COLUNAS
├─────────┼───────────────┼───────────┼─────────┤
│ 1       │ Mouse Sem Fio │    50     │  89.90  │  ← LINHA
│ 2       │ Teclado Mec.   │    30     │ 249.90  │  ← LINHA
└─────────┴───────────────┴───────────┴─────────┘
```

---

#### 📊 DECIMAL(10,2)

**O que é:** Tipo numérico com 10 dígitos totais e 2 casas decimais.

**Exemplo:** R$ 1.234.567,89 cabe em DECIMAL(10,2), mas R$ 1.234.567.890,00 não cabe.

---

#### 📊 ENUM('ENTRADA','SAIDA')

**O que é:** Tipo que só aceita valores pré-definidos.

**Onde está (movimentacoes.tipo):**
```
Só pode ser: 'ENTRADA' ou 'SAIDA'
Tentativa de inserir outro valor → ERRO do banco
```

---

#### 📊 FOREIGN KEY (FK)

**Relacionamentos no sistema:**
```
categorias.id  ──→ produtos.id_categoria      (1:N)
usuarios.id    ──→ produtos.id_usuario_cad    (1:N)
produtos.id    ──→ movimentacoes.id_produto   (1:N)
usuarios.id    ──→ movimentacoes.id_usuario_mov (1:N)
```

---

#### 📊 LEFT JOIN

**O que é:** JOIN que mantém todos os registros da tabela da esquerda, mesmo sem correspondência.

**Resultado (movimentações mesmo sem produto):**
```
Mouse         │ 20       │ ENTRADA
Teclado       │ NULL      │ NULL   ← Teclado nunca teve movimentação
Papel A4      │ 50        │ ENTRADA
```

---

#### 📊 LINHA (Row / Registro)

**Exemplo (tabela produtos):**
```
┌─────────────────────────────────────────────────────┐
│ LINHA = todos os dados de UM produto específico    │
│                                                     │
│ id = 1                                            │
│ nome = "Mouse Sem Fio"                             │
│ quantidade = 50                                    │
│ preco = 89.90                                     │
│ id_categoria = 1                                   │
└─────────────────────────────────────────────────────┘
```

---

#### 📊 PRIMARY KEY (PK)

**No sistema:**
```
usuarios.id         → 1, 2, 3, ... (auto-incrementado)
categorias.id       → 1, 2, 3, ... (auto-incrementado)
produtos.id         → 1, 2, 3, ... (auto-incrementado)
movimentacoes.id    → 1, 2, 3, ... (auto-incrementado)
```

**Características:**
- UNIQUE — não se repete
- NOT NULL — sempre tem valor
- AUTO_INCREMENT — o banco escolhe automaticamente

---

#### 📊 SQL INJECTION

**O que é:** Ataque onde alguém insere código SQL malicioso em campos.

**Exemplo de ataque:**
```
Campo Nome: "'; DROP TABLE usuarios; --"
SQL gerado: DELETE FROM usuarios WHERE nome = ''; DROP TABLE usuarios; --'
```

**Prevenção no sistema (PreparedStatement):**
```java
// SEGURO: o valor é tratado como DADO, não como CÓDIGO
ps.setString(1, nomeDigitado);  // "'; DROP TABLE..." vira apenas texto
```

---

#### 📊 TABELA

**4 tabelas do sistema:**

| Tabela | O que guarda |
|--------|-------------|
| `categorias` | Tipos de produtos (Eletrônicos, Escritório...) |
| `usuarios` | Pessoas que acessam (admin, operador) |
| `produtos` | Itens em estoque (Mouse, Papel, Café...) |
| `movimentacoes` | Histórico de entradas e saídas |

---

#### 📊 VARCHAR(n)

**O que é:** Tipo de dado para texto com até n caracteres.

**No sistema:**
```sql
login VARCHAR(30)        -- "admin" (5 chars, cabem 30)
nome VARCHAR(100)        -- "Mouse Sem Fio Logitech" (24 chars)
senha VARCHAR(64)        -- hash SHA-256 sempre tem 64 chars
```

---

### 5.4 Termos de Arquitetura

---

#### 🏗️ ARQUITETURA EM CAMADAS (Layered Architecture)

**Organização do código em camadas distintas:**

```
VIEW       → Interface gráfica (apresentação)
CONTROLLER → Lógica de negócio (decisões)
MODEL      → Estrutura dos dados
DAO        → Acesso ao banco
UTIL       → Infraestrutura (conexão, segurança)
```

**Princípio:** Cada camada só conhece a camada diretamente abaixo dela.

---

#### 🏗️ BUSINESS LOGIC (Lógica de Negócio)

**Regras que definem como o sistema funciona:**

- "Senha deve ser hasheada antes de salvar"
- "Saída não pode deixar estoque negativo"
- "Não pode ter dois usuários com mesmo login"
- "Entrada soma no estoque, saída subtrai"

**Onde estão:** Principalmente no CONTROLLER e em validações no DAO.

---

#### 🏗️ COHESION (Coesão)

**Alta coesão (bom):**
```
UsuarioDAO só faz coisas de usuário: inserir, buscar, autenticar
MovimentacaoDAO só faz coisas de movimentação: registrar, listar
```

**Baixa coesão (ruim):**
```
ClasseMisc faz:
  - Validar email
  - Conectar ao banco
  - Enviar email
  - Formatar data
  → Tudo misturado, difícil de manter
```

---

#### 🏗️ COUPLING (Acoplamento)

**Baixo acoplamento (bom):**
```
LoginController depende de:
  • UsuarioDAO (só para buscar usuário)
  • HashUtil (só para hashar senha)
  • SessaoUsuario (só para guardar sessão)
```

**Alto acoplamento (ruim):**
```
ClasseTudoFaz:
  • Sabe usar banco
  • Sabe validar
  • Sabe enviar email
  → Se mudar banco, quebra tudo
```

---

#### 🏗️ DASHBOARD

**O que é:** Tela principal que mostra visão geral do sistema.

**No sistema:** `TelaPrincipalView` com 3 abas:
- Produtos (CRUD)
- Categorias (cadastro rápido)
- Movimentações (histórico + entradas/saídas)

---

#### 🏗️ MVC (Model-View-Controller)

**Padrão de arquitetura com 3 camadas:**

```
MODEL (com.sistema.model)
  → Usuario, Produto, Categoria, Movimentacao
  → "O QUÊ" — quais dados existem

VIEW (com.sistema.view)
  → LoginView, TelaPrincipalView
  → "COMO SE VÊ" — interface gráfica

CONTROLLER (com.sistema.controller)
  → LoginController, ProdutoController
  → "O QUE FAZER" — lógica e decisões
```

---

#### 🏗️ SINGLETON

**O que é:** Padrão que garante uma única instância de uma classe.

**ConnectionUtil (linha 22):**
```java
private static Connection conn;  // Uma só instância

public static Connection getConnection() {
    if (conn == null || conn.isClosed()) {
        conn = ...;  // Cria só na primeira vez
    }
    return conn;  // Sempre devolve a mesma conexão
}
```

**SessaoUsuario (linha 12):**
```java
private static Usuario usuarioLogado;  // Uma só sessão para toda app
```

---

#### 🏗️ EDD (Event Dispatch Thread)

**Onde está (MainView.java):**
```java
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        LoginView view = new LoginView();
        new LoginController(view);
        view.setVisible(true);
    });
}
```

**Explicação simples:** É como um balconista de banco. Uma só pessoa atende todos (cliques, digitação, pintura da tela).

---

#### 🏗️ ENTRY POINT

**Onde está (MainView.java):**
```java
public class MainView {
    public static void main(String[] args) {  // ← ENTRY POINT
        SwingUtilities.invokeLater(() -> {
            LoginView view = new LoginView();
            new LoginController(view);
            view.setVisible(true);
        });
    }
}
```

---

## RESUMO RÁPIDO — ONDE ESTÁ CADA COISA

| Termo | Onde Está |
|-------|-----------|
| **JDBC** | `ConnectionUtil.getConnection()` |
| **DAO** | `com.sistema.dao/` (UsuarioDAO, ProdutoDAO...) |
| **DDL** | `ConnectionUtil.initDatabase()` (CREATE TABLE) |
| **DML** | `ProdutoDAO.inserir()` (INSERT), `atualizar()` (UPDATE), `excluir()` (DELETE) |
| **Transaction** | `MovimentacaoDAO.registrar()` (commit/rollback) |
| **Hash SHA-256** | `HashUtil.sha256()` |
| **Sessão** | `SessaoUsuario` (set, get, clear) |
| **Validações** | `LoginController`, `ProdutoController`, `MovimentacaoDAO` |
| **PreparedStatement** | Todos os métodos DAO |
| **LEFT JOIN** | `MovimentacaoDAO.listar()` |

---

*Documento gerado para avaliação — Sistema de Controle de Estoque em Java*
