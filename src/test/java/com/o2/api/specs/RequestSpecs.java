package com.o2.api.specs;

import com.o2.api.config.ConfigProvider;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RequestSpecs {

    private RequestSpecs() {
        // utility class
    }

    public static RequestSpecification jsonRequest() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigProvider.getBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    public static RequestSpecification plainRequest() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigProvider.getBaseUrl())
                .setAccept(ContentType.JSON)
                .build();
    }
}