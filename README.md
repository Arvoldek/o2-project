# O2 API Integration Tests

Automated integration test suite for the [JSONPlaceholder REST API](https://jsonplaceholder.typicode.com), built with **Java 11**, **Gradle**, **Serenity BDD**, **REST Assured**, and **JUnit 5**.

---

## Tech Stack

| Tool                               | Version |
| ---------------------------------- | ------- |
| Java                               | 11+     |
| Gradle                             | 8.x     |
| Serenity BDD                       | 4.1.20  |
| serenity-rest-assured              | 4.1.20  |
| serenity-junit5                    | 4.1.20  |
| JUnit Jupiter                      | 5.10.0  |
| Lombok                             | 1.18.30 |
| Jackson                            | 2.15.2  |
| REST Assured JSON Schema Validator | 5.3.2   |

---

## Project Structure

```
src/
└── test/
    ├── java/com/o2/api/
    │   ├── config/          ← ConfigProvider (reads base URL from serenity.conf)
    │   ├── model/           ← POJOs: Post, User, Comment, Address
    │   ├── factory/         ← PostFactory (test data builders)
    │   ├── matchers/        ← IsValidEmailMatcher (custom Hamcrest matcher)
    │   ├── specs/           ← RequestSpecs, ResponseSpecs (reusable REST Assured specs)
    │   ├── steps/           ← PostSteps, CommentSteps, UserSteps (@Step actions)
    │   └── tests/           ← Test classes (extend BaseApiTest)
    │       ├── BaseApiTest.java
    │       ├── PostsApiTest.java         (TC-01 – TC-07)
    │       ├── CommentsApiTest.java      (TC-08)
    │       ├── UsersApiTest.java         (TC-09 – TC-10)
    │       └── NegativeScenariosApiTest.java (NEG-01 – NEG-03)
    └── resources/
        ├── serenity.conf    ← Environment profiles (dev/test/prod)
        └── schemas/         ← JSON Schema files for response validation
```

---

## Test Cases

| ID     | Endpoint                | Method | Description                                            | Tag        |
| ------ | ----------------------- | ------ | ------------------------------------------------------ | ---------- |
| TC-01  | `/posts`                | GET    | Returns 200, 100 posts, each with id/userId/title/body | smoke      |
| TC-02  | `/posts/1`              | GET    | Returns 200, id=1, non-empty title                     | smoke      |
| TC-03  | `/posts/9999`           | GET    | Returns 404 for non-existent post                      | negative   |
| TC-03p | `/posts/{id}`           | GET    | Parameterized: 404 for ids 9999, 0, -1                 | negative   |
| TC-04  | `/posts`                | POST   | Returns 201, generated id, title matches               | regression |
| TC-05  | `/posts/1`              | PUT    | Returns 200, title = "Updated Title"                   | regression |
| TC-06  | `/posts/1`              | PATCH  | Returns 200, title = "Patched Title"                   | regression |
| TC-07  | `/posts/1`              | DELETE | Returns 200                                            | regression |
| TC-08  | `/posts/1/comments`     | GET    | Returns 200, non-empty, all emails valid               | smoke      |
| TC-09  | `/users`                | GET    | Returns 200, each user has nested address.city         | smoke      |
| TC-10  | `/users?username=Bret`  | GET    | Returns 200, exactly 1 user, username=Bret             | smoke      |
| NEG-01 | `/posts`                | POST   | POST with missing title — documents API behaviour      | negative   |
| NEG-02 | `/posts`                | POST   | POST with malformed JSON — documents API behaviour     | negative   |
| NEG-03 | `/nonexistent-endpoint` | GET    | Returns 404                                            | negative   |

---

## Running Tests

### Prerequisites

- Java 11 or higher installed
- `JAVA_HOME` set correctly

### Run all tests

```bash
./gradlew clean test
```

### Run by tag

```bash
# Smoke tests only
./gradlew clean test -Dgroups=smoke

# Regression tests only
./gradlew clean test -Dgroups=regression

# Negative tests only
./gradlew clean test -Dgroups=negative
```

### Run against a specific environment

```bash
# Test environment (default)
./gradlew clean test -Denvironment=test

# Dev environment
./gradlew clean test -Denvironment=dev
```

---

## Serenity HTML Report

After running tests, the Serenity HTML report is generated at:

```
target/site/serenity/index.html
```

Open it in a browser:

```bash
open target/site/serenity/index.html
```

---

## CI/CD

A GitHub Actions workflow is configured at `.github/workflows/tests.yml`.

It runs on every push and pull request to `main`, `master`, or `develop`, and:

1. Sets up Java 11 (Temurin)
2. Caches Gradle dependencies
3. Runs `./gradlew clean test`
4. Uploads the Serenity HTML report as a build artifact (retained 30 days)
5. Uploads JUnit test results as a build artifact (retained 7 days)

---

## Architecture

The project follows a **layered architecture** to keep HTTP calls, business logic, and test assertions cleanly separated:

```
Test classes  →  @Steps (PostSteps / CommentSteps / UserSteps)  →  REST Assured (HTTP)
     ↑                        ↑
  JUnit 5               RequestSpecs / ResponseSpecs
  assertAll()           ConfigProvider (serenity.conf)
  Serenity report
```

**Key patterns used:**

- `BaseApiTest` — shared `@BeforeAll` setup, extends `SerenityJUnit5Extension`
- `@Step` methods — all HTTP calls are in step classes, not test classes
- `Builder pattern` — `PostFactory` creates test data via Lombok `@Builder`
- `RequestSpecification` / `ResponseSpecification` — reusable REST Assured specs
- `JsonPath` — value extraction from JSON responses
- `Java Streams` — list verification (`allMatch`, `map`)
- `assertAll` — soft assertions so all failures appear in one report
- `@ParameterizedTest` — TC-03 runs against multiple invalid IDs
- `JSON Schema validation` — structural response validation via `matchesJsonSchemaInClasspath`
- Custom Hamcrest matcher — `isValidEmail()` for readable email assertions
- `Serenity.recordReportData()` — attaches response body to Serenity report on failure
- `@Tag` — categorises tests as `smoke`, `regression`, or `negative`
