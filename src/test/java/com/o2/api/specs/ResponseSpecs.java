package com.o2.api.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;

public class ResponseSpecs {

    private ResponseSpecs() {
        // utility class
    }

    public static ResponseSpecification okResponse() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectBody(Matchers.notNullValue())
                .build();
    }

    public static ResponseSpecification createdResponse() {
        return new ResponseSpecBuilder()
                .expectStatusCode(201)
                .expectBody(Matchers.notNullValue())
                .build();
    }

    public static ResponseSpecification notFoundResponse() {
        return new ResponseSpecBuilder()
                .expectStatusCode(404)
                .build();
    }
}