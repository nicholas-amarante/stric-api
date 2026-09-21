package com.stric.recrutamento.config;

import com.stric.recrutamento.domain.entities.*;
import com.stric.recrutamento.domain.enums.Role;
import com.stric.recrutamento.domain.enums.StatusCandidatura;
import com.stric.recrutamento.domain.enums.StatusVaga;
import com.stric.recrutamento.domain.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeder inicial do banco de dados para desenvolvimento e testes.
 * Popula 10 recrutadores, 30 candidatos e vagas diversificadas com candidaturas e análises de IA.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final VagaRepository vagaRepository;
    private final CandidaturaRepository candidaturaRepository;
    private final AnaliseIARepository analiseIARepository;
    private final AuditoriaAcessoContatoRepository auditoriaRepository;
    private final PasswordEncoder passwordEncoder;
    private final StorageProperties storageProperties;

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            log.info("[Seeder] Banco de dados já possui registros ({} usuários). Seeder ignorado.", usuarioRepository.count());
            return;
        }

        log.info("[Seeder] Iniciando população do banco de dados com dados realistas...");

        String senhaPadrao = passwordEncoder.encode("senha123");

        // 1. Criar 10 Recrutadores
        List<Usuario> recrutadores = criarRecrutadores(senhaPadrao);
        usuarioRepository.saveAll(recrutadores);
        log.info("[Seeder] {} recrutadores cadastrados.", recrutadores.size());

        // 2. Criar 30 Candidatos
        List<Usuario> candidatos = criarCandidatos(senhaPadrao);
        usuarioRepository.saveAll(candidatos);
        log.info("[Seeder] {} candidatos cadastrados.", candidatos.size());

        // 3. Criar Vagas Diversificadas
        List<Vaga> vagas = criarVagas(recrutadores);
        vagaRepository.saveAll(vagas);
        log.info("[Seeder] {} vagas criadas com sucesso.", vagas.size());

        // 4. Criar Candidaturas, Análises de IA e Logs de Auditoria
        criarCandidaturasComAnalise(vagas, candidatos);
        log.info("[Seeder] Candidaturas e análises de IA populadas com sucesso!");
    }

    private List<Usuario> criarRecrutadores(String senha) {
        return List.of(
                Usuario.builder().nome("Ana Beatriz Souza").email("ana.souza@techrecruiting.com.br").senha(senha).role(Role.ROLE_RECRUITER).build(),
                Usuario.builder().nome("Carlos Eduardo Lima").email("carlos.lima@rhagil.com.br").senha(senha).role(Role.ROLE_RECRUITER).build(),
                Usuario.builder().nome("Fernanda Torres").email("fernanda.torres@talentos.com.br").senha(senha).role(Role.ROLE_RECRUITER).build(),
                Usuario.builder().nome("Rodrigo Meireles").email("rodrigo.meireles@geekhunter.com.br").senha(senha).role(Role.ROLE_RECRUITER).build(),
                Usuario.builder().nome("Mariana Albuquerque").email("mariana.albuquerque@startuphr.com.br").senha(senha).role(Role.ROLE_RECRUITER).build(),
                Usuario.builder().nome("Gabriel Siqueira").email("gabriel.siqueira@inova.com.br").senha(senha).role(Role.ROLE_RECRUITER).build(),
                Usuario.builder().nome("Juliana Paes Leme").email("juliana.leme@techvagas.com.br").senha(senha).role(Role.ROLE_RECRUITER).build(),
                Usuario.builder().nome("Marcos Vinícius Rocha").email("marcos.rocha@rhsolutions.com.br").senha(senha).role(Role.ROLE_RECRUITER).build(),
                Usuario.builder().nome("Patrícia Diniz").email("patricia.diniz@headhunter.com.br").senha(senha).role(Role.ROLE_RECRUITER).build(),
                Usuario.builder().nome("Thiago Camargo").email("thiago.camargo@recrutamentoti.com.br").senha(senha).role(Role.ROLE_RECRUITER).build()
        );
    }

    private List<Usuario> criarCandidatos(String senha) {
        return List.of(
                Usuario.builder().nome("Lucas Santos Silva").email("lucas.santos@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Beatriz Oliveira Costa").email("beatriz.costa@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Matheus Henrique Pereira").email("matheus.pereira@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Larissa Mendes Farias").email("larissa.mendes@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Gustavo Henrique Alves").email("gustavo.alves@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Camila Rodrigues Lima").email("camila.rodrigues@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Felipe Augusto Ribeiro").email("felipe.ribeiro@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Amanda Caroline Martins").email("amanda.martins@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Bruno Cesar Carvalho").email("bruno.carvalho@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Jéssica Rafaela Gomes").email("jessica.gomes@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Rafael Duarte Nogueira").email("rafael.nogueira@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Mariana Ferreira Castro").email("mariana.castro@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Diego Pinheiro Barbosa").email("diego.barbosa@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Natália Cristina Moreira").email("natalia.moreira@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Vinícius Guimarães Pires").email("vinicius.pires@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Letícia Helena Moura").email("leticia.moura@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Leonardo Azevedo Ramos").email("leonardo.ramos@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Isabella Cardoso Antunes").email("isabella.antunes@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("André Luiz Silveira").email("andre.silveira@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Vanessa Borges Prado").email("vanessa.prado@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Thiago Zanetti Neves").email("thiago.neves@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Tatiana Meireles Fontes").email("tatiana.fontes@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Caio Vinícius Fonseca").email("caio.fonseca@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Bruna Stephanie Dias").email("bruna.dias@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Renan Felipe Bastos").email("renan.bastos@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Aline Vasconcelos Paz").email("aline.paz@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Danilo Augusto Teles").email("danilo.teles@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Gabriela Medeiros Cruz").email("gabriela.cruz@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Igor Santana Valente").email("igor.valente@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build(),
                Usuario.builder().nome("Priscila Brandão Rangel").email("priscila.rangel@email.com").senha(senha).role(Role.ROLE_CANDIDATE).build()
        );
    }

    private List<Vaga> criarVagas(List<Usuario> r) {
        List<Vaga> vagas = new ArrayList<>();

        // 1. Backend Java Sênior
        vagas.add(Vaga.builder()
                .titulo("Desenvolvedor Java / Spring Boot Sênior")
                .descricao("Buscamos engenheiro de software para atuar na arquitetura de microsserviços de missão crítica, processamento assíncrono e alta volumetria de requisições.")
                .requisitosObrigatorios(List.of("Java 21", "Spring Boot 3", "PostgreSQL", "Docker", "Arquitetura Limpa"))
                .requisitosDesejaveis(List.of("Kubernetes", "Apache Kafka", "Spring AI", "AWS"))
                .tempoMinimoExperienciaAnos(5)
                .status(StatusVaga.ABERTA)
                .recrutador(r.get(0))
                .build());

        // 2. Fullstack React + Node
        vagas.add(Vaga.builder()
                .titulo("Engenheiro de Software Fullstack (React + Node.js)")
                .descricao("Desenvolvimento de produtos digitais ponta a ponta, desde APIs RESTful eficientes até interfaces modernas e responsivas.")
                .requisitosObrigatorios(List.of("TypeScript", "React", "Node.js", "REST APIs", "Git"))
                .requisitosDesejaveis(List.of("Next.js", "TailwindCSS", "Jest / Vitest", "GraphQL"))
                .tempoMinimoExperienciaAnos(3)
                .status(StatusVaga.ABERTA)
                .recrutador(r.get(1))
                .build());

        // 3. Frontend React Pleno
        vagas.add(Vaga.builder()
                .titulo("Desenvolvedor Frontend React / Next.js Pleno")
                .descricao("Criação de interfaces ricas, componentizadas e com alta performance de renderização para nosso ecossistema web.")
                .requisitosObrigatorios(List.of("React 18+", "TypeScript", "HTML5 Semântico", "CSS3 / Sass", "Consumo de APIs REST"))
                .requisitosDesejaveis(List.of("Next.js 14", "Zustand / Redux Toolkit", "Figma", "Cypress"))
                .tempoMinimoExperienciaAnos(3)
                .status(StatusVaga.ABERTA)
                .recrutador(r.get(2))
                .build());

        // 4. DevOps / Cloud
        vagas.add(Vaga.builder()
                .titulo("Engenheiro DevOps & Cloud (AWS / Kubernetes)")
                .descricao("Gerenciamento de infraestrutura como código (IaC), pipelines de CI/CD automatizados e observabilidade de clusters de produção.")
                .requisitosObrigatorios(List.of("AWS", "Kubernetes / EKS", "Terraform", "CI/CD (GitHub Actions ou GitLab)", "Linux"))
                .requisitosDesejaveis(List.of("Prometheus & Grafana", "ArgoCD", "Helm", "FinOps"))
                .tempoMinimoExperienciaAnos(4)
                .status(StatusVaga.ABERTA)
                .recrutador(r.get(3))
                .build());

        // 5. Data Science / AI
        vagas.add(Vaga.builder()
                .titulo("Cientista de Dados & Inteligência Artificial")
                .descricao("Construção de modelos preditivos, integração com Large Language Models (LLMs) e análise estatística de grandes conjuntos de dados.")
                .requisitosObrigatorios(List.of("Python", "Pandas / NumPy", "Scikit-Learn", "SQL Avançado", "Machine Learning"))
                .requisitosDesejaveis(List.of("LLMs / LangChain / Gemini", "PyTorch", "MLOps / MLflow", "Docker"))
                .tempoMinimoExperienciaAnos(3)
                .status(StatusVaga.ABERTA)
                .recrutador(r.get(4))
                .build());

        // 6. Engenheiro de Dados Sênior
        vagas.add(Vaga.builder()
                .titulo("Engenheiro de Dados Sênior (Big Data & Pipelines)")
                .descricao("Desenvolvimento de pipelines ETL/ELT resilientes, modelagem de Data Lake e otimização de consultas em larga escala.")
                .requisitosObrigatorios(List.of("Python", "Apache Spark", "SQL / Data Warehousing", "Airflow", "Cloud Data (BigQuery / Redshift)"))
                .requisitosDesejaveis(List.of("dbt", "Kafka", "Data Lakehouse (Delta Lake)", "Databricks"))
                .tempoMinimoExperienciaAnos(5)
                .status(StatusVaga.ABERTA)
                .recrutador(r.get(5))
                .build());

        // 7. Mobile Flutter / Android
        vagas.add(Vaga.builder()
                .titulo("Desenvolvedor Mobile Flutter / Android Pleno")
                .descricao("Desenvolvimento de aplicativos móveis multiplataforma com excelente experiência do usuário e animações fluidas.")
                .requisitosObrigatorios(List.of("Flutter & Dart", "Consumo de APIs REST", "Gerenciamento de Estado (Bloc/Provider)", "Git"))
                .requisitosDesejaveis(List.of("Kotlin nativo", "Publicação nas lojas Google Play e App Store", "Testes Unitários em Flutter"))
                .tempoMinimoExperienciaAnos(3)
                .status(StatusVaga.ABERTA)
                .recrutador(r.get(6))
                .build());

        // 8. Product Designer / UX/UI
        vagas.add(Vaga.builder()
                .titulo("Product Designer Sênior (UX / UI)")
                .descricao("Liderar pesquisas com usuários, criar fluxos de navegação intuitivos, wireframes e design system unificado no Figma.")
                .requisitosObrigatorios(List.of("Figma Avançado", "Design System", "User Research / Testes de Usabilidade", "Prototipagem"))
                .requisitosDesejaveis(List.of("Noções de HTML/CSS", "Metodologias Ágeis", "Métricas de Produto (HEART / SUS)"))
                .tempoMinimoExperienciaAnos(4)
                .status(StatusVaga.ABERTA)
                .recrutador(r.get(7))
                .build());

        // 9. QA / Testes Automatizados
        vagas.add(Vaga.builder()
                .titulo("QA Engineer / Especialista em Automação de Testes")
                .descricao("Definição de estratégias de testes, criação de suítes de automação E2E, testes de API e integração com esteiras de CI/CD.")
                .requisitosObrigatorios(List.of("Automação E2E (Cypress ou Playwright)", "Testes de API REST (Postman / RestAssured)", "JavaScript / TypeScript ou Java", "Git"))
                .requisitosDesejaveis(List.of("Testes de Carga / JMeter ou k6", "CI/CD Integration", "BDD / Cucumber"))
                .tempoMinimoExperienciaAnos(3)
                .status(StatusVaga.ABERTA)
                .recrutador(r.get(8))
                .build());

        // 10. Cyber Security & DevSecOps
        vagas.add(Vaga.builder()
                .titulo("Especialista em Cyber Security & DevSecOps")
                .descricao("Garantir a segurança das aplicações, conduzir análise de vulnerabilidades (SAST/DAST), gestão de acessos e conformidade LGPD/ISO 27001.")
                .requisitosObrigatorios(List.of("Segurança de Aplicações (OWASP Top 10)", "SAST / DAST", "Gestão de Identidade & RBAC", "Conformidade LGPD"))
                .requisitosDesejaveis(List.of("Certificação de Segurança (Security+, CEH)", "Hardening de Containers e Kubernetes", "SIEM"))
                .tempoMinimoExperienciaAnos(4)
                .status(StatusVaga.ABERTA)
                .recrutador(r.get(9))
                .build());

        // 11. Backend Python FastAPI
        vagas.add(Vaga.builder()
                .titulo("Desenvolvedor Backend Python / FastAPI Pleno")
                .descricao("Construção de APIs assíncronas de alto desempenho, integração com microserviços e bancos relacionais/NoSQL.")
                .requisitosObrigatorios(List.of("Python 3.10+", "FastAPI ou Django", "SQLAlchemy / PostgreSQL", "Docker", "PyTest"))
                .requisitosDesejaveis(List.of("Redis / Celery", "RabbitMQ", "Poetry", "AsyncIO"))
                .tempoMinimoExperienciaAnos(3)
                .status(StatusVaga.ABERTA)
                .recrutador(r.get(0))
                .build());

        // 12. Golang Microservices
        vagas.add(Vaga.builder()
                .titulo("Engenheiro de Software Golang (Microsserviços)")
                .descricao("Desenvolvimento de microsserviços de altíssima concorrência e baixa latência para processamento financeiro.")
                .requisitosObrigatorios(List.of("Go (Golang)", "Concorrência (Goroutines / Channels)", "gRPC / REST", "PostgreSQL ou MongoDB"))
                .requisitosDesejaveis(List.of("Kafka", "Docker & Kubernetes", "Observabilidade (OpenTelemetry)"))
                .tempoMinimoExperienciaAnos(3)
                .status(StatusVaga.ABERTA)
                .recrutador(r.get(1))
                .build());

        // 13. Tech Lead
        vagas.add(Vaga.builder()
                .titulo("Tech Lead / Líder Técnico de Engenharia")
                .descricao("Liderança técnica de squad ágil, mentoria de desenvolvedores, desenho de arquitetura de software e governança técnica.")
                .requisitosObrigatorios(List.of("Experiência sólida em arquitetura de software", "Liderança de times técnicos", "Java ou Node.js ou Python", "Design Patterns"))
                .requisitosDesejaveis(List.of("Metodologias Ágeis (Scrum/Kanban)", "Cloud Architecture", "Gestão de Débito Técnico"))
                .tempoMinimoExperienciaAnos(6)
                .status(StatusVaga.ABERTA)
                .recrutador(r.get(2))
                .build());

        // 14. Product Manager
        vagas.add(Vaga.builder()
                .titulo("Product Manager (PM) - Plataformas Digitais")
                .descricao("Definição de visão de produto, discovery contínuo, priorização de backlog, análise de métricas de negócio e alinhamento com stakeholders.")
                .requisitosObrigatorios(List.of("Product Discovery", "Gestão de Backlog & Roadmap", "Métricas de Produto (Retention, Churn, NPS)", "Scrum"))
                .requisitosDesejaveis(List.of("Conhecimento técnico de APIs", "Jira / Confluence", "Data-driven decision making"))
                .tempoMinimoExperienciaAnos(4)
                .status(StatusVaga.ABERTA)
                .recrutador(r.get(4))
                .build());

        // 15. iOS Swift
        vagas.add(Vaga.builder()
                .titulo("Desenvolvedor iOS Swift / SwiftUI Pleno")
                .descricao("Criação de aplicativos nativos para o ecossistema Apple com foco em usabilidade, estabilidade e conformidade com as diretrizes da Apple.")
                .requisitosObrigatorios(List.of("Swift", "SwiftUI e UIKit", "Consumo de APIs REST", "Arquitetura MVVM", "Git"))
                .requisitosDesejaveis(List.of("Combine", "Testes Unitários com XCTest", "Publicação na App Store"))
                .tempoMinimoExperienciaAnos(3)
                .status(StatusVaga.ABERTA)
                .recrutador(r.get(6))
                .build());

        return vagas;
    }

    private void criarCandidaturasComAnalise(List<Vaga> vagas, List<Usuario> candidatos) {
        String dummyPdfPath = new File(storageProperties.getUploadDir(), "curriculo-modelo.pdf").getAbsolutePath();
        long dummyPdfSize = 85000L;

        // Vaga 0: Java Sênior (Recrutadora Ana Beatriz)
        Vaga vagaJava = vagas.get(0);
        Usuario recrutadorAna = vagaJava.getRecrutador();

        // Candidato 1: Lucas Santos -> Selecionado para Entrevista (Score 94%)
        Candidatura c1 = Candidatura.builder()
                .vaga(vagaJava)
                .candidato(candidatos.get(0))
                .caminhoCurriculoPdf(dummyPdfPath)
                .nomeOriginalArquivo("Curriculo_Lucas_Santos_Java_Senior.pdf")
                .tamanhoArquivoBytes(dummyPdfSize)
                .status(StatusCandidatura.SELECIONADO_ENTREVISTA)
                .build();
        candidaturaRepository.save(c1);

        AnaliseIA a1 = AnaliseIA.builder()
                .candidatura(c1)
                .scoreAderencia(94)
                .resumoExecutivo("Candidato com perfil excepcional para a vaga. Demonstra mais de 6 anos de atuação com Java 21 e ecossistema Spring Boot, além de experiência comprovada em arquitetura de microsserviços e Docker.")
                .requisitosAtendidos(List.of("Java 21", "Spring Boot 3", "PostgreSQL", "Docker", "Arquitetura Limpa", "Kubernetes", "Apache Kafka"))
                .pontosAtencao(List.of("Pouca menção direta a Spring AI no currículo"))
                .tempoExperienciaAnos(6)
                .nivelFormacao("Bacharel em Ciência da Computação")
                .telefoneIdentificado("(11) 98765-4321")
                .emailIdentificado("lucas.santos@email.com")
                .dataProcessamento(LocalDateTime.now().minusDays(2))
                .build();
        analiseIARepository.save(a1);

        AuditoriaAcessoContato aud1 = AuditoriaAcessoContato.builder()
                .candidatura(c1)
                .recrutador(recrutadorAna)
                .acao("LIBERACAO_DADOS_CONTATO_ENTREVISTA")
                .justificativa("Candidato com score de 94% e excelente fit técnico. Convidado para entrevista técnica inicial.")
                .dataAcesso(LocalDateTime.now().minusDays(1))
                .build();
        auditoriaRepository.save(aud1);

        // Candidato 2: Beatriz Costa -> Concluído (Score 86%)
        Candidatura c2 = Candidatura.builder()
                .vaga(vagaJava)
                .candidato(candidatos.get(1))
                .caminhoCurriculoPdf(dummyPdfPath)
                .nomeOriginalArquivo("Beatriz_Costa_Curriculo.pdf")
                .tamanhoArquivoBytes(dummyPdfSize)
                .status(StatusCandidatura.CONCLUIDO)
                .build();
        candidaturaRepository.save(c2);

        AnaliseIA a2 = AnaliseIA.builder()
                .candidatura(c2)
                .scoreAderencia(86)
                .resumoExecutivo("Profissional sólida com 5 anos de experiência em desenvolvimento backend Java e PostgreSQL. Atende a todos os requisitos obrigatórios da vaga.")
                .requisitosAtendidos(List.of("Java 21", "Spring Boot 3", "PostgreSQL", "Docker", "Arquitetura Limpa"))
                .pontosAtencao(List.of("Não menciona experiência prévia com Kafka ou mensageria"))
                .tempoExperienciaAnos(5)
                .nivelFormacao("Pós-Graduação em Engenharia de Software")
                .telefoneIdentificado("(21) 99123-8877")
                .emailIdentificado("beatriz.costa@email.com")
                .dataProcessamento(LocalDateTime.now().minusDays(3))
                .build();
        analiseIARepository.save(a2);

        // Candidato 3: Matheus Pereira -> Concluído (Score 72%)
        Candidatura c3 = Candidatura.builder()
                .vaga(vagaJava)
                .candidato(candidatos.get(2))
                .caminhoCurriculoPdf(dummyPdfPath)
                .nomeOriginalArquivo("Matheus_Pereira_Dev.pdf")
                .tamanhoArquivoBytes(dummyPdfSize)
                .status(StatusCandidatura.CONCLUIDO)
                .build();
        candidaturaRepository.save(c3);

        AnaliseIA a3 = AnaliseIA.builder()
                .candidatura(c3)
                .scoreAderencia(72)
                .resumoExecutivo("Candidato pleno com 4 anos de experiência em Java. Atende à maioria dos requisitos obrigatórios, mas experiência em Docker e Arquitetura Limpa ainda é intermediária.")
                .requisitosAtendidos(List.of("Java 21", "Spring Boot 3", "PostgreSQL"))
                .pontosAtencao(List.of("Experiência com Docker básica", "Não detalhou projetos com Arquitetura Limpa"))
                .tempoExperienciaAnos(4)
                .nivelFormacao("Bacharel em Sistemas de Informação")
                .telefoneIdentificado("(31) 98844-2211")
                .emailIdentificado("matheus.pereira@email.com")
                .dataProcessamento(LocalDateTime.now().minusDays(1))
                .build();
        analiseIARepository.save(a3);

        // Candidato 4: Larissa Mendes -> Em Análise (Status EM_ANALISE)
        Candidatura c4 = Candidatura.builder()
                .vaga(vagaJava)
                .candidato(candidatos.get(3))
                .caminhoCurriculoPdf(dummyPdfPath)
                .nomeOriginalArquivo("Curriculo_Larissa_Mendes.pdf")
                .tamanhoArquivoBytes(dummyPdfSize)
                .status(StatusCandidatura.EM_ANALISE)
                .build();
        candidaturaRepository.save(c4);

        // Candidato 5: Gustavo Alves -> Rejeitado (Score 40%)
        Candidatura c5 = Candidatura.builder()
                .vaga(vagaJava)
                .candidato(candidatos.get(4))
                .caminhoCurriculoPdf(dummyPdfPath)
                .nomeOriginalArquivo("Curriculo_Gustavo.pdf")
                .tamanhoArquivoBytes(dummyPdfSize)
                .status(StatusCandidatura.REJEITADO)
                .build();
        candidaturaRepository.save(c5);

        AnaliseIA a5 = AnaliseIA.builder()
                .candidatura(c5)
                .scoreAderencia(40)
                .resumoExecutivo("Candidato em início de carreira com foco principal em PHP e JavaScript. Conhecimento de Java e Spring Boot restrito a projetos acadêmicos.")
                .requisitosAtendidos(List.of("PostgreSQL", "Docker básico"))
                .pontosAtencao(List.of("Não possui tempo mínimo de experiência exigido (5 anos)", "Sem experiência profissional com Spring Boot"))
                .tempoExperienciaAnos(1)
                .nivelFormacao("Cursando Análise e Desenvolvimento de Sistemas")
                .telefoneIdentificado("(41) 97711-3322")
                .emailIdentificado("gustavo.alves@email.com")
                .dataProcessamento(LocalDateTime.now().minusDays(4))
                .build();
        analiseIARepository.save(a5);

        // Vaga 1: Fullstack React + Node (Recrutador Carlos Eduardo)
        Vaga vagaFullstack = vagas.get(1);

        // Candidata 6: Camila Lima -> Selecionado para Entrevista (Score 91%)
        Candidatura c6 = Candidatura.builder()
                .vaga(vagaFullstack)
                .candidato(candidatos.get(5))
                .caminhoCurriculoPdf(dummyPdfPath)
                .nomeOriginalArquivo("Camila_Lima_Fullstack.pdf")
                .tamanhoArquivoBytes(dummyPdfSize)
                .status(StatusCandidatura.SELECIONADO_ENTREVISTA)
                .build();
        candidaturaRepository.save(c6);

        AnaliseIA a6 = AnaliseIA.builder()
                .candidatura(c6)
                .scoreAderencia(91)
                .resumoExecutivo("Excelente perfil fullstack. 4 anos desenvolvendo aplicações com TypeScript, React e Node.js. Grande familiaridade com Next.js e TailwindCSS.")
                .requisitosAtendidos(List.of("TypeScript", "React", "Node.js", "REST APIs", "Git", "Next.js", "TailwindCSS"))
                .pontosAtencao(List.of("Pouca menção a testes de integração com Vitest"))
                .tempoExperienciaAnos(4)
                .nivelFormacao("Bacharel em Engenharia de Computação")
                .telefoneIdentificado("(11) 97654-1234")
                .emailIdentificado("camila.rodrigues@email.com")
                .dataProcessamento(LocalDateTime.now().minusDays(2))
                .build();
        analiseIARepository.save(a6);

        AuditoriaAcessoContato aud6 = AuditoriaAcessoContato.builder()
                .candidatura(c6)
                .recrutador(vagaFullstack.getRecrutador())
                .acao("LIBERACAO_DADOS_CONTATO_ENTREVISTA")
                .justificativa("Candidata de alto nível para o squad web. Contato liberado para agendamento de bate-papo com o Tech Lead.")
                .dataAcesso(LocalDateTime.now().minusHours(12))
                .build();
        auditoriaRepository.save(aud6);

        // Candidato 7: Felipe Ribeiro -> Concluído (Score 80%)
        Candidatura c7 = Candidatura.builder()
                .vaga(vagaFullstack)
                .candidato(candidatos.get(6))
                .caminhoCurriculoPdf(dummyPdfPath)
                .nomeOriginalArquivo("Felipe_Ribeiro_CV.pdf")
                .tamanhoArquivoBytes(dummyPdfSize)
                .status(StatusCandidatura.CONCLUIDO)
                .build();
        candidaturaRepository.save(c7);

        AnaliseIA a7 = AnaliseIA.builder()
                .candidatura(c7)
                .scoreAderencia(80)
                .resumoExecutivo("Perfil equilibrado com 3 anos de experiência em React e Node.js. Boa capacidade de entrega e histórico em startups.")
                .requisitosAtendidos(List.of("TypeScript", "React", "Node.js", "REST APIs", "Git"))
                .pontosAtencao(List.of("Desejável GraphQL não contemplado"))
                .tempoExperienciaAnos(3)
                .nivelFormacao("Tecnólogo em Desenvolvimento Web")
                .telefoneIdentificado("(19) 98112-9900")
                .emailIdentificado("felipe.ribeiro@email.com")
                .dataProcessamento(LocalDateTime.now().minusDays(1))
                .build();
        analiseIARepository.save(a7);

        // Vaga 2: Frontend React (Recrutadora Fernanda Torres)
        Vaga vagaFrontend = vagas.get(2);

        // Candidata 8: Amanda Martins -> Concluído (Score 88%)
        Candidatura c8 = Candidatura.builder()
                .vaga(vagaFrontend)
                .candidato(candidatos.get(7))
                .caminhoCurriculoPdf(dummyPdfPath)
                .nomeOriginalArquivo("Amanda_Martins_Frontend.pdf")
                .tamanhoArquivoBytes(dummyPdfSize)
                .status(StatusCandidatura.CONCLUIDO)
                .build();
        candidaturaRepository.save(c8);

        AnaliseIA a8 = AnaliseIA.builder()
                .candidatura(c8)
                .scoreAderencia(88)
                .resumoExecutivo("Especialista em interfaces com React e TypeScript. Grande domínio de CSS moderno, componentização no Figma e Next.js.")
                .requisitosAtendidos(List.of("React 18+", "TypeScript", "HTML5 Semântico", "CSS3 / Sass", "Consumo de APIs REST", "Next.js 14", "Figma"))
                .pontosAtencao(List.of("Não detalha uso de Cypress para testes E2E"))
                .tempoExperienciaAnos(4)
                .nivelFormacao("Bacharel em Design Digital")
                .telefoneIdentificado("(48) 99887-6543")
                .emailIdentificado("amanda.martins@email.com")
                .dataProcessamento(LocalDateTime.now().minusHours(8))
                .build();
        analiseIARepository.save(a8);

        // Vaga 4: Data Science / AI (Recrutadora Mariana Albuquerque)
        Vaga vagaData = vagas.get(4);

        // Candidato 9: Bruno Carvalho -> Concluído (Score 95%)
        Candidatura c9 = Candidatura.builder()
                .vaga(vagaData)
                .candidato(candidatos.get(8))
                .caminhoCurriculoPdf(dummyPdfPath)
                .nomeOriginalArquivo("Bruno_Carvalho_DataScientist.pdf")
                .tamanhoArquivoBytes(dummyPdfSize)
                .status(StatusCandidatura.SELECIONADO_ENTREVISTA)
                .build();
        candidaturaRepository.save(c9);

        AnaliseIA a9 = AnaliseIA.builder()
                .candidatura(c9)
                .scoreAderencia(95)
                .resumoExecutivo("Cientista de dados sênior com sólida formação matemática e mestrado. Forte atuação em Machine Learning clássico e soluções com LLMs.")
                .requisitosAtendidos(List.of("Python", "Pandas / NumPy", "Scikit-Learn", "SQL Avançado", "Machine Learning", "LLMs / LangChain / Gemini", "PyTorch", "Docker"))
                .pontosAtencao(List.of("Nenhuma lacuna crítica identificada"))
                .tempoExperienciaAnos(5)
                .nivelFormacao("Mestrado em Inteligência Artificial")
                .telefoneIdentificado("(11) 98822-1144")
                .emailIdentificado("bruno.carvalho@email.com")
                .dataProcessamento(LocalDateTime.now().minusDays(1))
                .build();
        analiseIARepository.save(a9);

        AuditoriaAcessoContato aud9 = AuditoriaAcessoContato.builder()
                .candidatura(c9)
                .recrutador(vagaData.getRecrutador())
                .acao("LIBERACAO_DADOS_CONTATO_ENTREVISTA")
                .justificativa("Perfil altamente compatível com o projeto de IA generativa da empresa.")
                .dataAcesso(LocalDateTime.now().minusHours(5))
                .build();
        auditoriaRepository.save(aud9);

        // Candidata 10: Jéssica Gomes -> Concluído (Score 78%)
        Candidatura c10 = Candidatura.builder()
                .vaga(vagaData)
                .candidato(candidatos.get(9))
                .caminhoCurriculoPdf(dummyPdfPath)
                .nomeOriginalArquivo("Jessica_Gomes_CV.pdf")
                .tamanhoArquivoBytes(dummyPdfSize)
                .status(StatusCandidatura.CONCLUIDO)
                .build();
        candidaturaRepository.save(c10);

        AnaliseIA a10 = AnaliseIA.builder()
                .candidatura(c10)
                .scoreAderencia(78)
                .resumoExecutivo("Experiência consistente em análise de dados, SQL e modelagem estatística com Python. Aprendendo frameworks de LLMs recentemente.")
                .requisitosAtendidos(List.of("Python", "Pandas / NumPy", "Scikit-Learn", "SQL Avançado"))
                .pontosAtencao(List.of("Pouca experiência prática com PyTorch e MLOps"))
                .tempoExperienciaAnos(3)
                .nivelFormacao("Bacharel em Estatística")
                .telefoneIdentificado("(31) 97755-4433")
                .emailIdentificado("jessica.gomes@email.com")
                .dataProcessamento(LocalDateTime.now().minusDays(2))
                .build();
        analiseIARepository.save(a10);
    }
}
