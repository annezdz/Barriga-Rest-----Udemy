package testsRefact;

import core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.Matchers.is;

public class AuthTest extends BaseTest {

    @Test
    public void naoDeveAcessarAPISemToken() {
        FilterableRequestSpecification req = (FilterableRequestSpecification) requestSpecification;
        req.removeHeader("Authorization");
        given()
                .when()
                .get("/contas")
                .then()
                .statusCode(401);
    }

    public Integer getId(String nome) {
        return RestAssured.get("/contas?id"+nome).then().extract().path("id[1]");
    }
}
