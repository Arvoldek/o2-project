package com.o2.api.steps;

import com.o2.api.specs.RequestSpecs;
import io.restassured.response.Response;
import net.serenitybdd.annotations.Step;

import static io.restassured.RestAssured.given;

public class UserSteps {

    @Step("Get all users from /users")
    public Response getAllUsers() {
        return given()
                .spec(RequestSpecs.plainRequest())
                .when()
                .get("/users")
                .then()
                .extract()
                .response();
    }

    @Step("Get users filtered by username '{0}' from /users?username={0}")
    public Response getUsersByUsername(String username) {
        return given()
                .spec(RequestSpecs.plainRequest())
                .queryParam("username", username)
                .when()
                .get("/users")
                .then()
                .extract()
                .response();
    }
}