package com.o2.api.factory;

import com.o2.api.model.Post;

public class PostFactory {

    private PostFactory() {
        // utility class
    }

    public static Post validPost() {
        return Post.builder()
                .title("Test Post")
                .body("This is a test body")
                .userId(1)
                .build();
    }

    public static Post postWithMissingTitle() {
        return Post.builder()
                .body("Body without title")
                .userId(1)
                .build();
    }

    public static Post updatedPost() {
        return Post.builder()
                .id(1)
                .title("Updated Title")
                .body("Updated body")
                .userId(1)
                .build();
    }

    public static Post patchedPost() {
        return Post.builder()
                .title("Patched Title")
                .build();
    }
}