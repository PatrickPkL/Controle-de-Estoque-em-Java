# 🎨 DESIGN SYSTEM — Sistema de Controle de Estoque

**Versão:** 2.0 (Advanced Design)  
**Data:** 2026-06-09  

---

## 1. DESIGN PHILOSOPHY

O novo design prioriza:
- **Profissionalismo** — Interfaces limpas e modernas
- **Usabilidade** — Hierarquia visual clara e feedback imediato
- **Consistência** — Paleta de cores unificada em todas as telas
- **Acessibilidade** — Contraste adequado e elementos tocáveis

---

## 2. COLOR PALETTE

### Primary Colors
| Nome | Hex | Uso |
|------|-----|-----|
| Primary | `#006699` | Botões principais, headers |
| Primary Dark | `#003366` | Gradientes, estados hover |
| Accent | `#0099CC` | Seleção de tabela, destaques |

### Semantic Colors
| Nome | Hex | Uso |
|------|-----|-----|
| Success | `#2E7D32` | Botões de entrada, sucesso |
| Danger | `#C62828` | Botões de saída, exclusão |
| Warning | `#FF9800` | Botões de atualização |

### Neutral Colors
| Nome | Hex | Uso |
|------|-----|-----|
| White | `#FFFFFF` | Background de cards |
| Light BG | `#F5F5F5` | Background alternativo |
| Border | `#DCDCDC` | Bordas de inputs e tables |
| Text Dark | `#323232` | Texto principal |
| Text Light | `#787878` | Texto secundário |

---

## 3. TYPOGRAPHY

### Font Family
- **Primary:** Segoe UI (Windows native)
- **Fallback:** Arial, sans-serif
- **Emoji:** Segoe UI Emoji (para ícones)

### Font Sizes
| Uso | Size | Weight |
|-----|------|--------|
| Page Title | 26px | Bold |
| Section Title | 16-18px | Bold |
| Body Text | 13-14px | Regular |
| Labels | 12-13px | Bold |
| Small Text | 11px | Italic |

---

## 4. SPACING SYSTEM

### Margins
- **Page Padding:** 20-30px
- **Section Gap:** 15-20px
- **Card Padding:** 20px
- **Component Gap:** 10-15px

### Component Sizes
| Componente | Height | Width |
|------------|--------|-------|
| Text Input | 32-35px | 150-330px |
| Button Primary | 35-45px | 120-250px |
| Button Secondary | 40px | 250px |
| Table Row | 28-30px | — |

---

## 5. COMPONENT SPECIFICATIONS

### 5.1 Text Fields

**Default State:**
```java
Border: 1px solid #DCDCDC
Padding: 5px top/bottom, 10px left/right
Font: Segoe UI, 13-14px
Caret Color: #006699
```

**Focus State:**
```java
Border: 1px solid #006699
```

### 5.2 Buttons

**Primary Button (Entrar, Cadastrar):**
```java
Background: #006699
Text: White, Bold, 12-14px
Border: none
Padding: 10px 20px
Hover: #005285
```

**Secondary Button (Voltar):**
```java
Background: White
Text: #006699, Regular, 13px
Border: 1px solid #006699
Hover: Background #F0F8FF
```

**Success Button (Entrada):**
```java
Background: #2E7D32
```

**Danger Button (Saída, Excluir):**
```java
Background: #C62828
```

**Warning Button (Atualizar):**
```java
Background: #FF9800
```

### 5.3 Cards

**Design:**
```java
Background: White
Border: none
Shadow: simulated with offset darker panel
Corner Radius: 0 (sharp corners for professional look)
```

### 5.4 Tables

**Header:**
```java
Background: #006699
Text: White, Bold, 12px
Row Height: 28-30px
Grid Color: #DCDCDC
Selection: #0099CC (light blue)
```

### 5.5 Tabs

```java
Font: Segoe UI, Bold, 14px
Background: White
Selected: Border bottom 3px #006699
Unselected: Text #787878
```

---

## 6. SCREEN SPECIFICATIONS

### 6.1 LoginView

```
┌────────────────────────────────────┐
│  GRADIENT HEADER (Primary)         │
│                                    │
│  ┌────────────────────────────┐    │
│  │  Sistema de Estoque        │    │
│  │  Faça login para continuar │    │
│  │                            │    │
│  │  👤 Usuário                │    │
│  │  [________________]        │    │
│  │                            │    │
│  │  🔒 Senha                  │    │
│  │  [________________]        │    │
│  │                            │    │
│  │  [      Entrar      ]      │    │
│  │  [   Cadastrar-se   ]      │    │
│  └────────────────────────────┘    │
└────────────────────────────────────┘

Size: 450 x 550px
Position: Centered on screen
```

### 6.2 CadastroUsuarioView

```
┌────────────────────────────────────┐
│  GRADIENT HEADER (Primary)          │
│                                    │
│  ┌────────────────────────────┐    │
│  │  Criar Conta               │    │
│  │  Preencha os dados         │    │
│  │                            │    │
│  │  👤 Novo Login             │    │
│  │  [________________]        │    │
│  │                            │    │
│  │  🔒 Senha                  │    │
│  │  [________________]        │    │
│  │                            │    │
│  │  ✓ Confirmar Senha         │    │
│  │  [________________]        │    │
│  │                            │    │
│  │  [   Salvar Cadastro   ]   │    │
│  │  [   Voltar para Login ]   │    │
│  └────────────────────────────┘    │
└────────────────────────────────────┘
```

### 6.3 TelaPrincipalView — Dashboard

```
┌──────────────────────────────────────────────────────────────────┐
│ GRADIENT HEADER                                                  │
│  Sistema de Controle de Estoque          👤 admin    [Sair] [✕]   │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─ 📦 Produtos ──┬─ 🏷️ Categorias ──┬─ 🔄 Movimentações ──┐     │
│  │                                                            │     │
│  │  Buscar por Nome: [_______________] [🔍 Buscar]           │     │
│  │                                                            │     │
│  │  ┌────────────────────────────────────────────────────┐   │     │
│  │  │ ID │ Nome │ Qtd │ Preço │ Categoria │ Cad Por     │   │     │
│  │  ├────┼──────┼─────┼───────┼───────────┼────────────┤   │     │
│  │  │ 1  │ Tecl │ 100 │ 150   │ Eletrônic │ admin       │   │     │
│  │  └────────────────────────────────────────────────────┘   │     │
│  │                                                            │     │
│  │  Dados do Produto                                          │     │
│  │  Nome: [__________________]  Categoria: [v Eletrônicos]   │     │
│  │  Qtd: [____]  Preço: [______]                             │     │
│  │                          [+Cadastrar] [↑Atualizar] [🗑️]   │     │
│  │                                                            │     │
│  │  ┌─ Estatísticas Rápidas ────────────────────────────┐    │     │
│  │  │  Total de Produtos: 24    │  Valor Total: R$12.500  │    │     │
│  │  └───────────────────────────────────────────────────┘    │     │
│  │                                                            │     │
│  └────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────┘

Size: 1000 x 700px
Position: Centered on screen
Undecorated (custom title bar)
```

---

## 7. ICONS EMOJI

| Contexto | Emoji | Usage |
|----------|-------|-------|
| Usuário | 👤 | Login label |
| Senha | 🔒 | Password label |
| Confirmar | ✓ | Confirm password label |
| Buscar | 🔍 | Search button |
| Cadastrar | ➕ | Add buttons |
| Atualizar | ✏️ | Edit button |
| Excluir | 🗑️ | Delete button |
| Entrada | 📥 | Stock in button |
| Saída | 📤 | Stock out button |
| Produtos | 📦 | Tab icon |
| Categorias | 🏷️ | Tab icon |
| Movimentações | 🔄 | Tab icon |

---

## 8. ANIMATIONS & INTERACTIONS

### Hover Effects
```java
Button: background becomes 10% darker
Cursor: HAND_CURSOR on all clickable elements
```

### Focus Effects
```java
TextField: border changes to primary color
```

### Click Feedback
```java
Buttons: immediate color change on mouseEnter/mouseExit
```

---

## 9. RESPONSIVIDADE

### Fixed Dimensions
- Login/Cadastro: 450 x 550px
- Dashboard: 1000 x 700px

### Window Behavior
- Resizable: false (fixed size)
- Centered on screen
- Undecorated (custom title bar with close button)

---

## 10. ACCESSIBILITY

### Contrast Ratios
- Text on Primary: 4.5:1+ ✓
- Text on White: 7:1+ ✓

### Touch Targets
- Minimum size: 35px height
- All buttons easily clickable

### Focus Indicators
- All interactive elements have visible focus state

---

## 11. IMPLEMENTATION NOTES

### Files Modified
1. `LoginView.java` — Pure code redesign (no .form)
2. `CadastroUsuarioView.java` — Pure code redesign
3. `TelaPrincipalView.java` — Pure code redesign

### Key Changes
- All views now use pure Java code (no NetBeans GUI builder)
- Consistent color scheme across all screens
- Professional card-based layout
- Custom title bar (undecorated window)

### Controller Updates
- `CadastroUsuarioController.java` — Added password confirmation validation

---

*Design System v2.0 — 2026-06-09*