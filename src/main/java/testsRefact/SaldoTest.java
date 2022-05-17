package testsRefact;

import core.BaseTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.Matchers.is;

public class SaldoTest extends BaseTest {

    @Test
    public void deveCalcularSaldoContas() {

        Integer CONTA_ID = getId("Conta para saldo");

        given()
                .when()
                .get("/saldo")
                .then()
                .log().all()
                .statusCode(200)
                .body("find{it.conta_id == " + CONTA_ID +"}.saldo", is("534.00"));
    }

    public Integer getId(String nome) {
        return RestAssured.get("/contas?id"+nome).then().extract().path("id[1]");
    }
}
