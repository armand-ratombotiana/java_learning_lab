# Code Deep Dive: Micronaut

## Micronaut Application
```java
@MicronautTest
public class ProductControllerTest {
    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void testGetProduct() {
        Product product = client.toBlocking()
            .retrieve(HttpRequest.GET("/api/products/1"), Product.class);
        assertNotNull(product);
    }
}

@Controller("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Get("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Product getProduct(Long id) {
        return productService.findById(id);
    }

    @Post
    public HttpResponse<Product> create(@Body @Valid ProductCreateRequest request) {
        Product created = productService.create(request);
        return HttpResponse.created(created);
    }
}
```
