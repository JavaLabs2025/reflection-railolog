package org.example;

import org.example.classes.*;
import org.example.generator.Generator;

public class GenerateExample {
    public static void main(String[] args) {
        var gen = new Generator();
        
        System.out.println("=== Testing Generator ===");

        try {
            System.out.println("\n1. Generating Example:");
            Object example = gen.generateValueOfType(Example.class);
            System.out.println("Generated: " + example);
        } catch (Exception e) {
            System.err.println("Error generating Example: " + e.getMessage());
        }

        try {
            System.out.println("\n2. Generating Shape:");
            Object shape = gen.generateValueOfType(Shape.class);
            System.out.println("Generated: " + shape);
            if (shape instanceof Shape s) {
                System.out.println("Area: " + s.getArea());
                System.out.println("Perimeter: " + s.getPerimeter());
            }
        } catch (Exception e) {
            System.err.println("Error generating Shape: " + e.getMessage());
        }

        try {
            System.out.println("\n3. Generating Product:");
            Object product = gen.generateValueOfType(Product.class);
            System.out.println("Generated: " + product);
        } catch (Exception e) {
            System.err.println("Error generating Product: " + e.getMessage());
        }

        try {
            System.out.println("\n4. Generating Rectangle:");
            Object rectangle = gen.generateValueOfType(Rectangle.class);
            System.out.println("Generated: " + rectangle);
            if (rectangle instanceof Rectangle r) {
                System.out.println("Area: " + r.getArea());
                System.out.println("Perimeter: " + r.getPerimeter());
            }
        } catch (Exception e) {
            System.err.println("Error generating Rectangle: " + e.getMessage());
        }

        try {
            System.out.println("\n5. Generating Triangle:");
            Object triangle = gen.generateValueOfType(Triangle.class);
            System.out.println("Generated: " + triangle);
            if (triangle instanceof Triangle t) {
                System.out.println("Area: " + t.getArea());
                System.out.println("Perimeter: " + t.getPerimeter());
            }
        } catch (Exception e) {
            System.err.println("Error generating Triangle: " + e.getMessage());
        }

        try {
            System.out.println("\n6. Generating Cart:");
            Object cart = gen.generateValueOfType(Cart.class);
            System.out.println("Generated: " + cart);
        } catch (Exception e) {
            System.err.println("Error generating Cart: " + e.getMessage());
        }

        try {
            System.out.println("\n7. Generating BinaryTreeNode:");
            Object node = gen.generateValueOfType(BinaryTreeNode.class);
            System.out.println("Generated: " + node);
        } catch (Exception e) {
            System.err.println("Error generating BinaryTreeNode: " + e.getMessage());
        }
    }
}