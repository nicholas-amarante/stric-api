package com.stric.recrutamento.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;

class CorsConfigTest {

    @Test
    @DisplayName("Deve permitir origens da Vercel e preview deployments no CORS")
    void devePermitirOrigensVercel() {
        SecurityConfig securityConfig = new SecurityConfig(null, null);
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();

        MockHttpServletRequest request1 = new MockHttpServletRequest("GET", "/api/vagas");
        request1.addHeader("Origin", "https://stric-web.vercel.app");
        CorsConfiguration config1 = source.getCorsConfiguration(request1);

        assertNotNull(config1, "CORS config não deve ser nulo");
        assertEquals("https://stric-web.vercel.app", config1.checkOrigin("https://stric-web.vercel.app"));

        MockHttpServletRequest request2 = new MockHttpServletRequest("GET", "/api/vagas");
        request2.addHeader("Origin", "https://stric-5a5a6i1m9-nicholas-e47c.vercel.app");
        CorsConfiguration config2 = source.getCorsConfiguration(request2);

        assertNotNull(config2, "CORS config não deve ser nulo");
        assertEquals("https://stric-5a5a6i1m9-nicholas-e47c.vercel.app",
                config2.checkOrigin("https://stric-5a5a6i1m9-nicholas-e47c.vercel.app"));

        MockHttpServletRequest request3 = new MockHttpServletRequest("GET", "/api/vagas");
        request3.addHeader("Origin", "https://preview-branch.vercel.app");
        CorsConfiguration config3 = source.getCorsConfiguration(request3);

        assertNotNull(config3, "CORS config não deve ser nulo");
        assertEquals("https://preview-branch.vercel.app", config3.checkOrigin("https://preview-branch.vercel.app"));
    }
}
