package testsRefact;

import core.BaseTest;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import tests.BarrigaTest;
import utils.BarrigaUtils;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.Matchers.is;

public class ContaTest extends BaseTest {

    @Test
    public void deveIncluirContaComSucesso() {
        given()
                .body("{\"nome\":\"Conta inserida\" }")
                .when()
                .post("/contas")
                .then()
                .statusCode(201);
    }

    @Test
    public void deveAlterarContaComSucesso() {

        Integer CONTA_ID = BarrigaUtils.getId("Conta para alterar");
        given()
                .body("{\"nome\":\"Conta inserida\" }")
                .pathParam("id", CONTA_ID)
                .when()
                .put("/contas/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("nome", is("Conta inserida"));
    }

    @Test
    public void naoDeveInserirContaComMesmoNome() {
        given()
                .body("{\"nome\": \"Conta mesmo nome\" }")
                .when()
                .post("/contas")
                .then()
                .log().all()
                .statusCode(400)
                .body("error",is("Já existe uma conta com esse nome!"));
    }


}
