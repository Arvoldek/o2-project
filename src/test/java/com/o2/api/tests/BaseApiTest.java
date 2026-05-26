package com.o2.api.tests;

import com.o2.api.config.ConfigProvider;
import io.restassured.RestAssured;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SerenityJUnit5Extension.class)
public abstract class BaseApiTest {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = ConfigProvider.getBaseUrl();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}