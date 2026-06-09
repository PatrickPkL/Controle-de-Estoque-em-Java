# 📖 Guia de Explicação do Sistema de Estoque

Este guia foi criado para ajudar você a explicar o projeto de forma simples e clara. O sistema segue o padrão **MVC** (Model-View-Controller), que separa a interface da lógica e do banco de dados.

---

## 📂 Estrutura de Pastas (O que cada uma faz?)

### 1. `com.sistema.model` (Os Dados)
Aqui ficam as "formas" dos nossos dados. São classes simples que representam o que o sistema manipula.
- **Exemplo:** A classe `Produto` diz que todo produto tem um nome, preço e quantidade.
- **O que faz:** Apenas guarda e transporta informações entre as outras partes do sistema.

### 2. `com.sistema.dao` (O Banco de Dados)
**DAO** significa *Data Access Object*. É aqui que o código Java conversa com o SQL.
- **O que faz:** Contém os comandos `SELECT`, `INSERT`, `UPDATE` e `DELETE`.
- **Como funciona:** Ele abre uma conexão com o banco, prepara o comando SQL e executa.

### 3. `com.sistema.controller` (O Cérebro)
É a ponte entre a tela (View) e o banco (DAO).
- **O que faz:** Quando você clica em um botão na tela, o Controller percebe, pega os dados que você digitou, valida se estão certos e manda para o DAO salvar no banco.
- **Lógica:** É aqui que decidimos o que acontece quando um erro ocorre ou quando uma operação dá certo.

### 4. `com.sistema.view` (A Interface)
São as janelas e botões que o usuário vê.
- **O que faz:** Exibe os dados em tabelas e fornece campos para digitar informações.
- **Regra:** A View não deve ter lógica de banco de dados. Ela apenas "avisa" o Controller que algo aconteceu.

### 5. `com.sistema.util` (As Ferramentas)
Classes que ajudam o sistema todo.
- **`ConnectionUtil`:** O "encanador" que gerencia a conexão com o MySQL.
- **`HashUtil`:** O "criptógrafo" que transforma senhas em códigos seguros.
- **`SessaoUsuario`:** O "crachá" que lembra quem é o usuário que está logado.

---

## 🔄 Fluxo de uma Operação (Exemplo: Registro de Movimentação)

1. **Seleção:** Na aba **Produtos**, selecione o produto que deseja alterar o estoque.
2. **Quantidade:** Vá na aba **Movimentações** e digite a quantidade no campo "Qtd p/ Movimentar".
3. **Ação:** Clique em **Registrar Entrada** (para adicionar) ou **Registrar Saída** (para remover).
4. **Controller:** O `ProdutoController` pega o ID do produto selecionado na outra aba, a quantidade digitada e o tipo da movimentação.
5. **DAO:** O `MovimentacaoDAO.registrar()` é chamado. Ele faz duas coisas ao mesmo tempo (Transação):
   - Adiciona o registro no histórico de movimentações.
   - Atualiza a quantidade atual na tabela de produtos.
6. **Atualização:** As tabelas de Produtos e Movimentações são atualizadas automaticamente.

---

## 🛠️ Tecnologias e Conceitos Chave

- **JDBC:** Tecnologia que permite ao Java se conectar a qualquer banco de dados SQL.
- **Try-with-resources:** Usamos `try (Connection conn = ...)` para garantir que a conexão com o banco feche sozinha, evitando lentidão.
- **Lambda Expressions:** Usamos `e -> acao()` nos botões para deixar o código mais curto e moderno.
- **SHA-256:** Criptografia usada para que a senha do usuário nunca seja salva como texto puro no banco.

---

## 💡 Dicas para a Explicação

- **"Por que MVC?"**: Para que, se um dia decidirmos mudar a interface de Desktop para Web, só precisemos trocar a pasta `view`, mantendo todo o resto do código intacto.
- **"Segurança"**: Destaque que usamos `PreparedStatement` para evitar ataques de *SQL Injection* (um hacker tentar mandar comandos SQL pelos campos de texto).
