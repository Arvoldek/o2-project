package com.o2.api.tests;

import com.o2.api.factory.PostFactory;
import com.o2.api.model.Post;
import com.o2.api.steps.PostSteps;
import io.restassured.response.Response;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.Title;
import net.serenitybdd.core.Serenity;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.*;

class PostsApiTest extends BaseApiTest {

    @Steps
    PostSteps postSteps;

    // ------------------------------------------------------------------
    // TC-01: Get list of posts
    // ------------------------------------------------------------------
    @Tag("smoke")
    @Test
    @Title("TC-01: GET /posts returns 200, 100 posts, each with id/userId/title/body")
    void shouldGetAllPosts() {
        Response response = postSteps.getAllPosts();

        Serenity.recordReportData()
                .withTitle("Response Status")
                .andContents("Status: " + response.getStatusCode());

        List<Post> posts = response.jsonPath().getList("", Post.class);

        assertAll("TC-01 - Get all posts",
                () -> assertEquals(200, response.getStatusCode(), "Status code should be 200"),
                () -> assertEquals(100, posts.size(), "Should return exactly 100 posts"),
                () -> assertTrue(posts.stream().allMatch(p -> p.getId() != null), "Every post must have an id"),
                () -> assertTrue(posts.stream().allMatch(p -> p.getUserId() != null), "Every post must have a userId"),
                () -> assertTrue(posts.stream().allMatch(p -> p.getTitle() != null && !p.getTitle().isEmpty()), "Every post must have a non-empty title"),
                () -> assertTrue(posts.stream().allMatch(p -> p.getBody() != null && !p.getBody().isEmpty()), "Every post must have a non-empty body")
        );
    }

    // ------------------------------------------------------------------
    // TC-02: Get single post
    // ------------------------------------------------------------------
    @Tag("smoke")
    @Test
    @Title("TC-02: GET /posts/1 returns 200, id=1, non-empty title")
    void shouldGetSinglePost() {
        Response response = postSteps.getPostById(1);

        Serenity.recordReportData()
                .withTitle("Response Body")
                .andContents(response.asPrettyString());

        Post post = response.as(Post.class);

        response.then().assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/post-schema.json"));

        assertAll("TC-02 - Get single post",
                () -> assertEquals(200, response.getStatusCode(), "Status code should be 200"),
                () -> assertEquals(1, post.getId(), "Post id should be 1"),
                () -> assertNotNull(post.getTitle(), "Title should not be null"),
                () -> assertFalse(post.getTitle().isEmpty(), "Title should not be empty")
        );
    }

    // ------------------------------------------------------------------
    // TC-03: Get non-existent post
    // ------------------------------------------------------------------
    @Tag("negative")
    @Test
    @Title("TC-03: GET /posts/9999 returns 404")
    void shouldReturn404ForNonExistentPost() {
        Response response = postSteps.getPostById(9999);

        assertEquals(404, response.getStatusCode(), "Status code should be 404 for a non-existent post");
    }

    // ------------------------------------------------------------------
    // TC-03 (parameterized): Multiple invalid post IDs
    // ------------------------------------------------------------------
    @Tag("negative")
    @ParameterizedTest(name = "GET /posts/{0} should return 404")
    @ValueSource(ints = {9999, 0, -1})
    @Title("TC-03 (param): GET /posts/{id} with invalid id returns 404")
    void shouldReturn404ForInvalidPostId(int id) {
        Response response = postSteps.getPostById(id);

        assertEquals(404, response.getStatusCode(),
                "Status code should be 404 for post id=" + id);
    }

    // ------------------------------------------------------------------
    // TC-04: Create a new post
    // ------------------------------------------------------------------
    @Tag("regression")
    @Test
    @Title("TC-04: POST /posts returns 201, generated id, title matches request")
    void shouldCreateNewPost() {
        Post newPost = PostFactory.validPost();
        Response response = postSteps.createPost(newPost);

        Serenity.recordReportData()
                .withTitle("Response Body")
                .andContents(response.asPrettyString());

        Post created = response.as(Post.class);

        assertAll("TC-04 - Create new post",
                () -> assertEquals(201, response.getStatusCode(), "Status code should be 201"),
                () -> assertNotNull(created.getId(), "Created post should have a generated id"),
                () -> assertEquals(newPost.getTitle(), created.getTitle(), "Title in response should match sent title")
        );
    }

    // ------------------------------------------------------------------
    // TC-05: Update post (PUT)
    // ------------------------------------------------------------------
    @Tag("regression")
    @Test
    @Title("TC-05: PUT /posts/1 returns 200 with updated title")
    void shouldUpdatePost() {
        Post update = PostFactory.updatedPost();
        Response response = postSteps.updatePost(1, update);

        Serenity.recordReportData()
                .withTitle("Response Body")
                .andContents(response.asPrettyString());

        Post updated = response.as(Post.class);

        assertAll("TC-05 - Update post",
                () -> assertEquals(200, response.getStatusCode(), "Status code should be 200"),
                () -> assertEquals("Updated Title", updated.getTitle(), "Title should be 'Updated Title'")
        );
    }

    // ------------------------------------------------------------------
    // TC-06: Partial update post (PATCH)
    // ------------------------------------------------------------------
    @Tag("regression")
    @Test
    @Title("TC-06: PATCH /posts/1 returns 200 with patched title")
    void shouldPatchPost() {
        Post patch = PostFactory.patchedPost();
        Response response = postSteps.patchPost(1, patch);

        Serenity.recordReportData()
                .withTitle("Response Body")
                .andContents(response.asPrettyString());

        Post patched = response.as(Post.class);

        assertAll("TC-06 - Patch post",
                () -> assertEquals(200, response.getStatusCode(), "Status code should be 200"),
                () -> assertEquals("Patched Title", patched.getTitle(), "Title should be 'Patched Title'")
        );
    }

    // ------------------------------------------------------------------
    // TC-07: Delete post
    // ------------------------------------------------------------------
    @Tag("regression")
    @Test
    @Title("TC-07: DELETE /posts/1 returns 200")
    void shouldDeletePost() {
        Response response = postSteps.deletePost(1);

        assertEquals(200, response.getStatusCode(), "Status code should be 200 after deletion");
    }
}