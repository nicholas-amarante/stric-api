# STRIC - Sistema de Triagem e Resumo Inteligente de Currículos
### Backend API REST (Spring Boot 3.3.5 / Java 21)

---

## 📌 Visão Geral do Projeto
O **STRIC** é uma plataforma de triagem automatizada de currículos em formato PDF. Utilizando **Inteligência Artificial (Google Gemini 1.5 Flash)** e processamento óptico de caracteres (**Tesseract OCR**), o sistema analisa a compatibilidade técnica dos candidatos frente aos requisitos de vagas cadastradas, gerando scores de aderência (0 a 100%), resumos executivos e extração estruturada de dados, com rigoroso respeito à **LGPD** e **segurança em múltiplas camadas**.

---

## 🚀 Tecnologias Utilizadas
- **Linguagem**: Java 21 LTS
- **Framework Principal**: Spring Boot 3.3.5 (Spring Web, Spring Security 6, Spring Data JPA, Spring Validation, Spring Async)
- **Segurança & Autenticação**: JJWT 0.12.6 (HMAC-SHA256, Stateless JWT) com BCrypt Password Encoder
- **Extração Híbrida de PDF**:
  - **Apache PDFBox 3.0.3**: Extração vetorial de fluxos diretos de texto em arquivos PDF digitais.
  - **Tess4J 5.13.0 (Tesseract OCR)**: Fallback automático com renderização a 300 DPI para leitura óptica de currículos escaneados, fotos ou documentos sem camada de texto vetorial, suportado pelos modelos em `tessdata`.
- **Inteligência Artificial**: Google Gemini (modelo `gemini-1.5-flash` via REST API com isolamento `<curriculo_texto>` anti-prompt injection e fallback mock inteligente para desenvolvimento offline/local)
- **Banco de Dados**: PostgreSQL 16 (via Docker Compose na porta `5438:5432` para evitar colisões com instâncias locais; suporte a H2 in-memory para testes unitários)
- **Documentação Interativa de API**: Springdoc OpenAPI / Swagger UI 2.6.0
- **Build Tool**: Apache Maven 3.9+

---

## ⚙️ Arquitetura e Regras de Negócio Implementadas

| Regra / Requisito | Descrição |
|---|---|
| **RN01 (Cadastro de Vagas)** | Título, descrição, requisitos obrigatórios, desejáveis e tempo mínimo de experiência em anos (`ROLE_RECRUITER`). |
| **RN02 (Submissão Única)** | Candidato só pode submeter 1 currículo por vaga ativa (`uk_candidatura_vaga_candidato`). |
| **RN03 (Processamento Assíncrono)** | Endpoint de upload responde imediatamente `HTTP 202 Accepted` com status `EM_ANALISE` e processa a IA em segundo plano via `@Async`. |
| **RN04 (Cálculo de Aderência)** | A IA avalia o currículo frente aos requisitos da vaga e gera score de compatibilidade técnica de 0 a 100%. |
| **RN05 (Ciclo de Vida & LGPD)** | Registro de `data_submissao` para controle de retenção de dados e histórico de auditoria. |
| **Extração Híbrida (PDFBox + OCR)** | Tentativa primária com `PDFTextStripper`. Se o texto for inferior a 30 caracteres, aciona automaticamente o OCR via Tess4J/Tesseract a 300 DPI. |
| **Upload Seguro (Multicamadas)** | Validação de extensão `.pdf`, MIME type `application/pdf`, validação binária de Magic Bytes (`%PDF-`), limite de 5MB e armazenamento seguro com UUID em diretório fora do web root. |
| **Proteção de Dados (LGPD)** | Listagem para recrutador com dados de contato mascarados (`(11) 9****-**34`, `j***@***.com`). Liberação sob demanda via `PATCH /selecionar-entrevista` com justificativa e log de auditoria permanente. |
| **Segurança Anti-Injection** | Prompt da IA isola o texto do currículo entre `<curriculo_texto>...</curriculo_texto>` e instrui o modelo a tratar todo o conteúdo estritamente como dado não confiável. |

---

## 🔑 Carga Inicial de Dados (DatabaseSeeder)

Ao iniciar com o banco de dados vazio, o sistema popula automaticamente dados realistas de teste com a senha padrão **`senha123`**:

| Papel | Nome | E-mail | Senha |
|---|---|---|---|
| **Recrutador** | Ana Beatriz Souza | `ana.souza@techrecruiting.com.br` | `senha123` |
| **Recrutador** | Carlos Eduardo Lima | `carlos.lima@rhagil.com.br` | `senha123` |
| **Candidato** | Lucas Santos Silva | `lucas.santos@email.com` | `senha123` |
| **Candidato** | Beatriz Oliveira Costa | `beatriz.costa@email.com` | `senha123` |

*(Além desses, são gerados 10 recrutadores, 30 candidatos, vagas ativas com candidaturas e análises de IA já persistidas).*

---

## 🛠️ Como Executar a Aplicação

### 1. Pré-requisitos
- Java 21 JDK instalado e configurado no PATH
- Maven 3.9+ instalado
- Docker & Docker Compose (para subir a instância do PostgreSQL)

### 2. Subir o Banco de Dados (PostgreSQL)
O container está mapeado para a porta **5438** do host (para não disputar com a porta 5432 de bancos locais):
```bash
docker-compose up -d
```

### 3. Configurar Variáveis de Ambiente (Opcional)
Defina no ambiente ou no `application.yml` / `application-dev.yml`:
- `GEMINI_API_KEY`: Chave da API do Google Gemini (se omitida, a aplicação utiliza mock inteligente determinístico para desenvolvimento).
- `DB_URL`: `jdbc:postgresql://localhost:5438/stric_db`
- `DB_USERNAME`: `postgres`
- `DB_PASSWORD`: `postgres`

### 4. Compilar e Executar
```bash
# Executar a suíte de testes unitários
mvn test

# Iniciar a aplicação Spring Boot
mvn spring-boot:run
```

A API estará acessível em: `http://localhost:8080`.

---

## 📖 Documentação da API (Swagger / OpenAPI)
Acesse a interface interativa e teste todos os endpoints diretamente no navegador:
👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

---

## 📬 Catálogo Completo de Endpoints REST

### 1. Autenticação e Gestão de Perfil (`/api/auth`)

| Método | Endpoint | Proteção | Descrição |
|---|---|---|---|
| `POST` | `/api/auth/register` | Público | Cadastrar novo usuário (`ROLE_CANDIDATE` ou `ROLE_RECRUITER`). |
| `POST` | `/api/auth/login` | Público | Autenticar e obter token JWT e informações do usuário. |
| `GET` | `/api/auth/me` | Autenticado | Obter dados do usuário atualmente autenticado. |
| `PUT` | `/api/auth/profile` | Autenticado | Atualizar perfil (nome, e-mail e senha). Exige validação da senha atual. |

#### Exemplo de Atualização de Perfil (cURL):
```bash
curl -X PUT http://localhost:8080/api/auth/profile   -H "Content-Type: application/json"   -H "Authorization: Bearer <TOKEN_JWT>"   -d '{
    "nome": "Lucas Santos Silva Atualizado",
    "email": "lucas.novo@email.com",
    "senhaAtual": "senha123",
    "novaSenha": "novaSenhaSegura456"
  }'
```

---

### 2. Gestão de Vagas (`/api/vagas`)

| Método | Endpoint | Proteção | Descrição |
|---|---|---|---|
| `GET` | `/api/vagas` | Público | Listar vagas com status `ABERTA`. Suporta `?termoBusca=` e `?experienciaMaxima=`. |
| `GET` | `/api/vagas/minhas` | `RECRUITER` | Listar apenas as vagas criadas pelo recrutador autenticado. |
| `GET` | `/api/vagas/{id}` | Público | Obter detalhes de uma vaga específica pelo ID. |
| `POST` | `/api/vagas` | `RECRUITER` | Criar nova vaga de emprego com requisitos. |
| `PUT` | `/api/vagas/{id}` | `RECRUITER` | Atualizar dados completos da vaga. |
| `PATCH` | `/api/vagas/{id}/status` | `RECRUITER` | Atualizar status da vaga (`ABERTA`, `PAUSADA`, `ENCERRADA`). |
| `DELETE`| `/api/vagas/{id}` | `RECRUITER` | Excluir vaga do sistema. |

#### Exemplo de Cadastro de Vaga (cURL):
```bash
curl -X POST http://localhost:8080/api/vagas   -H "Content-Type: application/json"   -H "Authorization: Bearer <TOKEN_RECRUTADOR>"   -d '{
    "titulo": "Desenvolvedor Java Backend Sênior",
    "descricao": "Responsável pela construção de microsserviços de alto desempenho com Spring Boot.",
    "requisitosObrigatorios": ["Java 21", "Spring Boot 3", "PostgreSQL", "Docker"],
    "requisitosDesejaveis": ["Kubernetes", "AWS", "Spring AI"],
    "tempoMinimoExperienciaAnos": 5
  }'
```

#### Exemplo de Alteração de Status da Vaga (cURL):
```bash
curl -X PATCH http://localhost:8080/api/vagas/1/status   -H "Content-Type: application/json"   -H "Authorization: Bearer <TOKEN_RECRUTADOR>"   -d '{
    "status": "ENCERRADA"
  }'
```

---

### 3. Candidaturas, Triagem com IA e LGPD (`/api/candidaturas`)

| Método | Endpoint | Proteção | Descrição |
|---|---|---|---|
| `POST` | `/api/candidaturas/vagas/{vagaId}/submeter` | `CANDIDATE` | Submeter currículo em PDF (`multipart/form-data`). Retorna `HTTP 202 Accepted`. |
| `GET` | `/api/candidaturas/vagas/{vagaId}` | `RECRUITER` | Listar candidatos triados de uma vaga com dados mascarados (LGPD). |
| `GET` | `/api/candidaturas/{id}` | Autenticado | Consultar status da candidatura (usado no polling pelo candidato). |
| `GET` | `/api/candidaturas/minhas` | `CANDIDATE` | Listar histórico de candidaturas submetidas pelo candidato autenticado. |
| `PATCH` | `/api/candidaturas/{id}/selecionar-entrevista` | `RECRUITER` | Revelar dados de contato do candidato mediante justificativa e auditoria. |
| `GET` | `/api/candidaturas/{id}/download-pdf` | Autenticado | Fazer download do PDF original do currículo submetido. |

#### Exemplo de Submissão de Currículo (cURL):
```bash
curl -X POST http://localhost:8080/api/candidaturas/vagas/1/submeter   -H "Authorization: Bearer <TOKEN_CANDIDATO>"   -F "arquivo=@/caminho/do/curriculo.pdf;type=application/pdf"
```

#### Exemplo de Seleção para Entrevista com LGPD (cURL):
```bash
curl -X PATCH http://localhost:8080/api/candidaturas/1/selecionar-entrevista   -H "Content-Type: application/json"   -H "Authorization: Bearer <TOKEN_RECRUTADOR>"   -d '{
    "justificativa": "Candidato atende 100% dos requisitos obrigatórios com score de IA 92%."
  }'
```

#### Exemplo de Download do PDF do Currículo (cURL):
```bash
curl -X GET http://localhost:8080/api/candidaturas/1/download-pdf   -H "Authorization: Bearer <TOKEN_RECRUTADOR_OU_CANDIDATO>"   --output curriculo_baixado.pdf
```
