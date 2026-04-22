package com.salihguneyin.stockwise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class StockwiseApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void dashboardEndpointReturnsSeededMetrics() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary.length()").value(4))
                .andExpect(jsonPath("$.recentMovements.length()").value(4));
    }

    @Test
    void createProductAddsANewRecord() throws Exception {
        String payload = """
                {
                  "sku": "HW-303",
                  "name": "POS Tablet",
                  "category": "Hardware",
                  "warehouseZone": "E2",
                  "currentStock": 12,
                  "reorderPoint": 4,
                  "unitPrice": 8999.00,
                  "active": true
                }
                """;

        String response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("HW-303"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response).contains("POS Tablet");
    }
}
