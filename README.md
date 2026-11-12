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
- **Inteligência Artificial**: Google Gemini (modelo padrão `gemini-2.5-flash` via REST API, configurável por `GEMINI_MODEL`, com isolamento `<curriculo_texto>` contra prompt injection). A variável `GEMINI_API_KEY` é obrigatória; não há fallback simulado.
- **Banco de Dados**: PostgreSQL 16 (via Docker Compose na porta `5438:5432` para evitar colisões com instâncias locais; suporte a H2 in-memory para testes unitários)
- **Documentação Interativa de API**: Springdoc OpenAPI / Swagger UI 2.6.0
- **Build Tool**: Apache Maven 3.9+

---

## ⚙️ Arquitetura e Regras de Negócio Implementadas

| Regra / Requisito | Descrição |
|---|---|
| **RN01 (Cadastro de Vagas)** | Título, descrição, requisitos obrigatórios, desejáveis e tempo mínimo de experiência em anos (`ROLE_RECRUITER`). |
| **RN02 (Submissão de Currículos)** | Candidatos podem submeter currículos para vagas ativas. Cada submissão gera uma candidatura e é processada de forma independente. |
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
- Java 21 JDK (ou superior) configurado no PATH
- Docker & Docker Compose (para subir a instância do PostgreSQL)
- *Obs:* Não é necessário instalar o Maven globalmente, pois o projeto já inclui o Maven Wrapper (`mvnw` / `mvnw.cmd`).

### 2. Subir o Banco de Dados (PostgreSQL)
O container está mapeado para a porta **5438** do host (para não disputar com a porta 5432 de bancos locais):
```bash
docker-compose up -d
```

### 3. Configurar Variáveis de Ambiente
Defina no ambiente ou no arquivo `.env` (na raiz do projeto):
- `GEMINI_API_KEY`: Chave obrigatória da API do Google Gemini.
- `GEMINI_MODEL`: Modelo Gemini a utilizar (padrão: `gemini-2.5-flash`).
- `DB_URL`: `jdbc:postgresql://localhost:5438/stric_db`
- `DB_USERNAME`: `postgres`
- `DB_PASSWORD`: `postgres`
- `SERVER_PORT` ou `PORT`: Porta HTTP da aplicação (padrão: `8081`).

### 4. Compilar e Executar

#### Via Terminal (PowerShell / Windows):
```powershell
# Executar a suíte de testes unitários
.\mvnw.cmd test

# Iniciar a aplicação Spring Boot
.\mvnw.cmd spring-boot:run
```

#### Via Terminal (Linux / macOS):
```bash
# Executar a suíte de testes unitários
./mvnw test

# Iniciar a aplicação Spring Boot
./mvnw spring-boot:run
```

#### Via IDE (VS Code / Antigravity IDE):
Pressione `F5` ou selecione o profile **StricApiApplication (dev)** na aba *Run & Debug*.

A API estará acessível em: `http://localhost:8081` por padrão. Configure `SERVER_PORT` ou `PORT` caso precise utilizar outra porta.

---

## 📖 Documentação da API (Swagger / OpenAPI)
Acesse a interface interativa e teste todos os endpoints diretamente no navegador:
👉 **[http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)**

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
curl -X PUT http://localhost:8081/api/auth/profile   -H "Content-Type: application/json"   -H "Authorization: Bearer <TOKEN_JWT>"   -d '{
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
| `GET` | `/api/vagas` | Público | Listar vagas com status `ABERTA`. Suporta `?search=` e `?maxExperience=`. |
| `GET` | `/api/vagas/minhas` | `RECRUITER` | Listar apenas as vagas criadas pelo recrutador autenticado. |
| `GET` | `/api/vagas/{id}` | Público | Obter detalhes de uma vaga específica pelo ID. |
| `POST` | `/api/vagas` | `RECRUITER` | Criar nova vaga de emprego com requisitos. |
| `PUT` | `/api/vagas/{id}` | `RECRUITER` | Atualizar dados completos da vaga. |
| `PATCH` | `/api/vagas/{id}/status` | `RECRUITER` | Atualizar status da vaga (`ABERTA`, `PAUSADA`, `ENCERRADA`). |
| `DELETE`| `/api/vagas/{id}` | `RECRUITER` | Excluir vaga do sistema. |

#### Exemplo de Cadastro de Vaga (cURL):
```bash
curl -X POST http://localhost:8081/api/vagas   -H "Content-Type: application/json"   -H "Authorization: Bearer <TOKEN_RECRUTADOR>"   -d '{
    "titulo": "Desenvolvedor Java Backend Sênior",
    "descricao": "Responsável pela construção de microsserviços de alto desempenho com Spring Boot.",
    "requisitosObrigatorios": ["Java 21", "Spring Boot 3", "PostgreSQL", "Docker"],
    "requisitosDesejaveis": ["Kubernetes", "AWS", "Spring AI"],
    "tempoMinimoExperienciaAnos": 5
  }'
```

#### Exemplo de Alteração de Status da Vaga (cURL):
```bash
curl -X PATCH http://localhost:8081/api/vagas/<VAGA_UUID>/status   -H "Content-Type: application/json"   -H "Authorization: Bearer <TOKEN_RECRUTADOR>"   -d '{
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
| `PATCH` | `/api/candidaturas/{id}/reprocessar` | `RECRUITER` | Agendar nova análise explicável, preservando a análise anterior para auditoria. Retorna `HTTP 202 Accepted`. |
| `GET` | `/api/candidaturas/{id}/download-pdf` | Autenticado | Fazer download do PDF original do currículo submetido. |

#### Exemplo de Submissão de Currículo (cURL):
```bash
curl -X POST http://localhost:8081/api/candidaturas/vagas/<VAGA_UUID>/submeter   -H "Authorization: Bearer <TOKEN_CANDIDATO>"   -F "arquivo=@/caminho/do/curriculo.pdf;type=application/pdf"
```

#### Exemplo de Seleção para Entrevista com LGPD (cURL):
```bash
curl -X PATCH http://localhost:8081/api/candidaturas/<CANDIDATURA_UUID>/selecionar-entrevista   -H "Content-Type: application/json"   -H "Authorization: Bearer <TOKEN_RECRUTADOR>"   -d '{
    "justificativa": "Candidato atende 100% dos requisitos obrigatórios com score de IA 92%."
  }'
```

#### Exemplo de Download do PDF do Currículo (cURL):
```bash
curl -X GET http://localhost:8081/api/candidaturas/<CANDIDATURA_UUID>/download-pdf   -H "Authorization: Bearer <TOKEN_RECRUTADOR_OU_CANDIDATO>"   --output curriculo_baixado.pdf
```

#### Exemplo de Reprocessamento da Análise (cURL):
```bash
curl -X PATCH http://localhost:8081/api/candidaturas/<CANDIDATURA_UUID>/reprocessar \
  -H "Authorization: Bearer <TOKEN_RECRUTADOR>"
```

---

## 👥 Papéis de Usuário e Controle de Acesso (RBAC)

O controle de acesso no STRIC é baseado em papéis estritos geridos pelo Spring Security:

- **`ROLE_CANDIDATE`**: Perfil de candidato autenticado. Tem permissão para consultar vagas, submeter candidaturas (upload de PDF), acompanhar o status de suas próprias candidaturas e baixar o PDF submetido.
- **`ROLE_RECRUITER`**: Perfil de recrutador autenticado. Tem permissão para criar, alterar e encerrar vagas, visualizar a listagem e os detalhes de candidatos triados com mascaramento LGPD, solicitar revelação de contato para entrevista e disparar o reprocessamento da triagem.
- **Visitante (Acesso Público)**: Representa o usuário não autenticado. **Não é um papel (`Role`) cadastrado ou persistido no banco de dados**, mas sim o acesso anônimo concedido a endpoints públicos, tais como endpoints de autenticação (`/api/auth/register`, `/api/auth/login`), visualização pública de vagas abertas (`GET /api/vagas`, `GET /api/vagas/{id}`) e documentação Swagger.
- **Integrações de Infraestrutura (Google Gemini e Tesseract OCR)**: São componentes e serviços de integração de infraestrutura consumidos pelo backend, **não possuindo credenciais de usuário, papéis ou representação no modelo de RBAC**.

---

## 🧮 Como o Score de Aderência é Definido

O cálculo do score final de compatibilidade técnica (0 a 100%) é **100% determinístico e executado exclusivamente pelo backend** (`CalculadoraScoreService`), garantindo total auditabilidade:

1. **Separação de Responsabilidades da IA**:
   - A Inteligência Artificial (Gemini) **não calcula a nota global** e **não tem autoridade para aprovar ou reprovar candidatos**.
   - O papel do modelo generativo restringe-se a interpretar semanticamente o currículo frente aos requisitos cadastrados, retornando a classificação individual, o percentual de atendimento de cada item, citações de evidência e estimativas de experiência/formação.
2. **Ponderação e Distribuição de Pesos**:
   - **Vagas com Requisitos Desejáveis**:
     $$\text{Score} = (0{,}70 \times \text{Aderência}_{\text{obrigatórios}}) + (0{,}20 \times \text{Aderência}_{\text{desejáveis}}) + (0{,}10 \times \text{Aderência}_{\text{experiência}})$$
   - **Vagas sem Requisitos Desejáveis**:
     Para evitar penalização indevida do candidato, o peso dos desejáveis é absorvido pelos obrigatórios:
     $$\text{Score} = (0{,}90 \times \text{Aderência}_{\text{obrigatórios}}) + (0{,}10 \times \text{Aderência}_{\text{experiência}})$$
3. **Média Ponderada por Requisito**:
   - Dentro de cada grupo (obrigatórios ou desejáveis), o atendimento é calculado pela média ponderada dos percentuais individuais baseada no peso configurado para cada requisito:
     $$\text{Aderência}_{\text{grupo}} = \frac{\sum (\text{peso}_i \times \text{percentualAtendimento}_i)}{\sum \text{peso}_i}$$
4. **Cálculo da Experiência Profissional**:
   - Proporcional ao tempo mínimo exigido pela vaga:
     $$\text{Aderência}_{\text{experiência}} = \min\left(\frac{\text{anosExperienciaCandidato}}{\text{tempoMinimoExperienciaVaga}}, 1{,}0\right)$$
   - Se a vaga não exigir tempo mínimo, a aderência deste critério é considerada integral (1,0).
5. **Normalização Final**:
   - O resultado global é escalado para 0–100 e arredondado para número inteiro (`int`) com compensação para imprecisões de ponto flutuante.

---

## 📊 Dados Estruturados e Privacidade (LGPD)

A análise armazena detalhadamente a avaliação semântica e respeita a minimização de dados preconizada pela LGPD:

- **Estrutura por Requisito (`AnaliseRequisito`)**:
  - `tipo`: Obrigatório (`OBRIGATORIO`) ou Desejável (`DESEJAVEL`).
  - `peso`: Peso relativo definido no cadastro do requisito da vaga (padrão 1).
  - `classificacao`: Enum semântico avaliado pela IA (`ATENDIDO`, `PARCIALMENTE_ATENDIDO`, `NAO_ATENDIDO`, `NAO_IDENTIFICADO`, `INCONCLUSIVO`).
  - `percentualAtendimento`: Valor numérico normalizado entre 0.0 e 1.0.
  - `evidenciaTexto`: Trecho literal extraído do currículo que sustenta a avaliação.
  - `paginaCurriculo`: Página do PDF onde a evidência foi localizada.
  - `confiancaIA`: Grau de certeza da IA na inferência (0.0 a 1.0).
  - `justificativa`: Justificativa técnica do modelo para a classificação atribuída.
- **Dados Extraídos do Candidato**:
  - `tempoExperienciaAnos`: Anos totais de experiência profissional identificados.
  - `nivelFormacao`: Escolaridade/titulação mais alta identificada no documento.
  - `resumoExecutivo`: Síntese textual do perfil técnico elaborado pela IA.
- **Anonimização Prévia e Não Persistência de PII**:
  - Antes de qualquer chamada ao LLM, o texto extraído do PDF passa por higienização via `PiiRedactionService`, que substitui dados de contato e dados pessoais identificáveis (como nome do candidato, CPF, telefone e e-mail) por marcadores anônimos (`[NOME_REMOVIDO]`, `[CPF_REMOVIDO]`, etc.).
  - Os campos de contato na análise (`telefone_identificado` e `email_identificado`) são **gravados como `null`** no banco de dados, assegurando que informações sensíveis não sejam duplicadas na estrutura analítica da IA.

---

## 🔍 Rastreabilidade, Auditoria e Limites Atuais

O STRIC mantém mecanismos para auditoria de decisões automatizadas, com limites claros de reprodutibilidade:

- **Metadados Persistidos**:
  - `modelo_ia`: Modelo utilizado na chamada (ex.: `gemini-2.5-flash`).
  - `versao_prompt`: Identificador da versão do prompt empregado (ex.: `triagem-explicavel-v1`).
  - `prompt_utilizado`: **Não armazena o prompt completo expandido** com os dados interpolados do currículo; armazena apenas a string identificadora de versão por razões de economia de armazenamento e privacidade.
  - `raw_response_json`: Armazena o JSON resultante normalizado após o parsing e validação estrutural do DTO (`AnaliseIAResultDTO`), e **não o payload HTTP bruto** (envelope original de resposta do Google Gemini).
  - `data_processamento`: Data/hora registrada no processamento.
- **Histórico de Reprocessamento**:
  - Quando um recrutador aciona `PATCH /api/candidaturas/{id}/reprocessar`, o registro anterior da análise não é sobrescrito destrutivamente: ele é copiado para a entidade `HistoricoAnaliseIA` preservando score anterior, modelo, versão do prompt, raw response JSON estruturado e data da avaliação anterior.
- **Limitações de Reprodutibilidade Conhecidas**:
  - **A versão da fórmula de cálculo de score NÃO é persistida** na tabela de análise. Caso os pesos ou as regras de cálculo do backend venham a ser alterados no código-fonte no futuro, não há identificador de versão do algoritmo persistido na linha da análise para discriminar retroativamente qual fórmula foi aplicada, exceto pela inferência indireta via `data_processamento`.
  - Como nem o prompt interpolado integral nem a resposta HTTP bruta de baixo nível são armazenados na íntegra, a reprodução forense idêntica depende da versão do prompt, do PDF do currículo armazenado e do estado dos requisitos da vaga.

---

Os identificadores de vagas e candidaturas são UUIDs. Para o contrato detalhado da análise por requisito, consulte [a documentação de triagem explicável](docs/triagem-explicavel.md).
