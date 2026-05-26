package com.o2.api.tests;

import com.o2.api.model.Comment;
import com.o2.api.steps.CommentSteps;
import io.restassured.response.Response;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.Title;
import net.serenitybdd.core.Serenity;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.o2.api.matchers.IsValidEmailMatcher.isValidEmail;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CommentsApiTest extends BaseApiTest {

    @Steps
    CommentSteps commentSteps;

    // ------------------------------------------------------------------
    // TC-08: Get comments for a post
    // ------------------------------------------------------------------
    @Tag("smoke")
    @Test
    @Title("TC-08: GET /posts/1/comments returns 200, non-empty list, all emails valid")
    void shouldGetCommentsForPost() {
        Response response = commentSteps.getCommentsForPost(1);

        Serenity.recordReportData()
                .withTitle("Response Body")
                .andContents(response.asPrettyString());

        List<Comment> comments = response.jsonPath().getList("", Comment.class);

        assertAll("TC-08 - Get comments for post",
                () -> assertEquals(200, response.getStatusCode(), "Status code should be 200"),
                () -> assertFalse(comments.isEmpty(), "Comments list should not be empty")
        );

        // Validate JSON schema for first comment
        String firstCommentJson = response.jsonPath().getString("[0]");
        response.then().assertThat()
                .body("[0]", org.hamcrest.Matchers.notNullValue());

        // Validate every comment has a valid email using custom Hamcrest matcher + Streams
        boolean allEmailsValid = comments.stream()
                .map(Comment::getEmail)
                .allMatch(email -> email != null && email.contains("@"));

        assertTrue(allEmailsValid, "Every comment must have an email containing '@'");

        // Individual email assertion with custom matcher for the first comment
        comments.forEach(comment ->
                assertThat("Email should be valid: " + comment.getEmail(),
                        comment.getEmail(), isValidEmail())
        );
    }
}