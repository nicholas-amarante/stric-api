package com.stric.recrutamento.service;

import com.stric.recrutamento.domain.entities.AnaliseIA;
import com.stric.recrutamento.domain.entities.Candidatura;
import com.stric.recrutamento.domain.enums.StatusCandidatura;
import com.stric.recrutamento.dto.candidatura.CandidaturaMaskedDTO;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Serviço de mascaramento de dados sensíveis para conformidade com a LGPD (Lei Geral de Proteção de Dados).
 */
@Service
public class DataMaskingService {

    private static final Pattern PHONE_PATTERN = Pattern.compile("(\\+?\\d{1,3})?\\s*\\(?(\\d{2})\\)?\\s*(\\d{4,5})[-.\\s]?(\\d{4})");

    /**
     * Mascara um endereço de e-mail (ex: joao.silva@gmail.com -> j***a@***.com).
     */
    public String maskEmail(String email) {
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            return "***@***.com";
        }
        String cleanEmail = email.trim();
        String[] parts = cleanEmail.split("@", 2);
        String username = parts[0];
        String domain = parts[1];

        String maskedUser;
        if (username.length() <= 2) {
            maskedUser = username.charAt(0) + "***";
        } else {
            maskedUser = username.charAt(0) + "***" + username.charAt(username.length() - 1);
        }

        String maskedDomain;
        int dotIndex = domain.lastIndexOf('.');
        if (dotIndex > 0) {
            String suffix = domain.substring(dotIndex);
            maskedDomain = "***" + suffix;
        } else {
            maskedDomain = "***.com";
        }

        return maskedUser + "@" + maskedDomain;
    }

    /**
     * Mascara número de telefone celular ou fixo (ex: (11) 98765-4321 -> (11) 9****-**21 ou (11) 9****-**34).
     */
    public String maskPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "(**) 9****-****";
        }
        String clean = phone.trim();
        Matcher matcher = PHONE_PATTERN.matcher(clean);

        if (matcher.find()) {
            String ddd = matcher.group(2);
            String firstPart = matcher.group(3);
            String lastPart = matcher.group(4);

            String maskedFirst = firstPart.charAt(0) + "*".repeat(Math.max(1, firstPart.length() - 1));
            String maskedLast = "*".repeat(Math.max(1, lastPart.length() - 2)) + lastPart.substring(Math.max(0, lastPart.length() - 2));

            return String.format("(%s) %s-%s", ddd, maskedFirst, maskedLast);
        }

        // Caso o telefone não case com o regex padrão brasileiro, mascara genericamente
        String digitsOnly = clean.replaceAll("\\D", "");
        if (digitsOnly.length() >= 8) {
            int len = digitsOnly.length();
            String start = digitsOnly.substring(0, Math.min(2, len));
            String end = digitsOnly.substring(Math.max(0, len - 2));
            return String.format("(%s) 9****-**%s", start, end);
        }

        return "(**) 9****-****";
    }

    /**
     * Mascara nome de candidato (ex: Lucas Silva Santos -> Lucas S. S.).
     */
    public String maskName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Candidato";
        }
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0];
        }
        StringBuilder sb = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                sb.append(" ").append(parts[i].toUpperCase().charAt(0)).append(".");
            }
        }
        return sb.toString();
    }

    /**
     * Converte a entidade Candidatura para o DTO mascarado para visualização em lote do recrutador.
     */
    public CandidaturaMaskedDTO toMaskedDTO(Candidatura candidatura) {
        if (candidatura == null) return null;

        boolean isLiberado = candidatura.getStatus() == StatusCandidatura.SELECIONADO_ENTREVISTA;
        AnaliseIA analise = candidatura.getAnaliseIA();

        String rawPhone = analise != null && analise.getTelefoneIdentificado() != null ?
                analise.getTelefoneIdentificado() : "";
        String rawEmail = candidatura.getCandidato() != null ? candidatura.getCandidato().getEmail() : "";
        String rawName = candidatura.getCandidato() != null ? candidatura.getCandidato().getNome() : "";

        return CandidaturaMaskedDTO.builder()
                .id(candidatura.getId())
                .vagaId(candidatura.getVaga().getId())
                .tituloVaga(candidatura.getVaga().getTitulo())
                .candidatoId(candidatura.getCandidato().getId())
                .nomeCandidatoMascarado(isLiberado ? rawName : maskName(rawName))
                .emailCandidatoMascarado(isLiberado ? rawEmail : maskEmail(rawEmail))
                .telefoneMascarado(isLiberado ? rawPhone : maskPhone(rawPhone))
                .dadosContatoLiberados(isLiberado)
                .status(candidatura.getStatus())
                .scoreAderencia(analise != null ? analise.getScoreAderencia() : null)
                .resumoExecutivo(analise != null ? analise.getResumoExecutivo() : null)
                .requisitosAtendidos(analise != null ? analise.getRequisitosAtendidos() : null)
                .pontosAtencao(analise != null ? analise.getPontosAtencao() : null)
                .tempoExperienciaAnos(analise != null ? analise.getTempoExperienciaAnos() : null)
                .nivelFormacao(analise != null ? analise.getNivelFormacao() : null)
                .nomeOriginalArquivo(candidatura.getNomeOriginalArquivo())
                .tamanhoArquivoBytes(candidatura.getTamanhoArquivoBytes())
                .dataSubmissao(candidatura.getDataSubmissao())
                .build();
    }
}
