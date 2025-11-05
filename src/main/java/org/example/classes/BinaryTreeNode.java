package org.example.classes;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.generator.Generatable;

@Getter
@Setter
@ToString
@Generatable
public class BinaryTreeNode {
    private Integer data;
    private BinaryTreeNode left;
    private BinaryTreeNode right;

    public BinaryTreeNode(Integer data, BinaryTreeNode left, BinaryTreeNode right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }
}
