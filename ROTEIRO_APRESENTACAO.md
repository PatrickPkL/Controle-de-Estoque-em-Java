# ROTEIRO DE APRESENTAÇÃO — SISTEMA DE CONTROLE DE ESTOQUE
## Avaliação (10 Pontos)

---

## PARTE 1 — ANÁLISE GERAL DO SISTEMA

### PONTO 1: Visão Geral do Sistema

**Título:** Sistema de Controle de Estoque — Java Desktop + MySQL

**Descrição:**
Sistema desktop para gestão de estoque com autenticação de usuários, CRUD de produtos, controle de categorias e registro de movimentações (entradas/saídas).

**Tecnologias utilizadas:**
- Java 11 (linguagem)
- Swing (interface gráfica — NetBeans GUI Builder)
- MySQL 8.0 (banco de dados)
- Maven (build e dependências)
- JDBC (conectividade)

**Pontos fortes demonstrados:**
- Arquitetura MVC organizada
- Persistência com DAO pattern
- Hash SHA-256 para senhas
- Transações atômicas em movimentações
- Auto-criação do banco na primeira conexão

---

### PONTO 2: Modelo Conceitual de Dados (MER)

**Título:** Modelo Entidade-Relacionamento

```
┌──────────────────────────┐       ┌──────────────────────────┐
│       USUÁRIO             │       │       CATEGORIA           │
├──────────────────────────┤       ├──────────────────────────┤
│ PK  id_usuario: INTEGER  │       │ PK  id_categoria: INT    │
│     login: VARCHAR(30)    │       │     nome: VARCHAR(50)     │
│     senha: VARCHAR(64)    │       │                           │
│     nome_completo:        │       └──────────────────────────┘
│          VARCHAR(100)     │                  │
└───────────┬──────────────┘                  │
            │  (1,N)                         │ (1,N)
            │                                │
            ▼                                ▼
┌──────────────────────────┐       ┌──────────────────────────┐
│        PRODUTO            │       │    MOVIMENTACAO           │
├──────────────────────────┤       ├──────────────────────────┤
│ PK  id_produto: INTEGER  │───────│ FK  id_produto: INTEGER  │
│     nome: VARCHAR(100)   │ (1,N) │ PK  id_mov: INTEGER      │
│     quantidade: INTEGER  │       │     tipo: ENUM            │
│     preco: DECIMAL(10,2) │       │       (ENTRADA, SAIDA)   │
│ FK  id_categoria: INT    │       │     quantidade: INTEGER   │
│ FK  id_usuario_cad: INT  │       │     data_mov: DATETIME    │
└──────────────────────────┘       │ FK  id_usuario_mov: INT   │
                                   └──────────────────────────┘
```

**Entidades e seus atributos:**

| Entidade | Descrição | Atributos Principais |
|----------|-----------|---------------------|
| **Usuario** | Pessoa que acessa o sistema | login, senha (hash), nome completo |
| **Categoria** | Classificação de produtos | nome (único) |
| **Produto** | Item em estoque | nome, quantidade, preço, categoria, quem cadastrou |
| **Movimentacao** | Registro de entrada ou saída | tipo, quantidade, data, quem registrou |

**Relacionamentos:**

| Relacionamento | Tipo | Descrição |
|---------------|------|-----------|
| Usuario → Produto | 1:N | Um usuário pode cadastrar vários produtos |
| Categoria → Produto | 1:N | Uma categoria classifica vários produtos |
| Produto → Movimentacao | 1:N | Um produto pode ter várias movimentações |
| Usuario → Movimentacao | 1:N | Um usuário pode registrar várias movimentações |

---

### PONTO 3: Modelo Lógico de Dados

**Título:** Estrutura tabular das tabelas e seus relacionamentos

```
TABELA: usuarios
┌────────────────────────────────────────────────────────────┐
│  id (PK)  │  login (UK)  │  senha  │  nome_completo       │
├───────────┼──────────────┼─────────┼──────────────────────┤
│  1        │  admin       │  xxxxx  │  Administrador       │
│  2        │  operador    │  xxxxx  │  Operador de Estoque  │
└───────────┴──────────────┴─────────┴──────────────────────┘

TABELA: categorias
┌────────────────────────────────────────────────────────────┐
│  id (PK)  │  nome (UK)                                     │
├───────────┼───────────────────────────────────────────────-┤
│  1        │  Eletrônicos                                   │
│  2        │  Escritório                                    │
│  3        │  Limpeza                                       │
│  4        │  Alimentos                                    │
│  5        │  Bebidas                                       │
│  6        │  Higiene                                       │
│  7        │  Roupas                                        │
│  8        │  Ferramentas                                   │
└───────────┴───────────────────────────────────────────────┘

TABELA: produtos
┌────────────────────────────────────────────────────────────┐
│  id (PK)  │  nome          │  qtd  │  preço     │ cat_fk │
├───────────┼────────────────┼───────┼───────────┼────────┤
│  1        │  Mouse Sem Fio  │  50   │  89.90     │   1    │
│  2        │  Teclado Mec.   │  30   │  249.90   │   1    │
│  ...      │  ...           │  ...  │  ...      │  ...   │
│  21       │  Multímetro     │  10   │  89.90    │   8    │
└───────────┴────────────────┴───────┴───────────┴────────┘
                              │
                              │ FK(id_categoria) → categorias(id)
                              │ FK(id_usuario_cad) → usuarios(id)

TABELA: movimentacoes
┌────────────────────────────────────────────────────────────┐
│  id (PK)  │  id_produto (FK)  │  tipo      │  qtd  │ data_mov              │
├───────────┼──────────────────┼────────────┼───────┼───────────────────────┤
│  1        │  1               │  ENTRADA   │  100  │  2026-04-01 08:00:00  │
│  2        │  2               │  ENTRADA   │   60  │  2026-04-01 08:05:00  │
│  ...      │  ...              │  ...       │  ...  │  ...                  │
│  140      │  19              │  SAIDA     │    6  │  2026-07-29 11:00:00  │
└───────────┴──────────────────┴────────────┴───────┴───────────────────────┘
                              │
                              │ FK(id_produto) → produtos(id)
                              │ FK(id_usuario_mov) → usuarios(id)
```

**Regras de integridade definidas:**
- `login` em `usuarios` é UNIQUE
- `nome` em `categorias` é UNIQUE
- `tipo` em `movimentacoes` só aceita ENTRADA ou SAIDA
- DELETE em `produtos` propaga para `movimentacoes` (ON DELETE CASCADE)
- Quantidade de produtos não pode ser negativa (validação na aplicação)

---

### PONTO 4: Modelo Físico de Dados

**Título:** Script SQL de criação do banco e tabelas

```sql
-- ==============================================================
-- CRIAÇÃO DO BANCO DE DADOS
-- ==============================================================
CREATE DATABASE IF NOT EXISTS sistema_estoque
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sistema_estoque;

-- ==============================================================
-- TABELA: categorias
-- ==============================================================
CREATE TABLE IF NOT EXISTS categorias (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nome        VARCHAR(50) NOT NULL UNIQUE
);

-- ==============================================================
-- TABELA: usuarios
-- ==============================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    login           VARCHAR(30) NOT NULL UNIQUE,
    senha           VARCHAR(64) NOT NULL,          -- SHA-256 = 64 hex chars
    nome_completo   VARCHAR(100)
);

-- ==============================================================
-- TABELA: produtos
-- ==============================================================
CREATE TABLE IF NOT EXISTS produtos (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nome            VARCHAR(100) NOT NULL,
    quantidade      INT NOT NULL DEFAULT 0,
    preco           DECIMAL(10,2) NOT NULL,
    id_categoria    INT,
    id_usuario_cad  INT,
    FOREIGN KEY (id_categoria)   REFERENCES categorias(id),
    FOREIGN KEY (id_usuario_cad) REFERENCES usuarios(id)
);

-- ==============================================================
-- TABELA: movimentacoes
-- ==============================================================
CREATE TABLE IF NOT EXISTS movimentacoes (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    id_produto      INT NOT NULL,
    tipo            ENUM('ENTRADA', 'SAIDA') NOT NULL,
    quantidade      INT NOT NULL,
    data_mov        DATETIME DEFAULT CURRENT_TIMESTAMP,
    id_usuario_mov  INT,
    FOREIGN KEY (id_produto)     REFERENCES produtos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario_mov) REFERENCES usuarios(id)
);
```

**Índices criados automaticamente:**
- PRIMARY KEY em todas as tabelas (acelera buscas por ID)
- UNIQUE em `usuarios.login` e `categorias.nome` (evita duplicatas)

**Dados de demonstração (mock data):**
- 2 usuários (admin / operador)
- 8 categorias
- 21 produtos
- 140 movimentações (abr-jul/2026)

---

### PONTO 5: Conexão com o Banco de Dados

**Título:** Como o sistema se conecta ao MySQL

**Fluxo de conexão:**

```
Aplicação Java
      │
      ▼
ConnectionUtil.java
  │
  ├── Lê db.properties (url, user, pass)
  │
  ├── Carrega driver JDBC: com.mysql.cj.jdbc.Driver
  │
  ├── Estabelece conexão via DriverManager
  │     jdbc:mysql://localhost:3306/sistema_estoque
  │
  └── Na PRIMEIRA conexão:
        └── initDatabase()
              ├── CREATE TABLE IF NOT EXISTS categorias
              ├── CREATE TABLE IF NOT EXISTS usuarios
              ├── CREATE TABLE IF NOT EXISTS produtos
              ├── CREATE TABLE IF NOT EXISTS movimentacoes
              └── INSERT IGNORE (dados mock)
```

**db.properties:**
```properties
db.url=jdbc:mysql://localhost:3306/sistema_estoque?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
db.user=root
db.pass=
```

**Código principal (ConnectionUtil.java):**
```java
public class ConnectionUtil {
    private static Connection conn;

    public static Connection getConnection() {
        if (conn == null || conn.isClosed()) {
            Properties props = new Properties();
            props.load(/* ler db.properties */);
            conn = DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.pass")
            );
            initDatabase(conn);  // cria tabelas na primeira vez
        }
        return conn;
    }
}
```

**Ponto importante:** O sistema cria o banco automaticamente na primeira conexão — não precisa criar manualmente!

---

## PARTE 2 — DESENVOLVIMENTO DO SISTEMA

### PONTO 6: Arquitetura MVC + DAO

**Título:** Estrutura de camadas

```
┌─────────────────────────────────────────────┐
│                  VIEW (Camada 1)             │
│  com.sistema.view                            │
│  ─ LoginView, CadastroUsuarioView,           │
│    TelaPrincipalView, MainView              │
└────────────────────┬────────────────────────┘
                     │ eventos + dados
                     ▼
┌─────────────────────────────────────────────┐
│              CONTROLLER (Camada 2)           │
│  com.sistema.controller                     │
│  ─ LoginController, CadastroUsuarioController│
│    ProdutoController                        │
│  Regras de negócio, validações              │
└────────────────────┬────────────────────────┘
                     │ objetos Model
                     ▼
┌─────────────────────────────────────────────┐
│               MODEL (Camada 3)               │
│  com.sistema.model                          │
│  ─ Usuario, Produto, Categoria, Movimentacao│
└────────────────────┬────────────────────────┘
                     │ operações CRUD
                     ▼
┌─────────────────────────────────────────────┐
│                 DAO (Camada 4)              │
│  com.sistema.dao                            │
│  ─ UsuarioDAO, ProdutoDAO,                  │
│    CategoriaDAO, MovimentacaoDAO            │
│  SQL, PreparedStatement, ResultSet          │
└────────────────────┬────────────────────────┘
                     │ JDBC
                     ▼
┌─────────────────────────────────────────────┐
│               UTIL (Camada 5)               │
│  com.sistema.util                           │
│  ─ ConnectionUtil (conexão MySQL)           │
│  ─ HashUtil (SHA-256)                       │
│  ─ SessaoUsuario (sessão logada)           │
└─────────────────────────────────────────────┘
```

---

### PONTO 7: Autenticação e Segurança

**Título:** Login com hash SHA-256

**Fluxo de login:**
```
1. Usuário digita: login + senha
2. Aplicação calcula: hash = SHA-256(senha digitada)
3. Busca no banco: SELECT * FROM usuarios WHERE login = ? AND senha = ?
4. Se encontrar → login válido → abre dashboard
5. Se não encontrar → erro "Login ou senha inválidos"
```

**Segurança implementada:**
- Senhas nunca armazenadas em texto puro
- SHA-256 gera hash de 64 caracteres hex
- Sessão do usuário em singleton (`SessaoUsuario`)
- Login único verificado antes do cadastro

---

### PONTO 8: CRUD de Produtos

**Título:** Operações completas sobre produtos

**Tela Principal — Aba PRODUTOS:**

| Operação | Descrição |
|---------|-----------|
| **CADASTRAR** | Nome + quantidade + preço + categoria |
| **BUSCAR** | Busca por nome (LIKE %%) |
| **ATUALIZAR** | Seleciona produto na tabela, edita, salva |
| **EXCLUIR** | Confirmação + DELETE com CASCADE |

**Validações:**
- Nome obrigatório
- Quantidade obrigatória (não negativa)
- Preço obrigatório (aceita vírgula como separador)
- Categoria obrigatória (combobox)

---

### PONTO 9: Movimentações de Estoque

**Título:** Entradas e Saídas com transação atômica

**Fluxo de uma movimentação:**
```
1. Selecionar produto na tabela
2. Informar quantidade
3. Clicar "Registrar Entrada" ou "Registrar Saída"
4. Sistema executa TRANSAÇÃO:

   BEGIN;
   INSERT INTO movimentacoes (id_produto, tipo, quantidade, ...)
   VALUES (?, ?, ?, ...);
   UPDATE produtos SET quantidade = quantidade +|- ? WHERE id = ?;
   COMMIT;

5. Se der erro → ROLLBACK (nada muda)
```

**Regras:**
- Saída não pode deixar estoque negativo
- Produto com estoque baixo (ex: Chave Philips = 3) alerta o usuário
- Histórico mostra todas as movimentações com JOIN

---

### PONTO 10: Demonstração Prática

**Título:** Como executar o sistema

**Pré-requisitos:**
- MySQL instalado (porta 3306)
- Java 11+

**Comandos:**
```bash
# Compilar
mvn clean compile

# Executar
mvn exec:java

# Gerar JAR
mvn package
```

**Credenciais de acesso:**
| Login | Senha | Perfil |
|-------|-------|--------|
| admin | admin | Administrador (pode tudo) |
| operador | 1234 | Operador de Estoque |

**Fluxo de uso:**
1. `LoginView` → autenticar
2. `TelaPrincipalView` com 3 abas:
   - **Produtos** → CRUD completo
   - **Categorias** → cadastro rápido
   - **Movimentações** → histórico + entradas/saídas

---

## PARTE 3 — TOPOLOGIA COMPLETA DA ARQUITETURA

### Topologia Geral do Sistema

```
╔══════════════════════════════════════════════════════════════════════════════════════╗
║                         SISTEMA DE CONTROLE DE ESTOQUE                              ║
║                          (Aplicação Desktop Java)                                   ║
╠══════════════════════════════════════════════════════════════════════════════════════╣
║                                                                                      ║
║   ┌─────────────────────────────────────────────────────────────────────────────┐    ║
║   │                          CAMADA 1 — VIEW (Apresentação)                     │    ║
║   │                          Pacote: com.sistema.view                            │    ║
║   │  ┌──────────────────┐  ┌────────────────────────┐  ┌────────────────────┐ │    ║
║   │  │    MainView      │  │     LoginView          │  │ CadastroUsuarioView│ │    ║
║   │  │                  │  │                        │  │                    │ │    ║
║   │  │ • Entry point    │  │ • Campo login          │  │ • Campo novo login │ │    ║
║   │  │ • Inicializa EDT │  │ • Campo senha          │  │ • Campo nova senha │ │    ║
║   │  │ • Lança LoginView│  │ • Botão "Entrar"       │  │ • Botão "Salvar"   │ │    ║
║   │  │                  │  │ • Botão "Cadastrar-se" │  │ • Botão "Voltar"   │ │    ║
║   │  └──────────────────┘  └────────────────────────┘  └────────────────────┘ │    ║
║   │  ┌──────────────────────────────────────────────────────────────────────┐  │    ║
║   │  │                     TelaPrincipalView                                │  │    ║
║   │  │  ┌────────────────────────────────────────────────────────────────┐  │  │    ║
║   │  │  │  JTabbedPane (3 abas)                                           │  │  │    ║
║   │  │  │                                                                │  │  │    ║
║   │  │  │  ┌─────────────────┐ ┌──────────────────┐ ┌────────────────────┐ │  │  │    ║
║   │  │  │  │  ABA 1:        │ │  ABA 2:          │ │  ABA 3:           │ │  │  │    ║
║   │  │  │  │  PRODUTOS      │ │  CATEGORIAS       │ │  MOVIMENTAÇÕES    │ │  │  │    ║
║   │  │  │  │                │ │                  │ │                   │ │  │  │    ║
║   │  │  │  │  • Busca texto │ │  • Tabela cats   │ │  • Histórico     │ │  │  │    ║
║   │  │  │  │  • JTable prods│ │  • Cadastro      │ │  • Qtd entrada   │ │  │  │    ║
║   │  │  │  │  • Form CRUD  │ │  • Botão "Add"   │ │  • Qtd saída     │ │  │  │    ║
║   │  │  │  │  • Botões     │ │                  │ │  • Botões        │ │  │  │    ║
║   │  │  │  │  Cad/Alt/Exc  │ │                  │ │  Entrada/Saída   │ │  │  │    ║
║   │  │  │  └─────────────────┘ └──────────────────┘ └────────────────────┘ │  │  │    ║
║   │  │  └────────────────────────────────────────────────────────────────┘  │  │    ║
║   │  └──────────────────────────────────────────────────────────────────────┘  │    ║
║   └─────────────────────────────────────────────────────────────────────────────┘    ║
║                                          │                                            ║
║                                          │ Eventos (clique, texto, seleção)           ║
║                                          ▼                                            ║
║   ┌─────────────────────────────────────────────────────────────────────────────┐    ║
║   │                      CAMADA 2 — CONTROLLER (Lógica de Negócio)              │    ║
║   │                      Pacote: com.sistema.controller                          │    ║
║   │  ┌──────────────────────┐  ┌──────────────────────────┐  ┌────────────────┐  │    ║
║   │  │  LoginController     │  │ CadastroUsuarioController │  │ ProdutoController│ │    ║
║   │  │                      │  │                          │  │                │  │    ║
║   │  │ • autenticar()       │  │ • cadastrar()            │  │ • buscarProdutos()│ │    ║
║   │  │ • abrirCadastro()    │  │ • voltarLogin()          │  │ • salvarProduto() │ │    ║
║   │  │ • abrirDashboard()   │  │                          │  │ • excluirProduto()│ │    ║
║   │  │                      │  │                          │  │ • registrarMov()  │ │    ║
║   │  │                      │  │                          │  │ • cadastrarCat()  │ │    ║
║   │  └──────────────────────┘  └──────────────────────────┘  └────────────────┘  │    ║
║   │                                                                              │    ║
║   │  VALIDAÇÕES:                                                                 │    ║
║   │  • Campos obrigatórios (não vazios)                                          │    ║
║   │  • Formato de preço (vírgula → ponto)                                        │    ║
║   │  • Quantidade para saída ≤ estoque atual                                     │    ║
║   │  • Login único no cadastro                                                   │    ║
║   └─────────────────────────────────────────────────────────────────────────────┘    ║
║                                          │                                            ║
║                                          │ Objetos Model (Usuario, Produto, etc.)    ║
║                                          ▼                                            ║
║   ┌─────────────────────────────────────────────────────────────────────────────┐    ║
║   │                         CAMADA 3 — MODEL (Entidades)                         │    ║
║   │                         Pacote: com.sistema.model                            │    ║
║   │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌────────────────┐   │    ║
║   │  │   Usuario    │  │   Produto     │  │  Categoria   │  │ Movimentacao  │   │    ║
║   │  ├──────────────┤  ├──────────────┤  ├──────────────┤  ├────────────────┤   │    ║
║   │  │ Integer id   │  │ Integer id    │  │ Integer id   │  │ Integer id     │   │    ║
║   │  │ String login │  │ String nome   │  │ String nome  │  │ Produto prod   │   │    ║
║   │  │ String senha │  │ Integer qtd   │  │              │  │ Tipo tipo      │   │    ║
║   │  │ String nome  │  │ BigDecimal pre│  │              │  │ Integer qtd    │   │    ║
║   │  │              │  │ Categoria cat │  │              │  │ LocalDateTime  │   │    ║
║   │  │              │  │ Usuario cad   │  │              │  │   dataMov      │   │    ║
║   │  │              │  │              │  │              │  │ Usuario mov    │   │    ║
║   │  └──────────────┘  └──────────────┘  └──────────────┘  └────────────────┘   │    ║
║   │                                                                              │    ║
║   │  Enum: TipoMovimentacao → ENTRADA | SAIDA                                    │    ║
║   └─────────────────────────────────────────────────────────────────────────────┘    ║
║                                          │                                            ║
║                                          │ Operações CRUD (insert, update, delete)   ║
║                                          ▼                                            ║
║   ┌─────────────────────────────────────────────────────────────────────────────┐    ║
║   │                       CAMADA 4 — DAO (Persistência)                         │    ║
║   │                       Pacote: com.sistema.dao                                │    ║
║   │  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────────────┐  │    ║
║   │  │   UsuarioDAO     │  │   ProdutoDAO     │  │   MovimentacaoDAO        │  │    ║
║   │  │                  │  │                  │  │                          │  │    ║
║   │  │ • inserir()      │  │ • inserir()      │  │ • registrar()            │  │    ║
║   │  │ • autenticar()   │  │ • atualizar()    │  │   (transação atômica)    │  │    ║
║   │  │ • buscarPorLogin │  │ • excluir()       │  │ • listarPorProduto()     │  │    ║
║   │  │                  │  │ • listar()       │  │ • listar()               │  │    ║
║   │  │                  │  │ • buscarPorNome  │  │                          │  │    ║
║   │  └──────────────────┘  └──────────────────┘  └──────────────────────────┘  │    ║
║   │  ┌──────────────────┐                                                         │    ║
║   │  │  CategoriaDAO    │                                                         │    ║
║   │  │                  │                                                         │    ║
║   │  │ • inserir()      │                                                         │    ║
║   │  │ • listar()       │                                                         │    ║
║   │  │ • buscarPorId    │                                                         │    ║
║   │  └──────────────────┘                                                         │    ║
║   └─────────────────────────────────────────────────────────────────────────────┘    ║
║                                          │                                            ║
║                                          │ SQL / JDBC                                ║
║                                          ▼                                            ║
║   ┌─────────────────────────────────────────────────────────────────────────────┐    ║
║   │                        CAMADA 5 — UTIL (Utilitários)                        │    ║
║   │                        Pacote: com.sistema.util                             │    ║
║   │  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────────────┐  │    ║
║   │  │ ConnectionUtil   │  │    HashUtil       │  │    SessaoUsuario          │  │    ║
║   │  │                  │  │                  │  │                          │  │    ║
║   │  │ • getConnection()│  │ • sha256(String) │  │ • set(Usuario)           │  │    ║
║   │  │ • initDatabase() │  │                  │  │ • get() → Usuario        │  │    ║
║   │  │ • close()        │  │                  │  │ • clear() (logout)       │  │    ║
║   │  │                  │  │                  │  │ • isLogado()              │  │    ║
║   │  │ Singleton conn   │  │ SHA-256 hex 64   │  │ Singleton estático       │  │    ║
║   │  └──────────────────┘  └──────────────────┘  └──────────────────────────┘  │    ║
║   └─────────────────────────────────────────────────────────────────────────────┘    ║
║                                          │                                            ║
║   ┌──────────────────────────────────────┼──────────────────────────────────────┐    ║
║   │                                      │                                        │    ║
║   │                                      ▼                                        │    ║
║   │  ┌──────────────────────────────────────────────────────────────────────┐    │    ║
║   │  │                        MySQL DATABASE                               │    │    ║
║   │  │                    Base: sistema_estoque                             │    │    ║
║   │  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌───────────┐  │    │    ║
║   │  │  │  categorias  │  │   usuarios   │  │   produtos   │  │movimentac.│  │    │    ║
║   │  │  └──────────────┘  └──────────────┘  └──────────────┘  └───────────┘  │    │    ║
║   │  └──────────────────────────────────────────────────────────────────────┘    │    ║
║   └─────────────────────────────────────────────────────────────────────────────┘    ║
╚══════════════════════════════════════════════════════════════════════════════════════╝
```

---

### Módulos e Componentes

| Módulo | Pacote | Responsabilidade | Classes/Arquivos |
|--------|--------|-----------------|------------------|
| **View** | `com.sistema.view` | Interface gráfica Swing | `MainView`, `LoginView`, `CadastroUsuarioView`, `TelaPrincipalView` |
| **Controller** | `com.sistema.controller` | Lógica de negócio e validações | `LoginController`, `CadastroUsuarioController`, `ProdutoController` |
| **Model** | `com.sistema.model` | Entidades de domínio | `Usuario`, `Produto`, `Categoria`, `Movimentacao` |
| **DAO** | `com.sistema.dao` | Persistência em banco | `UsuarioDAO`, `ProdutoDAO`, `CategoriaDAO`, `MovimentacaoDAO` |
| **Util** | `com.sistema.util` | Utilitários de infraestrutura | `ConnectionUtil`, `HashUtil`, `SessaoUsuario` |

---

### Fluxo de Dados Entre Camadas

```
USUÁRIO (Interface)
    │
    │ 1. Interage com View (Swing)
    ▼
VIEW — componentes visuais (JTextField, JButton, JTable)
    │
    │ 2. Eventos chamam métodos do Controller
    ▼
CONTROLLER — validações + regras de negócio (RN-01 a RN-10)
    │
    │ 3. Objetos Model passam para DAO
    ▼
DAO — operações SQL (INSERT, SELECT, UPDATE, DELETE)
    │
    │ 4. JDBC executa no banco
    ▼
DATABASE — MySQL (persistência)
    │
    │ 5. ResultSet retorna pelo caminho inverso
    ▼
VIEW — atualiza componentes (JTable, mensagens)
    │
    ▼
USUÁRIO (feedback visual)
```

---

## RESUMO DOS 10 PONTOS

| # | Ponto | Descrição |
|---|-------|-----------|
| 1 | Visão Geral | Sistema desktop Java + MySQL |
| 2 | MER | 4 entidades: Usuario, Categoria, Produto, Movimentacao |
| 3 | Modelo Lógico | Tabelas com tipos, chaves, relacionamentos |
| 4 | Modelo Físico | CREATE TABLE + índices + mock data |
| 5 | Conexão BD | ConnectionUtil + auto-init do banco |
| 6 | Arquitetura | MVC + DAO pattern |
| 7 | Autenticação | SHA-256, login único, sessão |
| 8 | CRUD Produtos | Cadastro, busca, atualização, exclusão |
| 9 | Movimentações | Transação atômica entrada/saída |
| 10 | Demo | Execução prática + credenciais |

---

*Documento gerado para avaliação — Sistema de Controle de Estoque em Java*