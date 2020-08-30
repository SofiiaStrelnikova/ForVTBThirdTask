import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.*;

import java.util.Random;

import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestsFakeAPI {
    private final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("https://jsonplaceholder.typicode.com")
            .setContentType("application/json")
            .build();

    private final ResponseSpecification responseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .build();

    private static Response listOfComments;
    private static String postId;
    private static String userId;

    @Test
    @Order(1)
    public void getListOfComments() {
        Response response =
                given()
                        .spec(requestSpec)
                        .get(EndPoints.comments);

        response
                .then()
                .spec(responseSpec);

        listOfComments = response;
        response.getBody().print();
    }

    @Test
    @Order(2)
    public void getRandomComment() {
        Random random = new Random();
        int sizeOfResponseList =
                listOfComments
                        .getBody()
                        .jsonPath()
                        .getList("$")
                        .size();

        Assertions.assertTrue(sizeOfResponseList > 0);

        int randomIndex = random.nextInt(sizeOfResponseList);

        Response response =
                given()
                        .spec(requestSpec)
                        .get(EndPoints.comments + '/' + randomIndex);

        response
                .then()
                .spec(responseSpec);

        postId = response.jsonPath().getString("postId");
        response.getBody().print();
    }

    @Test
    @Order(3)
    public void getPostWithSpecificId() {
        int postIdInt = Integer.parseInt(postId);

        Response response =
                given()
                        .spec(requestSpec)
                        .get(EndPoints.posts + '/' + postIdInt);

        response
                .then()
                .spec(responseSpec);

        userId = response.jsonPath().getString("userId");
        response.getBody().print();
    }

    @Test
    @Order(4)
    public void getUserWithSpecificId() {
        int userIdInt = Integer.parseInt(userId);

        Response response =
                given()
                        .spec(requestSpec)
                        .get(EndPoints.users + '/' + userIdInt);

        response
                .then()
                .spec(responseSpec);

        response.getBody().print();
    }

    @Test
    @Order(5)
    public void deleteSpecificPost() {
        int postIdInt = Integer.parseInt(postId);

        Response response =
                given()
                        .spec(requestSpec)
                        .delete(EndPoints.posts + '/' + postIdInt);

        response
                .then()
                .spec(responseSpec);

        response.getBody().print();
    }

    @Test
    @Order(6)
    public void renameUserWithSpecificId() {
        int userIdInt = Integer.parseInt(userId);
        UserNameUpdater userNameUpdater = new UserNameUpdater("Abacaba");

        Response response =
                given()
                        .spec(requestSpec)
                        .basePath(EndPoints.users + '/' + userIdInt)
                        .body(userNameUpdater)
                        .put();
        response
                .then()
                .log()
                .all()
                .spec(responseSpec);
    }
}
