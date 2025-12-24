package org.example.classes;

import java.util.Map;

import lombok.ToString;
import org.example.generator.Generatable;

@ToString
@Generatable
public class Example {
    int i;
    public Map<Cart, Product> myMap;

    public Example(int i) {
        this.i = i;
    }
}
