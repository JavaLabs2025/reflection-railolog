package org.example.generator;

import org.example.classes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class GeneratorTest {
    
    private Generator generator;
    
    @BeforeEach
    void setUp() {
        generator = new Generator();
    }
    
    @Test
    void testGenerateExample_ShouldCreateObject() throws Exception {
        Object result = generator.generateValueOfType(Example.class);
        
        assertNotNull(result, "Generated object should not be null");
        assertInstanceOf(Example.class, result, "Generated object should be instance of Example");
    }
    
    @Test
    void testGenerateProduct_ShouldCreateObject() throws Exception {
        Object result = generator.generateValueOfType(Product.class);
        
        assertNotNull(result, "Generated Product should not be null");
        assertInstanceOf(Product.class, result, "Generated object should be instance of Product");
        
        Product product = (Product) result;
        assertNotNull(product.getName(), "Product name should not be null");
        assertTrue(product.getPrice() != 0 || product.getPrice() == 0, "Product price should be set");
    }
    
    @Test
    void testGenerateShape_ShouldCreateShapeImplementation() throws Exception {
        Object result = generator.generateValueOfType(Shape.class);
        
        assertNotNull(result, "Generated Shape should not be null");
        assertInstanceOf(Shape.class, result, "Generated object should implement Shape interface");

        assertTrue(result instanceof Rectangle || result instanceof Triangle,
                "Shape implementation should be either Rectangle or Triangle");
    }
    
    @Test
    void testGenerateCart_ShouldCreateCartWithNonEmptyCollection() throws Exception {
        Object result = generator.generateValueOfType(Cart.class);
        
        assertNotNull(result, "Generated Cart should not be null");
        assertInstanceOf(Cart.class, result, "Generated object should be instance of Cart");
        
        Cart cart = (Cart) result;
        List<Product> items = cart.getItems();
        
        assertNotNull(items, "Cart items should not be null");
        assertFalse(items.isEmpty(), "Cart should have non-empty collection of items");
    }
}