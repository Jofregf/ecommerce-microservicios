package app.portfoliojofregf.vercel.productservice.repository;

import app.portfoliojofregf.vercel.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
