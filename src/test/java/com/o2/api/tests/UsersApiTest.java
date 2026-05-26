package com.o2.api.tests;

import com.o2.api.model.User;
import com.o2.api.steps.UserSteps;
import io.restassured.response.Response;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.Title;
import net.serenitybdd.core.Serenity;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.*;

class UsersApiTest extends BaseApiTest {

    @Steps
    UserSteps userSteps;

    // ------------------------------------------------------------------
    // TC-09: Get all users and verify address structure
    // ------------------------------------------------------------------
    @Tag("smoke")
    @Test
    @Title("TC-09: GET /users returns 200, each user has nested address with city")
    void shouldGetAllUsersWithAddressStructure() {
        Response response = userSteps.getAllUsers();

        Serenity.recordReportData()
                .withTitle("Response Body")
                .andContents(response.asPrettyString());

        List<User> users = response.jsonPath().getList("", User.class);

        assertAll("TC-09 - Get all users",
                () -> assertEquals(200, response.getStatusCode(), "Status code should be 200"),
                () -> assertFalse(users.isEmpty(), "Users list should not be empty"),
                () -> assertTrue(
                        users.stream().allMatch(u -> u.getAddress() != null),
                        "Every user must have an address object"
                ),
                () -> assertTrue(
                        users.stream().allMatch(u -> u.getAddress() != null
                                && u.getAddress().getCity() != null
                                && !u.getAddress().getCity().isEmpty()),
                        "Every user's address must have a non-empty city"
                )
        );

        // JsonPath alternative: validate cities extracted directly
        List<String> cities = response.jsonPath().getList("address.city");
        assertTrue(cities.stream().allMatch(city -> city != null && !city.isEmpty()),
                "All cities should be non-empty strings");
    }

    // ------------------------------------------------------------------
    // TC-10: Filter user by username query param
    // ------------------------------------------------------------------
    @Tag("smoke")
    @Test
    @Title("TC-10: GET /users?username=Bret returns 200, exactly 1 user, username='Bret'")
    void shouldFilterUserByUsername() {
        Response response = userSteps.getUsersByUsername("Bret");

        Serenity.recordReportData()
                .withTitle("Response Body")
                .andContents(response.asPrettyString());

        List<User> users = response.jsonPath().getList("", User.class);

        assertAll("TC-10 - Filter user by username",
                () -> assertEquals(200, response.getStatusCode(), "Status code should be 200"),
                () -> assertEquals(1, users.size(), "Should return exactly 1 user for username='Bret'"),
                () -> assertEquals("Bret", users.get(0).getUsername(), "Returned user's username should be 'Bret'")
        );
    }
}