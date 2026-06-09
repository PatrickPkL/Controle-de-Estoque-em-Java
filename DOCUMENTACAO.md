# Documentação do Sistema de Controle de Estoque

## Sumário

1. [Visão Geral](#1-visão-geral)
2. [Topologia da Arquitetura](#2-topologia-da-arquitetura)
3. [Diagrama de Camadas](#3-diagrama-de-camadas)
4. [Modelo de Dados (MER)](#4-modelo-de-dados-mer)
5. [Camada Model — Entidades](#5-camada-model--entidades)
6. [Camada DAO — Persistência](#6-camada-dao--persistência)
7. [Camada Controller — Regras de Negócio](#7-camada-controller--regras-de-negócio)
8. [Camada View — Interface Gráfica](#8-camada-view--interface-gráfica)
9. [Camada Util — Utilitários](#9-camada-util--utilitários)
10. [Regras de Negócio](#10-regras-de-negócio)
11. [Script SQL (init.sql)](#11-script-sql-initsql)
12. [Configuração do Projeto (pom.xml / db.properties)](#12-configuração-do-projeto)
13. [Fluxo de Navegação da Aplicação](#13-fluxo-de-navegação-da-aplicação)
14. [Glossário](#14-glossário)

---

## 1. Visão Geral

**Sistema de Controle de Estoque** é uma aplicação desktop Java com interface gráfica Swing e banco de dados MySQL. Permite:

- Cadastro e autenticação de usuários
- CRUD de produtos (criar, ler, atualizar, excluir)
- Gerenciamento de categorias
- Registro de movimentações de entrada e saída
- Consulta de histórico de movimentações
- Controle de estoque em tempo real

### Tecnologias

| Componente      | Tecnologia                          |
|-----------------|-------------------------------------|
| Linguagem       | Java 11                             |
| Interface       | Java Swing (NetBeans GUI Builder)   |
| Banco           | MySQL 8.0                           |
| Conector JDBC   | mysql-connector-j 8.0.33            |
| Build           | Apache Maven                        |
| Gerenciamento   | Maven (pom.xml)                     |

---

## 2. Topologia da Arquitetura

A aplicação segue o padrão **MVC (Model-View-Controller)** com uma camada adicional de persistência (DAO).

```
┌─────────────────────────────────────────────────────────────────┐
│                     CONTROLE DE ESTOQUE                         │
│                       (Aplicação Desktop)                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────┐    ┌──────────────┐    ┌──────────────────────┐  │
│  │   VIEW   │    │  CONTROLLER  │    │   MODEL (Entidades)  │  │
│  │ (Swing)  │◄──►│  (Lógica)    │◄──►│                      │  │
│  │          │    │              │    │  • Usuario           │  │
│  │ MainView │    │ LoginCont.   │    │  • Produto           │  │
│  │ LoginView│    │ CadastroCont │    │  • Categoria         │  │
│  │ Cadastro │    │ ProdutoCont. │    │  • Movimentacao      │  │
│  │ TelaPrinc│    │              │    │                      │  │
│  └──────────┘    └──────┬───────┘    └──────────────────────┘  │
│                         │                                       │
│                         ▼                                       │
│                 ┌───────────────┐                               │
│                 │  DAO (Dados)  │                               │
│                 │               │                               │
│                 │ UsuarioDAO    │                               │
│                 │ ProdutoDAO    │                               │
│                 │ CategoriaDAO  │                               │
│                 │ Movimentacao  │                               │
│                 └───────┬───────┘                               │
│                         │                                       │
│                         ▼                                       │
│                 ┌───────────────┐                               │
│                 │  Connection   │                               │
│                 │    Util       │                               │
│                 └───────┬───────┘                               │
│                         │                                       │
├─────────────────────────┼───────────────────────────────────────┤
│                         ▼                                       │
│                 ┌───────────────┐                               │
│                 │    MySQL DB   │                               │
│                 │sistema_estoque│                               │
│                 └───────────────┘                               │
└─────────────────────────────────────────────────────────────────┘
```

---

## 3. Diagrama de Camadas

```
┌───────────────────────────────────────────────────────┐
│                    VIEW (camada 1)                     │
│  com.sistema.view                                      │
│  ┌──────────────────────────────────────────────────┐  │
│  │ MainView (entry point)                           │  │
│  │ LoginView (tela de login)                        │  │
│  │ CadastroUsuarioView (tela de cadastro)           │  │
│  │ TelaPrincipalView (dashboard principal)          │  │
│  └──────────────────────────────────────────────────┘  │
├───────────────────────────────────────────────────────┤
│                  CONTROLLER (camada 2)                 │
│  com.sistema.controller                                │
│  ┌──────────────────────────────────────────────────┐  │
│  │ LoginController         → autenticação           │  │
│  │ CadastroUsuarioController → cadastro de usuários │  │
│  │ ProdutoController        → CRUD + movimentações  │  │
│  └──────────────────────────────────────────────────┘  │
├───────────────────────────────────────────────────────┤
│                  MODEL (camada 3)                      │
│  com.sistema.model                                     │
│  ┌──────────────────────────────────────────────────┐  │
│  │ Usuario       → dados de usuário                 │  │
│  │ Produto       → dados de produto + estoque       │  │
│  │ Categoria     → categorias para classificação    │  │
│  │ Movimentacao  → registro de entrada/saída        │  │
│  └──────────────────────────────────────────────────┘  │
├───────────────────────────────────────────────────────┤
│                    DAO (camada 4)                      │
│  com.sistema.dao                                       │
│  ┌──────────────────────────────────────────────────┐  │
│  │ UsuarioDAO      → insert, buscar, autenticar     │  │
│  │ ProdutoDAO      → CRUD completo                  │  │
│  │ CategoriaDAO    → listar, buscar, inserir        │  │
│  │ MovimentacaoDAO → registrar, listar histórico    │  │
│  └──────────────────────────────────────────────────┘  │
├───────────────────────────────────────────────────────┤
│                  UTIL (camada 5)                       │
│  com.sistema.util                                      │
│  ┌──────────────────────────────────────────────────┐  │
│  │ ConnectionUtil → conexão MySQL + auto-init DB    │  │
│  │ HashUtil       → SHA-256 para senhas             │  │
│  │ SessaoUsuario  → sessão do usuário logado        │  │
│  └──────────────────────────────────────────────────┘  │
├───────────────────────────────────────────────────────┤
│               CONFIGURAÇÃO (resources)                │
│  ┌──────────────────────────────────────────────────┐  │
│  │ pom.xml          → dependências Maven            │  │
│  │ db.properties    → URL, user, pass do MySQL      │  │
│  │ init.sql         → script SQL para criar o banco │  │
│  └──────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────┘
```

---

## 4. Modelo de Dados (MER)

### Diagrama Entidade-Relacionamento

```
┌───────────────────────┐
│      categorias       │
├───────────────────────┤
│ PK id: INT (auto)     │────┐
│  nome: VARCHAR(50)    │    │   (1:N)
│    UNIQUE             │    │
└───────────────────────┘    │
                             │
┌───────────────────────┐    │
│       usuarios        │    │
├───────────────────────┤    │
│ PK id: INT (auto)     │────┤
│  login: VARCHAR(30)   │    │   (1:N)
│    UNIQUE             │    │
│  senha: VARCHAR(64)   │    │
│  nome_completo:       │    │
│    VARCHAR(100)       │    │
└───────────────────────┘    │
                             │
┌───────────────────────┐    │
│       produtos        │    │
├───────────────────────┤    │
│ PK id: INT (auto)     │    │
│  nome: VARCHAR(100)   │    │
│  quantidade: INT      │    │
│  preco: DECIMAL(10,2) │    │
│ FK id_categoria ──────┘    │
│ FK id_usuario_cad ────────┘
└───────────────────────┘
        │
        │ (1:N)
        ▼
┌───────────────────────┐
│    movimentacoes      │
├───────────────────────┤
│ PK id: INT (auto)     │
│ FK id_produto: INT    │────┘
│  tipo: ENUM(ENTRADA,  │
│         SAIDA)        │
│  quantidade: INT      │
│  data_mov: DATETIME   │
│ FK id_usuario_mov: INT────┘
└───────────────────────┘
```

### Dicionário de Dados

| Tabela         | Campo             | Tipo           | Restrições                     | Descrição                           |
|----------------|-------------------|----------------|--------------------------------|-------------------------------------|
| categorias     | id                | INT            | PK, AUTO_INCREMENT             | ID único da categoria               |
| categorias     | nome              | VARCHAR(50)    | NOT NULL, UNIQUE               | Nome da categoria                   |
| usuarios       | id                | INT            | PK, AUTO_INCREMENT             | ID único do usuário                 |
| usuarios       | login             | VARCHAR(30)    | NOT NULL, UNIQUE               | Nome de login                       |
| usuarios       | senha             | VARCHAR(64)    | NOT NULL                       | Hash SHA-256 da senha               |
| usuarios       | nome_completo     | VARCHAR(100)   |                                | Nome completo para exibição         |
| produtos       | id                | INT            | PK, AUTO_INCREMENT             | ID único do produto                 |
| produtos       | nome              | VARCHAR(100)   | NOT NULL                       | Nome do produto                     |
| produtos       | quantidade        | INT            | NOT NULL, DEFAULT 0            | Quantidade em estoque               |
| produtos       | preco             | DECIMAL(10,2)  | NOT NULL                       | Preço unitário                      |
| produtos       | id_categoria      | INT            | FK → categorias(id)            | Categoria do produto                |
| produtos       | id_usuario_cad    | INT            | FK → usuarios(id)              | Usuário que cadastrou               |
| movimentacoes  | id                | INT            | PK, AUTO_INCREMENT             | ID único da movimentação            |
| movimentacoes  | id_produto        | INT            | NOT NULL, FK → produtos(id)    | Produto movimentado                 |
| movimentacoes  | tipo              | ENUM          | NOT NULL (ENTRADA, SAIDA)      | Tipo de movimento                   |
| movimentacoes  | quantidade        | INT            | NOT NULL                       | Quantidade movimentada              |
| movimentacoes  | data_mov          | DATETIME       | DEFAULT CURRENT_TIMESTAMP      | Data/hora do movimento              |
| movimentacoes  | id_usuario_mov    | INT            | FK → usuarios(id)              | Usuário que registrou               |

---

## 5. Camada Model — Entidades

### 5.1 Usuario

**Arquivo:** `src/main/java/com/sistema/model/Usuario.java`

Representa um usuário do sistema.

| Atributo      | Tipo        | Descrição                              |
|---------------|-------------|----------------------------------------|
| id            | Integer     | Identificador único (auto-incremento)  |
| login         | String      | Nome de login (único no sistema)       |
| senha         | String      | Hash SHA-256 da senha (64 caracteres)  |
| nomeCompleto  | String      | Nome completo para exibição            |

**Construtores:**
- `Usuario()` — construtor padrão
- `Usuario(Integer id, String login, String senha, String nomeCompleto)` — construtor completo

**Métodos:**
- Getters e setters para todos os atributos
- `toString()` → retorna `nomeCompleto` ou `login`
- `equals()` / `hashCode()` → baseados no campo `id`

---

### 5.2 Produto

**Arquivo:** `src/main/java/com/sistema/model/Produto.java`

Representa um produto no estoque.

| Atributo    | Tipo        | Descrição                                 |
|-------------|-------------|-------------------------------------------|
| id          | Integer     | Identificador único                       |
| nome        | String      | Nome do produto                           |
| quantidade  | Integer     | Quantidade atual em estoque               |
| preco       | BigDecimal  | Preço unitário (precisão decimal)         |
| categoria   | Categoria   | Objeto Categoria (relacionamento)         |
| usuarioCad  | Usuario     | Usuário que cadastrou (relacionamento)    |

**Construtores:**
- `Produto()` — construtor padrão
- `Produto(Integer id, String nome, Integer quantidade, BigDecimal preco, Categoria categoria, Usuario usuarioCad)` — completo

**Métodos:**
- Getters e setters para todos os atributos
- `toString()` → retorna o nome
- `equals()` / `hashCode()` → baseados no campo `id`

---

### 5.3 Categoria

**Arquivo:** `src/main/java/com/sistema/model/Categoria.java`

Representa uma categoria para classificar produtos.

| Atributo | Tipo    | Descrição                  |
|----------|---------|----------------------------|
| id       | Integer | Identificador único        |
| nome     | String  | Nome da categoria (único)  |

**Construtores:**
- `Categoria()` — construtor padrão
- `Categoria(Integer id, String nome)` — completo

**Métodos:**
- Getters e setters
- `toString()` → retorna o nome
- `equals()` / `hashCode()` → baseados no campo `id`

---

### 5.4 Movimentacao

**Arquivo:** `src/main/java/com/sistema/model/Movimentacao.java`

Representa uma movimentação de entrada ou saída de estoque.

| Atributo    | Tipo                         | Descrição                              |
|-------------|------------------------------|----------------------------------------|
| id          | Integer                      | Identificador único                    |
| produto     | Produto                      | Produto movimentado                    |
| tipo        | TipoMovimentacao (enum)      | ENTRADA ou SAIDA                       |
| quantidade  | Integer                      | Quantidade movimentada                 |
| dataMov     | LocalDateTime                | Data/hora do registro                  |
| usuarioMov  | Usuario                      | Usuário que registrou                  |

**Enum:**
- `TipoMovimentacao.ENTRADA` — adiciona ao estoque
- `TipoMovimentacao.SAIDA` — remove do estoque

**Construtores:**
- `Movimentacao()` — construtor padrão
- `Movimentacao(...)` — completo

**Métodos:**
- Getters e setters para todos os atributos
- `equals()` / `hashCode()` → baseados no campo `id`

---

## 6. Camada DAO — Persistência

### 6.1 UsuarioDAO

**Arquivo:** `src/main/java/com/sistema/dao/UsuarioDAO.java`

Operações com a tabela `usuarios`.

| Método             | Descrição                                               |
|--------------------|---------------------------------------------------------|
| `inserir(Usuario)` | Insere um novo usuário no banco.                        |
| `buscarPorLogin(String)` | Busca usuário pelo login. Retorna `Optional<Usuario>`. |
| `autenticar(String, String)` | Valida login + senha hash. Retorna `Optional<Usuario>`. |

---

### 6.2 ProdutoDAO

**Arquivo:** `src/main/java/com/sistema/dao/ProdutoDAO.java`

Operações com a tabela `produtos`. As consultas usam JOIN com `categorias` e `usuarios`.

| Método                   | Descrição                                      |
|--------------------------|------------------------------------------------|
| `inserir(Produto)`       | Insere um novo produto.                        |
| `atualizar(Produto)`     | Atualiza nome, quantidade, preço e categoria.  |
| `excluir(int id)`        | Exclui um produto pelo ID.                     |
| `listar()`               | Retorna todos os produtos (ordenados por nome).|
| `buscarPorNome(String)`  | Busca produtos por nome parcial (LIKE %%).     |

---

### 6.3 CategoriaDAO

**Arquivo:** `src/main/java/com/sistema/dao/CategoriaDAO.java`

Operações com a tabela `categorias`.

| Método                   | Descrição                                   |
|--------------------------|---------------------------------------------|
| `listar()`               | Retorna todas as categorias ordenadas.      |
| `buscarPorId(int)`       | Busca categoria por ID.                     |
| `inserir(Categoria)`     | Insere nova categoria (nome único).         |

---

### 6.4 MovimentacaoDAO

**Arquivo:** `src/main/java/com/sistema/dao/MovimentacaoDAO.java`

Operações com a tabela `movimentacoes`.

| Método                        | Descrição                                              |
|-------------------------------|--------------------------------------------------------|
| `registrar(Movimentacao)`     | Registra movimentação + atualiza estoque (transação).  |
| `listarPorProduto(int)`       | Histórico de movimentações de um produto específico.   |
| `listar()`                    | Histórico completo de movimentações (LEFT JOIN).       |

---

## 7. Camada Controller — Regras de Negócio

### 7.1 LoginController

**Arquivo:** `src/main/java/com/sistema/controller/LoginController.java`

Controla a tela de login.

| Método            | Descrição                                                    |
|-------------------|--------------------------------------------------------------|
| `autenticar()`    | Valida campos, gera hash SHA-256, busca no DAO, inicia sessão |
| `abrirCadastro()` | Navega para a tela de cadastro de novo usuário               |
| `abrirDashboard()`| Abre a tela principal após login bem-sucedido                |

**Regras de negócio aplicadas:**
- Login e senha são obrigatórios
- Senha é hasheada com SHA-256 antes de comparar com o banco
- Usuário logado é armazenado em `SessaoUsuario` para uso nas demais operações

---

### 7.2 CadastroUsuarioController

**Arquivo:** `src/main/java/com/sistema/controller/CadastroUsuarioController.java`

Controla a tela de cadastro de novos usuários.

| Método          | Descrição                                                     |
|-----------------|---------------------------------------------------------------|
| `cadastrar()`   | Valida campos, verifica duplicidade, hasheia senha, insere   |
| `voltarLogin()` | Retorna à tela de login                                       |

**Regras de negócio aplicadas:**
- Login e senha são obrigatórios
- Login deve ser único no sistema (verificação antes de inserir)
- Senha é armazenada como hash SHA-256
- O campo `nomeCompleto` recebe o mesmo valor do `login` no cadastro simplificado

---

### 7.3 ProdutoController

**Arquivo:** `src/main/java/com/sistema/controller/ProdutoController.java`

Controlador principal do dashboard. Gerencia todas as operações de produto, categoria e movimentação.

| Método                          | Descrição                                         |
|---------------------------------|---------------------------------------------------|
| `buscarProdutos()`              | Dispara busca textual na tabela de produtos       |
| `salvarProduto(boolean novo)`   | Cadastra ou atualiza produto                      |
| `excluirProduto()`              | Exclui produto selecionado (com confirmação)      |
| `registrarMovimentacao(Tipo)`   | Registra entrada ou saída de estoque              |
| `cadastrarCategoria()`          | Cadastra nova categoria                           |

**Regras de negócio aplicadas no CRUD:**
- Nome, quantidade e preço são obrigatórios
- Preço aceita vírgula como separador decimal (convertido para ponto)
- Categoria deve ser selecionada no combobox
- Para atualizar, um produto deve estar selecionado na tabela
- Exclusão requer confirmação do usuário

---

## 8. Camada View — Interface Gráfica

### 8.1 MainView

**Arquivo:** `src/main/java/com/sistema/view/MainView.java`

Classe de entrada da aplicação. Inicializa a `LoginView` no Event Dispatch Thread do Swing e a conecta ao `LoginController`.

**Método principal:**
- `main(String[] args)` → ponto de entrada da aplicação

---

### 8.2 LoginView

**Arquivo:** `src/main/java/com/sistema/view/LoginView.java`

Tela de login com campos de usuário e senha e botões "Entrar" e "Cadastrar-se".

**Métodos ponte (para acesso do Controller):**

| Método              | Retorno          | Descrição                    |
|---------------------|------------------|------------------------------|
| `getLogin()`        | String           | Texto do campo de login      |
| `getSenha()`        | String           | Senha digitada               |
| `getBtnEntrar()`    | JButton          | Botão "Entrar"               |
| `getBtnCriarConta()`| JButton          | Botão "Cadastrar-se"         |
| `limparCampos()`    | void             | Limpa campos de login/senha  |

---

### 8.3 CadastroUsuarioView

**Arquivo:** `src/main/java/com/sistema/view/CadastroUsuarioView.java`

Tela de cadastro com campos de novo login e senha, botões "Salvar Cadastro" e "Voltar".

**Métodos ponte:**

| Método                 | Retorno          | Descrição                         |
|------------------------|------------------|-----------------------------------|
| `getNovoLogin()`       | String           | Texto do campo de novo login      |
| `getNovaSenha()`       | String           | Nova senha digitada               |
| `getBtnSalvarCadastro()`| JButton         | Botão "Salvar Cadastro"           |
| `getBtnVoltar()`       | JButton          | Botão "Voltar para Login"         |
| `limparCampos()`       | void             | Limpa campos                      |

---

### 8.4 TelaPrincipalView

**Arquivo:** `src/main/java/com/sistema/view/TelaPrincipalView.java`

Dashboard principal com três abas (JTabbedPane):

1. **Produtos** — busca, tabela de produtos, formulário CRUD
2. **Categorias** — tabela de categorias + cadastro rápido
3. **Movimentações** — histórico geral + botões de entrada/saída

A aba de movimentações possui um painel inferior com campo de quantidade e botões "Registrar Entrada" e "Registrar Saída" adicionados manualmente (código extra na linha 395-406).

**Métodos ponte por aba:**

| Método                      | Retorno   | Descrição                         |
|-----------------------------|-----------|-----------------------------------|
| `getTxtBusca()`             | String    | Campo de busca por nome           |
| `getBtnBuscar()`            | JButton   | Botão "Buscar"                    |
| `getTableProdutos()`        | JTable    | Tabela de produtos                |
| `getTxtNome()`              | String    | Campo nome do produto             |
| `getTxtQuantidade()`        | String    | Campo quantidade                  |
| `getTxtPreco()`             | String    | Campo preço                       |
| `getCbCategoria()`          | JComboBox | Combo de categorias               |
| `getBtnCadastrar()`         | JButton   | Botão "Cadastrar"                 |
| `getBtnAtualizar()`         | JButton   | Botão "Atualizar"                 |
| `getBtnExcluir()`           | JButton   | Botão "Excluir"                   |
| `limparCamposProduto()`     | void      | Limpa formulário de produto       |
| `getTableCategorias()`      | JTable    | Tabela de categorias              |
| `getTxtNomeCategoria()`     | String    | Campo nome da categoria           |
| `getBtnCadastrarCategoria()`| JButton   | Botão cadastrar categoria         |
| `getTableMovimentacoes()`   | JTable    | Tabela de histórico               |
| `getBtnEntrada()`           | JButton   | Botão "Registrar Entrada"         |
| `getBtnSaida()`             | JButton   | Botão "Registrar Saída"           |
| `getTxtQtdMov()`            | String    | Campo quantidade movimentação     |
| `limparCampoMovimentacao()` | void      | Limpa campo de movimentação       |

---

## 9. Camada Util — Utilitários

### 9.1 ConnectionUtil

**Arquivo:** `src/main/java/com/sistema/util/ConnectionUtil.java`

Gerencia a conexão com o banco de dados MySQL.

| Método                       | Descrição                                                    |
|------------------------------|--------------------------------------------------------------|
| `getConnection()`            | Retorna conexão singleton. Cria se não existir ou estiver fechada. |
| `initDatabase(Connection)`   | Cria tabelas e insere dados iniciais (admin + categorias).   |
| `close()`                    | Fecha a conexão com o banco.                                 |

**Funcionamento:**
- Lê configurações do arquivo `db.properties` no classpath
- Carrega o driver JDBC `com.mysql.cj.jdbc.Driver`
- Conecta ao MySQL com URL, usuário e senha do properties
- Na primeira conexão, executa `initDatabase` que cria as 4 tabelas (se não existirem) e insere dados seed

---

### 9.2 HashUtil

**Arquivo:** `src/main/java/com/sistema/util/HashUtil.java`

Utilitário para hash de senhas com SHA-256.

| Método            | Descrição                                        |
|-------------------|--------------------------------------------------|
| `sha256(String)`  | Retorna o hash SHA-256 em hexadecimal (64 chars) |

---

### 9.3 SessaoUsuario

**Arquivo:** `src/main/java/com/sistema/util/SessaoUsuario.java`

Mantém o usuário autenticado durante a sessão (singleton estático).

| Método       | Descrição                                |
|--------------|------------------------------------------|
| `set(Usuario)| Define o usuário logado                 |
| `get()`      | Retorna o usuário logado                 |
| `clear()`    | Remove o usuário da sessão (logout)      |
| `isLogado()` | Verifica se há um usuário logado         |

---

## 10. Regras de Negócio

### RN-01 — Autenticação Obrigatória
Para acessar o sistema, o usuário deve fornecer login e senha válidos. A senha é verificada através de hash SHA-256.

### RN-02 — Senha Segura
Nenhuma senha é armazenada em texto puro. O sistema gera o hash SHA-256 antes de qualquer operação de escrita ou leitura no banco.

### RN-03 — Login Único
Cada login deve ser único no sistema. O cadastro de um novo usuário verifica duplicidade antes de inserir.

### RN-04 — Transação em Movimentações
O registro de uma movimentação e a atualização do estoque são feitos em uma única transação atômica. Se qualquer parte falhar, um rollback é executado, garantindo a consistência dos dados.

### RN-05 — Estoque Atualizado Automaticamente
- **Entrada:** a quantidade do produto é incrementada (`quantidade + qtd`)
- **Saída:** a quantidade do produto é decrementada (`quantidade - qtd`)

### RN-06 — Categoria Obrigatória para Produtos
Todo produto deve pertencer a uma categoria. O sistema exige seleção no combobox antes de cadastrar ou atualizar.

### RN-07 — Confirmação de Exclusão
A exclusão de um produto requer confirmação explícita do usuário via diálogo de confirmação.

### RN-08 — Histórico com LEFT JOIN
As consultas de movimentações usam LEFT JOIN para garantir que o histórico permaneça visível mesmo se o produto ou usuário associado forem removidos futuramente.

### RN-09 — Auto-Init do Banco
Na primeira conexão, o sistema cria automaticamente as tabelas necessárias e insere dados iniciais (admin:admin e 3 categorias padrão).

### RN-10 — Usuário Admin Padrão
O sistema já vem com um usuário administrador pré-cadastrado:
- Login: `admin`
- Senha: `admin`
- O hash armazenado é: `8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918`

---

## 11. Script SQL (init.sql)

**Arquivo:** `src/main/resources/init.sql` (independente) e lógica embutida em `ConnectionUtil.java`

### Criação do Banco

```sql
CREATE DATABASE IF NOT EXISTS sistema_estoque
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sistema_estoque;
```

### Tabelas

```sql
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

CREATE TABLE IF NOT EXISTS produtos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    quantidade INT NOT NULL DEFAULT 0,
    preco DECIMAL(10,2) NOT NULL,
    id_categoria INT,
    id_usuario_cad INT,
    FOREIGN KEY (id_categoria) REFERENCES categorias(id),
    FOREIGN KEY (id_usuario_cad) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS movimentacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_produto INT NOT NULL,
    tipo ENUM('ENTRADA','SAIDA') NOT NULL,
    quantidade INT NOT NULL,
    data_mov DATETIME DEFAULT CURRENT_TIMESTAMP,
    id_usuario_mov INT,
    FOREIGN KEY (id_produto) REFERENCES produtos(id),
    FOREIGN KEY (id_usuario_mov) REFERENCES usuarios(id)
);
```

### Dados Iniciais (Seed)

```sql
INSERT IGNORE INTO usuarios (login, senha, nome_completo)
VALUES ('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'Administrador');
-- senha: admin

INSERT IGNORE INTO categorias (nome)
VALUES ('Eletrônicos'), ('Escritório'), ('Limpeza');
```

---

## 12. Configuração do Projeto

### pom.xml (Maven)

**Localização:** `pom.xml`

```xml
<groupId>com.sistema</groupId>
<artifactId>controle-estoque</artifactId>
<version>1.0.0</version>
<packaging>jar</packaging>
```

**Propriedades:**
- Java 11 (source e target)
- UTF-8

**Dependências:**
- `mysql-connector-j` 8.0.33 — driver JDBC MySQL

**Plugins:**
- `maven-compiler-plugin` 3.11.0
- `exec-maven-plugin` 3.1.0 (mainClass: `com.sistema.view.MainView`)

**Comandos Maven:**
| Comando              | Descrição                           |
|----------------------|-------------------------------------|
| `mvn clean compile`  | Compila o projeto                   |
| `mvn exec:java`      | Executa a aplicação                 |
| `mvn package`        | Gera o JAR executável               |

### db.properties

**Localização:** `src/main/resources/db.properties`

```properties
db.url=jdbc:mysql://localhost:3306/sistema_estoque?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
db.user=root
db.pass=
```

- **db.url:** URL JDBC com parâmetros para desabilitar SSL, definir timezone e criar banco automaticamente
- **db.user:** Usuário do MySQL (root por padrão)
- **db.pass:** Senha do MySQL (vazia por padrão — ajustar conforme ambiente)

---

## 13. Fluxo de Navegação da Aplicação

```
                  ┌──────────────────┐
                  │    MainView      │
                  │  (main inicial)  │
                  └────────┬─────────┘
                           │
                           ▼
                  ┌──────────────────┐
                  │   LoginView      │
                  │  ┌────────────┐  │
                  │  │ Entrar     │──┼──► autenticar()
                  │  └────────────┘  │       │
                  │  ┌────────────┐  │       ▼
                  │  │ Cadastrar  │──┼──► CadastroUsuarioView
                  │  │ -se        │  │       │
                  │  └────────────┘  │       ▼
                  └──────────────────┘  CadastroUsuarioController
                           │              │
                           │              ▼
                           │         (salva + volta)
                           ▼
                  ┌──────────────────────────────────────┐
                  │        TelaPrincipalView             │
                  │  ┌────────────────────────────────┐  │
                  │  │  Aba 1: PRODUTOS               │  │
                  │  │  ┌──────────────────────────┐  │  │
                  │  │  │ Busca + Tabela + CRUD    │  │  │
                  │  │  └──────────────────────────┘  │  │
                  │  ├────────────────────────────────┤  │
                  │  │  Aba 2: CATEGORIAS             │  │
                  │  │  ┌──────────────────────────┐  │  │
                  │  │  │ Tabela + Cadastro Rápido │  │  │
                  │  │  └──────────────────────────┘  │  │
                  │  ├────────────────────────────────┤  │
                  │  │  Aba 3: MOVIMENTAÇÕES          │  │
                  │  │  ┌──────────────────────────┐  │  │
                  │  │  │ Histórico + Entrada/Saída│  │  │
                  │  │  └──────────────────────────┘  │  │
                  │  └────────────────────────────────┘  │
                  └──────────────────────────────────────┘
```

---

## 14. Glossário

| Termo                | Definição                                               |
|----------------------|---------------------------------------------------------|
| CRUD                 | Create, Read, Update, Delete — operações básicas.       |
| DAO                  | Data Access Object — padrão para persistência em BD.   |
| DTO                  | Data Transfer Object — objeto para transporte de dados. |
| EDT                  | Event Dispatch Thread — thread única do Swing para UI.  |
| ENUM                 | Tipo enumerado com valores fixos (ENTRADA, SAIDA).      |
| Hash                 | Função unidirecional que transforma texto em código.    |
| JDBC                 | Java Database Connectivity — API de conexão com BD.     |
| JFrame               | Janela principal do Swing.                              |
| JOptionPane          | Diálogo de mensagens do Swing (alertas, confirmação).   |
| JTable               | Componente Swing para exibição de tabelas.              |
| LEFT JOIN            | JOIN que mantém registros mesmo sem correspondência.    |
| Maven                | Ferramenta de build e gerenciamento de dependências.    |
| MVC                  | Model-View-Controller — padrão de arquitetura.          |
| SHA-256              | Algoritmo de hash seguro de 256 bits.                   |
| Singleton            | Padrão que garante uma única instância de um objeto.    |
| Swing                | Toolkit de interface gráfica do Java.                   |
