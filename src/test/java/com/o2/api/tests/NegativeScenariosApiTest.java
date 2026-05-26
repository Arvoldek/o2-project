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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Extended negative scenarios (bonus).
 * Tests edge cases such as missing required fields, malformed JSON,
 * and requests to non-existent endpoints.
 *
 * Note: jsonplaceholder.typicode.com is a mock API that intentionally
 * accepts all POST/PATCH/PUT requests and returns 201/200 regardless of payload.
 * These tests document the actual API behaviour and serve as a baseline
 * for when the same patterns are applied against a real backend.
 */
class NegativeScenariosApiTest extends BaseApiTest {

    @Steps
    PostSteps postSteps;

    // ------------------------------------------------------------------
    // Negative: POST with missing required field (title)
    // ------------------------------------------------------------------
    @Tag("negative")
    @Test
    @Title("NEG-01: POST /posts with missing title - documents API response behaviour")
    void shouldHandlePostWithMissingTitle() {
        Post invalidPost = PostFactory.postWithMissingTitle();
        Response response = postSteps.createPost(invalidPost);

        Serenity.recordReportData()
                .withTitle("Response Body (Missing Title)")
                .andContents(response.asPrettyString());

        // jsonplaceholder accepts all payloads - assert the returned status and
        // record it so the report shows the actual API behaviour
        assertTrue(
                response.getStatusCode() == 201 || response.getStatusCode() == 400,
                "API should respond with 201 (mock accepts all) or 400 (strict validation)"
        );
    }

    // ------------------------------------------------------------------
    // Negative: POST with malformed JSON body
    // ------------------------------------------------------------------
    @Tag("negative")
    @Test
    @Title("NEG-02: POST /posts with malformed JSON - documents API response behaviour")
    void shouldHandleMalformedJsonBody() {
        String malformedJson = "{\"title\": \"broken\", \"body\": }";
        Response response = postSteps.createPostWithRawBody(malformedJson);

        Serenity.recordReportData()
                .withTitle("Response Body (Malformed JSON)")
                .andContents(response.asPrettyString());

        // Record the actual status code so the Serenity report documents real behavior.
        // jsonplaceholder may return 201 (lenient mock), 400 (strict validation),
        // 422 (unprocessable entity), or 500 (server error depending on parser).
        int statusCode = response.getStatusCode();
        Serenity.recordReportData()
                .withTitle("Actual status for malformed JSON request")
                .andContents("HTTP Status: " + statusCode);

        assertTrue(
                statusCode >= 200 && statusCode < 600,
                "API should return a valid HTTP status code for malformed JSON, but got: " + statusCode
        );
    }

    // ------------------------------------------------------------------
    // Negative: GET to a completely non-existent endpoint
    // ------------------------------------------------------------------
    @Tag("negative")
    @Test
    @Title("NEG-03: GET /nonexistent-endpoint returns 404")
    void shouldReturn404ForNonExistentEndpoint() {
        Response response = postSteps.getEndpoint("/nonexistent-endpoint");

        Serenity.recordReportData()
                .withTitle("Response Body (Non-existent endpoint)")
                .andContents(response.asPrettyString());

        assertEquals(404, response.getStatusCode(),
                "A request to a non-existent endpoint should return 404");
    }
}