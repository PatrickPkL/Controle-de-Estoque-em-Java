# AGENTS.md

## Build & Run
- `mvn clean compile` — compile
- `mvn exec:java` — run the application (entry: `com.sistema.view.MainView`)
- `mvn package` — build JAR

## Entry Point
`com.sistema.view.MainView` (configured in exec-maven-plugin). MainView launches `LoginView` on the Swing Event Dispatch Thread.

## Database
- MySQL required (driver: mysql-connector-j 8.0.33)
- Database `sistema_estoque` auto-created on first connection (ConnectionUtil.initDatabase creates tables + seeds admin+categories)
- Connection config: `src/main/resources/db.properties` (user=root, pass= empty by default)

## Default Credentials
- Login: `admin`, Password: `admin`
- SHA-256 hash stored: `8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918`

## Architecture
- MVC with DAO layer: `view` → `controller` → `dao` → `model`
- Packages: `com.sistema.{view,controller,dao,model,util}`
- Swing UI built with NetBeans GUI Builder (`.form` files)

## Important Patterns
- Passwords hashed with SHA-256 via `HashUtil.sha256()`
- `SessaoUsuario` singleton holds logged-in user
- `MovimentacaoDAO.registrar()` uses a transaction (rollback on failure)
- `ConnectionUtil.getConnection()` is a singleton; init runs once on first connection

## No Test Suite
- No tests exist in this repo. Do not run test commands.