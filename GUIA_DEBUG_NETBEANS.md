# 📚 GUIA DE DEBUG PARA COLEGAS — Sistema de Controle de Estoque

**Projeto:** Controle-de-Estoque-em-Java  
**Objetivo:** Instruir membros da equipe a debugging no NetBeans Apache  
**Versão NetBeans:** 12+ (Apache NetBeans)  

---

## 1. CONFIGURAÇÃO INICIAL DO AMBIENTE

### 1.1 Abrir o Projeto no NetBeans
```
File → Open Project → Navegue até "Controle-de-Estoque-em-Java" → Open
```
O projeto deve mostrar um ícone de café ☕ com "m" (Maven).

### 1.2 Configurar MySQL
1. Asegure-se que MySQL está rodando: `services MySQL`
2. Crie o banco se não existir:
   ```sql
   CREATE DATABASE IF NOT EXISTS sistema_estoque;
   ```

### 1.3 Configurar db.properties
```
src/main/resources/db.properties
```
```
db.url=jdbc:mysql://localhost:3306/sistema_estoque?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
db.user=root
db.pass=
```

---

## 2. ENTENDENDO A ESTRUTURA DO PROJETO

```
src/main/java/com/sistema/
├── view/          → Interfaces Swing (JFrame)
├── controller/    → Lógica de aplicação
├── dao/           → Acesso ao banco de dados
├── model/         → POJOs (dados)
└── util/          → Utilitários (Connection, Hash, Session)
```

---

## 3. COMO RODAR O PROJETO

### Método 1: Via NetBeans
```
Right-click no projeto → Run
```
Ou pressione `F6`

### Método 2: Via Maven
```bash
mvn clean compile
mvn exec:java
```

### Método 3: Via JAR
```bash
mvn package
java -jar target/controle-estoque-1.0.0.jar
```

---

## 4. COMO DEBUGAR NO NETBEANS

### 4.1 Definir Breakpoints

1. **Abra o arquivo** que deseja debugar (ex: `ProdutoController.java`)
2. **Clique na margem esquerda** ao lado do número da linha
3. Aparecerá um círculo vermelho 🔴 — isso é um breakpoint

**Breakpoints recomendados para começar:**
- `LoginController.java:51` — método autenticar()
- `ProdutoController.java:156` — método salvarProduto()
- `MovimentacaoDAO.java:34` — método registrar()

### 4.2 Iniciar Debugging

```
Debug → Debug Project (Ctrl+F5)
```

O NetBeans parará no primeiro breakpoint encontrado.

### 4.3 Inspecionar Variáveis

Quando o código para no breakpoint:

1. **Variables Window:** `Window → Debugging → Variables`
   - Mostra todas as variáveis locais e seus valores atuais

2. **Hover:** Passe o mouse sobre uma variável no código
   - Exibe tooltip com valor

3. **Watch Window:** `Right-click → New Watch`
   - Monitora variáveis específicas durante a execução

### 4.4 Navegação Entre Breakpoints

| Tecla | Ação |
|-------|------|
| `F5` | Continue (vai até próximo breakpoint) |
| `F8` | Step Over (pula linha atual) |
| `F7` | Step Into (entra dentro de método) |
| `Shift+F7` | Step Out (sai do método atual) |
| `Ctrl+F2` | Stop Debugging |

### 4.5 Exemplo Prático: Debugar Login

1. **Defina breakpoint** em `LoginController.java:60`:
   ```java
   Optional<Usuario> userOpt = usuarioDAO.autenticar(login, senhaHash);
   ```

2. **Inicie debugging** (Ctrl+F5)

3. **Insira credenciais:**
   - Login: `admin`
   - Senha: `admin`

4. **Quando parar no breakpoint, inspecione:**
   ```
   login = "admin"
   senhaHash = "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918"
   userOpt.isPresent() = true/false
   ```

5. **Pressione F5** para continuar até o próximo breakpoint ou término

---

## 5. TESTANDO AS ISSUES CONHECIDAS

### Issue #1: INNER JOIN — Produto desaparece

**Como debugar:**
1. Coloque breakpoint em `ProdutoDAO.java:83` (início do método listar)
2. Execute a query manualmente no MySQL:
   ```sql
   -- Verifique se existe produto sem categoria
   SELECT * FROM produtos WHERE id_categoria IS NULL;
   ```
3. Se existir, o produto não aparecerá na tabela do NetBeans

### Issue #2: NullPointer na Movimentação

**Como debugar:**
1. Coloque breakpoint em `MovimentacaoDAO.java:48`:
   ```java
   ps.setInt(4, mov.getUsuarioMov().getId());
   ```
2. Force `SessaoUsuario` para NULL temporariamente (limpe sessão)
3. Tente registrar movimentação
4. Observe se NullPointer ocorre

### Issue #3: Estoque Negativo

**Como debugar:**
1. Cadastre produto com quantidade = 5
2. Coloque breakpoint em `MovimentacaoDAO.java:52`
3. Tente registrar saída de 10
4. Observe se o SQL executa sem validação

### Issue #4: jLabel6 Visível

**Como debugar:**
1. Abra `TelaPrincipalView.java`
2. Procure linha 88: `jLabel6.setText("jLabel6");`
3. Simplesmente execute e olhe a aba Produtos

---

## 6. FERRAMENTAS ÚTEIS DO NETBEANS

### 6.1 SQL Executor
```
Window → Services → Databases → Right-click → Execute Command
```
Permite executar SQL direto no banco para verificar dados.

### 6.2 Maven Window
```
Window → IDE Tools → Maven
```
Mostra dependências, goals, e executa comandos Maven.

### 6.3 Tasks Window
```
Window → Tasks
```
Mostra anotações TODO no código.

### 6.4 Navigator
```
Window → Navigator
```
Mostra estrutura da classe atual (variáveis, métodos).

---

## 7. ONDE CADA ARQUIVO ESTÁ

| Arquivo | Caminho | O que faz |
|---------|---------|-----------|
| MainView | `view/MainView.java` | Entry point |
| LoginView | `view/LoginView.java` | Tela de login |
| CadastroUsuarioView | `view/CadastroUsuarioView.java` | Tela de cadastro |
| TelaPrincipalView | `view/TelaPrincipalView.java` | Dashboard |
| LoginController | `controller/LoginController.java` | Controla login |
| CadastroUsuarioController | `controller/CadastroUsuarioController.java` | Controla cadastro |
| ProdutoController | `controller/ProdutoController.java` | Controla dashboard |
| UsuarioDAO | `dao/UsuarioDAO.java` | Opera usuários |
| ProdutoDAO | `dao/ProdutoDAO.java` | Opera produtos |
| CategoriaDAO | `dao/CategoriaDAO.java` | Opera categorias |
| MovimentacaoDAO | `dao/MovimentacaoDAO.java` | Opera movimentações |
| ConnectionUtil | `util/ConnectionUtil.java` | Conexão BD |
| HashUtil | `util/HashUtil.java` | Hash SHA-256 |
| SessaoUsuario | `util/SessaoUsuario.java` | Sessão usuário |

---

## 8. FLUXO DE EXECUÇÃO (CALL STACK)

Quando você registrar uma movimentação, a Call Stack será:

```
Thread: AWT-EventQueue-0
  └─ ProdutoController.registrarMovimentacao()
       └─ MovimentacaoDAO.registrar()
            └─ ConnectionUtil.getConnection()
                 └─ DriverManager.getConnection()
```

No debugging, você pode ver isso na janela "Call Stack" (`Window → Debugging → Call Stack`)

---

## 9. CHECKLIST DE TESTES

| Teste | Passos | Esperado |
|-------|--------|----------|
| Login admin | admin / admin | Abre dashboard |
| Login errado | admin / errada | Mensagem erro |
| Cadastro usuário | Dados válidos | Volta ao login |
| Listar produtos | Clicar aba Produtos | Tabela populada |
| Buscar produto | Digitar nome + ENTER | Filtra tabela |
| Cadastrar produto | Preencher + Cadastrar | Produto aparece |
| Atualizar produto | Selecionar + Editar | Produto atualiza |
| Excluir produto | Selecionar + Excluir | Produto some |
| Registrar entrada | Selecionar + Entrada | Quantidade aumenta |
| Registrar saída | Selecionar + Saída | Quantidade diminui |
| Cadastrar categoria | Digitar nome + Cadastrar | Categoria aparece |

---

## 10. COMO REPORTAR BUGS

Ao encontrar um bug:

1. **Capture o Stacktrace completo** (copy do console/Output)
2. **Screenshot da tela** onde o bug ocorreu
3. **Passos para reproduzir:**
   - 1. Abrir aplicação
   - 2. Fazer login como...
   - 3. Clicar em...
4. **Comportamento esperado vs real**

---

*Documento criado para equipe — 2026-06-09*