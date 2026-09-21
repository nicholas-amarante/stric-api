# STRIC - Sistema de Triagem e Resumo Inteligente de Currículos
### Backend API REST (Spring Boot 3.3.5 / Java 21)

---

## 📌 Visão Geral do Projeto
O **STRIC** é uma plataforma de triagem automatizada de currículos em formato PDF. Utilizando **Inteligência Artificial (Google Gemini)**, o sistema analisa a compatibilidade técnica dos candidatos frente aos requisitos de vagas cadastradas, gerando scores de aderência (0 a 100%), resumos executivos e extração estruturada de dados, com rigoroso respeito à **LGPD** e **segurança em múltiplas camadas**.

---

## 🚀 Tecnologias Utilizadas
- **Linguagem**: Java 21 LTS
- **Framework Principal**: Spring Boot 3.3.5 (Spring Web, Spring Security 6, Spring Data JPA, Spring Validation, Spring Async)
- **Segurança & Autenticação**: JJWT 0.12.6 (HMAC-SHA256, Stateless JWT) com BCrypt
- **Processamento de PDF**: Apache PDFBox 3.0.3
- **Inteligência Artificial**: Google Gemini (via REST API com isolamento `<curriculo_texto>` anti-prompt injection e fallback inteligente para desenvolvimento local)
- **Banco de Dados**: PostgreSQL 16 (com suporte H2 para testes locais)
- **Documentação de API**: Springdoc OpenAPI / Swagger UI 2.6.0
- **Build Tool**: Maven

---

## ⚙️ Arquitetura e Regras de Negócio Implementadas

| Regra / Requisito | Descrição |
|---|---|
| **RN01 (Cadastro de Vagas)** | Título, descrição, requisitos obrigatórios, desejáveis e tempo mínimo de experiência em anos (`ROLE_RECRUITER`). |
| **RN02 (Submissão Única)** | Candidato só pode submeter 1 currículo por vaga ativa (`uk_candidatura_vaga_candidato`). |
| **RN03 (Processamento Assíncrono)** | Endpoint de upload responde imediatamente `HTTP 202 Accepted` com status `EM_ANALISE` e processa a IA em segundo plano via `@Async`. |
| **RN04 (Cálculo de Aderência)** | A IA avalia o currículo frente aos requisitos e gera score de 0 a 100%. |
| **RN05 (Ciclo de Vida & LGPD)** | Registro de `data_submissao` para controle de retenção de dados e histórico de auditoria. |
| **Upload Seguro (Multicamadas)** | Validação de extensão `.pdf`, MIME type `application/pdf`, Magic Bytes binários (`%PDF-`), limite de 5MB e armazenamento seguro com UUID fora do web root. |
| **Proteção de Dados (LGPD)** | Listagem para recrutador com dados de contato mascarados (`(11) 9****-**34`, `j***@***.com`). Liberação sob demanda via `PATCH /selecionar-entrevista` com log de auditoria. |
| **Segurança Anti-Injection** | Prompt da IA isola o texto entre `<curriculo_texto>...</curriculo_texto>` e instrui o modelo a tratar todo o conteúdo estritamente como dado não confiável. |

---

## 🛠️ Como Executar a Aplicação

### 1. Pré-requisitos
- Java 21 JDK instalado
- Maven 3.9+ instalado
- Docker & Docker Compose (opcional, para subir o PostgreSQL)

### 2. Subir o Banco de Dados (PostgreSQL)
```bash
docker-compose up -d
```

### 3. Configurar Variáveis de Ambiente (Opcional)
Você pode configurar as seguintes variáveis no seu ambiente ou no `application.yml`:
- `GEMINI_API_KEY`: Sua chave da API do Google Gemini (se não informada, o sistema executará em modo de simulação inteligente para testes locais).
- `DB_URL`: `jdbc:postgresql://localhost:5438/stric_db`
- `DB_USERNAME`: `postgres`
- `DB_PASSWORD`: `postgres`

### 4. Compilar e Executar
```bash
# Executar testes unitários
mvn test

# Iniciar a aplicação Spring Boot
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

---

## 📖 Documentação da API (Swagger / OpenAPI)
Acesse a interface interativa do Swagger UI em:
👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

---

## 📬 Exemplos de Chamadas da API (cURL)

### 1. Registrar Recrutador
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Carla Recrutadora",
    "email": "carla@empresa.com",
    "senha": "senhaSegura123",
    "role": "ROLE_RECRUITER"
  }'
```

### 2. Registrar Candidato
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João da Silva",
    "email": "joao.silva@email.com",
    "senha": "senhaSegura123",
    "role": "ROLE_CANDIDATE"
  }'
```

### 3. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "carla@empresa.com",
    "senha": "senhaSegura123"
  }'
```

### 4. Cadastrar Vaga (Recrutador)
```bash
curl -X POST http://localhost:8080/api/vagas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_RECRUTADOR>" \
  -d '{
    "titulo": "Desenvolvedor Java Backend Sênior",
    "descricao": "Responsável pela construção de microsserviços de alto desempenho com Spring Boot.",
    "requisitosObrigatorios": ["Java 21", "Spring Boot 3", "PostgreSQL", "Docker"],
    "requisitosDesejaveis": ["Kubernetes", "AWS", "Spring AI"],
    "tempoMinimoExperienciaAnos": 5
  }'
```

### 5. Submeter Currículo em PDF (Candidato - HTTP 202 Accepted)
```bash
curl -X POST http://localhost:8080/api/candidaturas/vagas/<VAGA_ID>/submeter \
  -H "Authorization: Bearer <TOKEN_CANDIDATO>" \
  -F "arquivo=@/caminho/do/curriculo.pdf;type=application/pdf"
```

### 6. Listar Candidatos Triados com Dados Mascarados (Recrutador - LGPD)
```bash
curl -X GET http://localhost:8080/api/candidaturas/vagas/<VAGA_ID> \
  -H "Authorization: Bearer <TOKEN_RECRUTADOR>"
```

### 7. Selecionar para Entrevista e Liberar Dados de Contato com Auditoria (Recrutador)
```bash
curl -X PATCH http://localhost:8080/api/candidaturas/<CANDIDATURA_ID>/selecionar-entrevista \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_RECRUTADOR>" \
  -d '{
    "justificativa": "Candidato atende 100% dos requisitos obrigatórios com score de IA 95%."
  }'
```

### 8. Download do PDF Original
```bash
curl -X GET http://localhost:8080/api/candidaturas/<CANDIDATURA_ID>/download-pdf \
  -H "Authorization: Bearer <TOKEN_RECRUTADOR_OU_CANDIDATO>" \
  --output curriculo_baixado.pdf
```
