package com.o2.api.steps;

import com.o2.api.model.Post;
import com.o2.api.specs.RequestSpecs;
import io.restassured.response.Response;
import net.serenitybdd.annotations.Step;

import static io.restassured.RestAssured.given;

public class PostSteps {

    @Step("Get all posts from /posts")
    public Response getAllPosts() {
        return given()
                .spec(RequestSpecs.plainRequest())
                .when()
                .get("/posts")
                .then()
                .extract()
                .response();
    }

    @Step("Get post with id {0} from /posts/{0}")
    public Response getPostById(int id) {
        return given()
                .spec(RequestSpecs.plainRequest())
                .when()
                .get("/posts/{id}", id)
                .then()
                .extract()
                .response();
    }

    @Step("Create a new post via POST /posts")
    public Response createPost(Post post) {
        return given()
                .spec(RequestSpecs.jsonRequest())
                .body(post)
                .when()
                .post("/posts")
                .then()
                .extract()
                .response();
    }

    @Step("Update post with id {0} via PUT /posts/{0}")
    public Response updatePost(int id, Post post) {
        return given()
                .spec(RequestSpecs.jsonRequest())
                .body(post)
                .when()
                .put("/posts/{id}", id)
                .then()
                .extract()
                .response();
    }

    @Step("Partially update post with id {0} via PATCH /posts/{0}")
    public Response patchPost(int id, Post post) {
        return given()
                .spec(RequestSpecs.jsonRequest())
                .body(post)
                .when()
                .patch("/posts/{id}", id)
                .then()
                .extract()
                .response();
    }

    @Step("Delete post with id {0} via DELETE /posts/{0}")
    public Response deletePost(int id) {
        return given()
                .spec(RequestSpecs.plainRequest())
                .when()
                .delete("/posts/{id}", id)
                .then()
                .extract()
                .response();
    }

    @Step("POST /posts with raw body (negative scenario)")
    public Response createPostWithRawBody(String rawBody) {
        return given()
                .spec(RequestSpecs.jsonRequest())
                .body(rawBody)
                .when()
                .post("/posts")
                .then()
                .extract()
                .response();
    }

    @Step("GET non-existent endpoint '{0}'")
    public Response getEndpoint(String path) {
        return given()
                .spec(RequestSpecs.plainRequest())
                .when()
                .get(path)
                .then()
                .extract()
                .response();
    }
}
