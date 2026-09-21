package com.stric.recrutamento;

import com.stric.recrutamento.service.DataMaskingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataMaskingServiceTest {

    private final DataMaskingService maskingService = new DataMaskingService();

    @Test
    @DisplayName("Deve mascarar endereço de e-mail conforme LGPD")
    void deveMascararEmail() {
        String original = "lucas.silva@empresa.com.br";
        String masked = maskingService.maskEmail(original);

        assertNotNull(masked);
        assertNotEquals(original, masked);
        assertTrue(masked.startsWith("l***"));
        assertTrue(masked.contains("@***."));
    }

    @Test
    @DisplayName("Deve mascarar telefone celular brasileiro padrão")
    void deveMascararTelefone() {
        String original = "(11) 98765-4321";
        String masked = maskingService.maskPhone(original);

        assertNotNull(masked);
        assertNotEquals(original, masked);
        assertTrue(masked.contains("(11)"));
        assertTrue(masked.contains("*"));
        assertTrue(masked.endsWith("21"));
    }

    @Test
    @DisplayName("Deve abreviar nome completo para privacidade LGPD")
    void deveMascararNomeCompleto() {
        String original = "Ana Clara Fernandes Souza";
        String masked = maskingService.maskName(original);

        assertNotNull(masked);
        assertEquals("Ana C. F. S.", masked);
    }
}
