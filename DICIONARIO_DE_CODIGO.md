# 📕 Dicionário de Código: Explicação Linha a Linha

Este documento explica os comandos mais importantes do seu sistema para que você saiba exatamente o que responder se o professor apontar para uma linha de código.

---

## 1. Conexão com o Banco (`ConnectionUtil.java`)

```java
public static synchronized Connection getConnection() throws SQLException {
```
> **Explicação:** Cria um método que devolve uma conexão com o banco. O `synchronized` garante que se dois botões forem clicados ao mesmo tempo, o Java organize a fila para não dar erro de conexão.

```java
Class.forName("com.mysql.cj.jdbc.Driver");
```
> **Explicação:** Carrega o "Driver" do MySQL. É como instalar o driver de uma impressora para que o Windows saiba falar com ela; aqui o Java aprende a falar com o MySQL.

```java
conn = DriverManager.getConnection(url, user, pass);
```
> **Explicação:** Esta é a linha que efetivamente abre a porta do banco de dados usando o endereço, usuário e senha.

---

## 2. Acesso a Dados (`UsuarioDAO.java` e outros)

```java
String sql = "INSERT INTO usuarios (login, senha) VALUES (?, ?)";
```
> **Explicação:** O `?` é um "espaço reservado". Usamos isso por segurança, para que o Java limpe os dados antes de enviar ao banco.

```java
try (PreparedStatement ps = conn.prepareStatement(sql)) {
```
> **Explicação:** O `PreparedStatement` é o tradutor. Ele pega o comando SQL e prepara para ser executado com segurança. O `try (...)` com parênteses garante que, após o uso, a conexão seja fechada automaticamente (evita lentidão).

```java
ps.setString(1, usuario.getLogin());
```
> **Explicação:** Aqui dizemos: "No primeiro ponto de interrogação `?`, coloque o login do usuário".

---

## 3. Transações de Estoque (`MovimentacaoDAO.java`)

Este é o código mais avançado do projeto, muito valorizado em provas:

```java
conn.setAutoCommit(false);
```
> **Explicação:** "Desliga o salvamento automático". Isso permite que a gente faça várias coisas e só salve no final se tudo der certo.

```java
conn.commit();
```
> **Explicação:** "Confirmar". Salva todas as alterações (o registro da movimentação E a mudança na quantidade do produto) de uma vez só.

```java
conn.rollback();
```
> **Explicação:** "Desfazer". Se o registro da movimentação der certo, mas o computador travar antes de atualizar o estoque, o `rollback` cancela tudo para o banco não ficar com dados errados.

---

## 4. O Controlador (`ProdutoController.java`)

```java
view.getBtnEntrada().addActionListener(e -> registrarMovimentacao(...));
```
> **Explicação:** O `addActionListener` diz ao botão para ficar "ouvindo" o clique. O `e ->` é uma **Expressão Lambda**, uma forma moderna e curta de dizer ao Java o que fazer quando o clique acontecer.

```java
int row = view.getTableProdutos().getSelectedRow();
```
> **Explicação:** Pergunta para a tabela: "Qual linha o usuário clicou com o mouse?". Se for `-1`, significa que ninguém selecionou nada.

```java
int id = (int) view.getTableProdutos().getValueAt(row, 0);
```
> **Explicação:** Pega o valor da coluna `0` (que é o ID) da linha que o usuário selecionou.

---

## 5. Segurança de Senhas (`HashUtil.java`)

```java
MessageDigest digest = MessageDigest.getInstance("SHA-256");
```
> **Explicação:** Ativa o algoritmo SHA-256. Ele transforma uma senha como "123" em um código gigante e impossível de ler.

---

## 💡 Termos Técnicos que você pode usar:

1.  **POJO (Plain Old Java Object):** São as classes da pasta `model` (classes puras, só com atributos e getters/setters).
2.  **Singleton (ou quase isso):** A classe `SessaoUsuario` que mantém o usuário logado em memória durante todo o uso do sistema.
3.  **JDBC (Java Database Connectivity):** O conjunto de bibliotecas que usamos para conectar o Java ao MySQL.
4.  **Surgical Update:** Quando atualizamos apenas uma parte da tela (como uma tabela) sem precisar fechar e abrir a janela.
