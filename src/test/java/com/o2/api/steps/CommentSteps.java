package com.o2.api.steps;

import com.o2.api.specs.RequestSpecs;
import io.restassured.response.Response;
import net.serenitybdd.annotations.Step;

import static io.restassured.RestAssured.given;

public class CommentSteps {

    @Step("Get comments for post with id {0} from /posts/{0}/comments")
    public Response getCommentsForPost(int postId) {
        return given()
                .spec(RequestSpecs.plainRequest())
                .when()
                .get("/posts/{postId}/comments", postId)
                .then()
                .extract()
                .response();
    }
}