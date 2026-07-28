package dravin.com.authentication.configuration.apiDocument;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SwaggerApiDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("We test swagger api document test")
    void shouldGenerateOpenApiDocument() throws Exception {

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title")
                        .value("Demo Application build by Dravin Palekar"))
                .andExpect(jsonPath("$.info.version")
                        .value("0.0.1-SNAPSHOT"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.type")
                        .value("http"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme")
                        .value("bearer"));
    }
}
