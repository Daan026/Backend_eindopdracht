package com.fondsdelecturelibre.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import com.fondsdelecturelibre.utils.JwtUtil;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Arrays;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void contextLoads() {
        // Deze test controleert alleen of de Spring context correct laadt
    }

    @Test
    public void testPublicEndpoint() throws Exception {
        mockMvc.perform(get("/api/test/public"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSecuredEndpointWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testSecuredEndpointWithValidJWT() throws Exception {
        String jwtToken = generateJwtToken("Admin", "ADMIN");
        
        mockMvc.perform(get("/api/categories")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testCreateNewCategory() throws Exception {
        String jwtToken = generateJwtToken("Admin", "ADMIN");
        String categoryJson = "{\"name\":\"Test Categorie\",\"description\":\"Dit is een test categorie\"}";
        
        mockMvc.perform(post("/api/categories")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Categorie"))
                .andExpect(jsonPath("$.description").value("Dit is een test categorie"));
    }

    @Test
    public void testCreateDuplicateCategory() throws Exception {
        String jwtToken = generateJwtToken("Admin", "ADMIN");
        String categoryJson = "{\"name\":\"Duplicate Test\",\"description\":\"First category\"}";
        
        // Eerst een categorie maken
        mockMvc.perform(post("/api/categories")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryJson))
                .andExpect(status().isCreated());
        
        // Dan proberen dezelfde categorie nogmaals te maken (moet 409 Conflict geven)
        mockMvc.perform(post("/api/categories")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryJson))
                .andExpect(status().isConflict());
    }

    @Test
    public void testUpdateCategory() throws Exception {
        String jwtToken = generateJwtToken("Admin", "ADMIN");
        String createCategoryJson = "{\"name\":\"Update Test\",\"description\":\"Original description\"}";
        
        // Eerst een categorie maken
        MvcResult createResult = mockMvc.perform(post("/api/categories")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createCategoryJson))
                .andExpect(status().isCreated())
                .andReturn();
        
        // ID uit response halen
        String responseContent = createResult.getResponse().getContentAsString();
        Long categoryId = JsonPath.parse(responseContent).read("$.id", Long.class);
        
        // Categorie updaten
        String updateCategoryJson = "{\"name\":\"Updated Name\",\"description\":\"Updated description\"}";
        mockMvc.perform(put("/api/categories/" + categoryId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateCategoryJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    public void testDeleteCategory() throws Exception {
        String jwtToken = generateJwtToken("Admin", "ADMIN");
        String createCategoryJson = "{\"name\":\"Delete Test\",\"description\":\"Category to be deleted\"}";
        
        // Eerst een categorie maken
        MvcResult createResult = mockMvc.perform(post("/api/categories")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createCategoryJson))
                .andExpect(status().isCreated())
                .andReturn();
        
        // ID uit response halen
        String responseContent = createResult.getResponse().getContentAsString();
        Long categoryId = JsonPath.parse(responseContent).read("$.id", Long.class);
        
        // Categorie verwijderen
        mockMvc.perform(delete("/api/categories/" + categoryId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
        
        // Controleren dat categorie niet meer bestaat (moet 404 geven)
        mockMvc.perform(get("/api/categories/" + categoryId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAdminAccessControl() throws Exception {
        String adminToken = generateJwtToken("Admin", "ADMIN");
        String categoryJson = "{\"name\":\"Admin Test\",\"description\":\"Testing admin access\"}";
        
        // ADMIN kan alle CRUD operaties uitvoeren
        
        // CREATE - ADMIN kan POST requests doen
        MvcResult createResult = mockMvc.perform(post("/api/categories")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Admin Test"))
                .andReturn();
        
        Long categoryId = JsonPath.parse(createResult.getResponse().getContentAsString()).read("$.id", Long.class);
        
        // READ - ADMIN kan GET requests doen
        mockMvc.perform(get("/api/categories")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        
        mockMvc.perform(get("/api/categories/" + categoryId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Admin Test"));
        
        // UPDATE - ADMIN kan PUT requests doen
        String updateJson = "{\"name\":\"Updated Admin Test\",\"description\":\"Updated by admin\"}";
        mockMvc.perform(put("/api/categories/" + categoryId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Admin Test"));
        
        // DELETE - ADMIN kan DELETE requests doen
        mockMvc.perform(delete("/api/categories/" + categoryId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testEBookPagination() throws Exception {
        String adminToken = generateJwtToken("Admin", "ADMIN");
        
        // Test pagination voor /api/ebooks endpoint
        mockMvc.perform(get("/api/ebooks")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .param("page", "0")
                .param("size", "5")
                .param("sort", "title,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    public void testEBookSearchPagination() throws Exception {
        String adminToken = generateJwtToken("Admin", "ADMIN");
        
        // Test pagination voor /api/ebooks/search endpoint
        mockMvc.perform(get("/api/ebooks/search")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .param("title", "test")
                .param("page", "0")
                .param("size", "3")
                .param("sort", "title,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.size").value(3))
                .andExpect(jsonPath("$.number").value(0));
    }

    private String generateJwtToken(String username, String... roles) {
        UserDetails userDetails = User.builder()
                .username(username)
                .password("password")
                .authorities(Arrays.stream(roles)
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .toArray(SimpleGrantedAuthority[]::new))
                .build();
        return jwtUtil.generateToken(userDetails);
    }
}
