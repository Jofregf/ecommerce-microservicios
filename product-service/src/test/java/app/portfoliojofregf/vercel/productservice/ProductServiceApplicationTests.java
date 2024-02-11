package app.portfoliojofregf.vercel.productservice;

import app.portfoliojofregf.vercel.productservice.dto.ProductRequest;
import app.portfoliojofregf.vercel.productservice.dto.ProductResponse;
import app.portfoliojofregf.vercel.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    //	@DirtiesContext
    @Test
    void shouldCreateProduct() throws Exception {
        List<ProductRequest> productRequests = Arrays.asList(
                getProductRequest("Producto1", "Descripcion1", BigDecimal.valueOf(100)),
                getProductRequest("Producto2", "Descripcion2", BigDecimal.valueOf(500)),
                getProductRequest("Producto3", "Descripcion3", BigDecimal.valueOf(1000))
        );

        for(ProductRequest product:productRequests){
            String productRequestString = objectMapper.writeValueAsString(product);
            mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(productRequestString))
                    .andExpect(status().isCreated());
        }
        Assertions.assertEquals(3, productRepository.findAll().size());
        Assertions.assertEquals(201, HttpStatus.CREATED.value());
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<ProductResponse> productResponses = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<ProductResponse>>() {});

        Assertions.assertEquals(3, productResponses.size());
        Assertions.assertEquals("Producto3", productResponses.get(2).getName());
    }

    private ProductRequest getProductRequest(String name, String description, BigDecimal price) {
        return ProductRequest.builder()
                .name(name)
                .description(description)
                .price(price)
                .build();
    }
}
