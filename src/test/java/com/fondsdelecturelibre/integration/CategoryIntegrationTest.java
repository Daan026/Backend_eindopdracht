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

    @Test
    public void testDashboardEndpointWithoutAuth() throws Exception {
        // Test dat dashboard endpoint authenticatie vereist
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDashboardEndpointWithAuth() throws Exception {
        String adminToken = generateJwtToken("Admin", "ADMIN");
        
        // Test dashboard endpoint met authenticatie
        mockMvc.perform(get("/api/dashboard")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userStatistics").exists())
                .andExpect(jsonPath("$.userStatistics.totalBooksUploaded").exists())
                .andExpect(jsonPath("$.userStatistics.totalReviewsWritten").exists())
                .andExpect(jsonPath("$.userStatistics.booksUploadedThisYear").exists())
                .andExpect(jsonPath("$.recentUploads").isArray())
                .andExpect(jsonPath("$.recentReviews").isArray());
    }

    @Test
    public void testDashboardMemberAccess() throws Exception {
        String memberToken = generateJwtToken("Admin", "MEMBER");
        
        // Test dat MEMBER ook toegang heeft tot dashboard
        mockMvc.perform(get("/api/dashboard")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userStatistics").exists())
                .andExpect(jsonPath("$.recentUploads").isArray())
                .andExpect(jsonPath("$.recentReviews").isArray());
    }

    @Test
    public void testAdvancedSearchByAuthor() throws Exception {
        String adminToken = generateJwtToken("Admin", "ADMIN");
        
        // Test search by author endpoint
        mockMvc.perform(get("/api/ebooks/search/author")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .param("author", "test")
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    public void testAdvancedSearchByCategory() throws Exception {
        String adminToken = generateJwtToken("Admin", "ADMIN");
        
        // Test search by category endpoint
        mockMvc.perform(get("/api/ebooks/search/category")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .param("categoryId", "1")
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.size").value(5));
    }

    @Test
    public void testAdvancedSearchCombined() throws Exception {
        String adminToken = generateJwtToken("Admin", "ADMIN");
        
        // Test advanced search with multiple parameters
        mockMvc.perform(get("/api/ebooks/search/advanced")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .param("title", "test")
                .param("author", "author")
                .param("categoryId", "1")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    public void testAdvancedSearchWithoutAuth() throws Exception {
        // Test dat advanced search endpoints authenticatie vereisen
        mockMvc.perform(get("/api/ebooks/search/author")
                .param("author", "test"))
                .andExpect(status().isForbidden());
                
        mockMvc.perform(get("/api/ebooks/search/category")
                .param("categoryId", "1"))
                .andExpect(status().isForbidden());
                
        mockMvc.perform(get("/api/ebooks/search/advanced")
                .param("title", "test"))
                .andExpect(status().isForbidden());
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
