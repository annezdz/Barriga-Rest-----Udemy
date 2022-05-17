package testsRefact.suite;

import core.BaseTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import tests.Movimentacao;
import testsRefact.AuthTest;
import testsRefact.ContaTest;
import testsRefact.SaldoTest;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ContaTest.class,
        Movimentacao.class,
        SaldoTest.class,
        AuthTest.class
})
public class Runner extends BaseTest {

    @BeforeClass
    public static void login() {
        Map<String,String> login = new HashMap<>();
        login.put("email","annezdz@hotmail.com");
        login.put("senha","123456");
        String TOKEN =  given()
                .body(login)
                .when()
                .post("/signin")
                .then()
                .statusCode(200)
                .extract().path("token");

        requestSpecification.header("Authorization","JWT " + TOKEN);
        RestAssured.get("/reset").then().statusCode(200);
    }


}
