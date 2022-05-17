package tests;

import core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import utils.DateUtils;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTest extends BaseTest {

    private static String CONTA_NAME = "Conta " + System.nanoTime();
    private static Integer CONTA_ID;
    private static Integer MOV_ID;

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

    }



    @Test
    public void t03_deveIncluirContaComSucesso() {
               CONTA_ID = given()
//                            .header("Authorization","JWT " + TOKEN) // pode ser bearer
                            .body("{\"nome\":\"" + CONTA_NAME + "\" }")
                       .when()
                            .post("/contas")
                       .then()
                            .statusCode(201)
                             .extract().path("id");
    }

    @Test
    public void t04_deveAlterarContaComSucesso() {
        given()
//                    .header("Authorization","JWT " + TOKEN) // pode ser bearer
                    .body("{\"nome\":\"" + CONTA_NAME + " alterada com sucesso\"}")
                    .pathParam("id", CONTA_ID)
                .when()
                    .put("/contas/{id}")
                .then()
                    .log().all()
                    .statusCode(200)
                .body("nome", is(CONTA_NAME + " alterada com sucesso"));
    }

    @Test
    public void t05_naoDeveInserirContaComMesmoNome() {
        given()
//                    .header("Authorization","JWT " + TOKEN) // pode ser bearer
                    .body("{\"nome\":\"" + CONTA_NAME + " alterada com sucesso\"}")
                .when()
                    .post("/contas")
                .then()
                .log().all()
                    .statusCode(400)
                .body("error",is("Já existe uma conta com esse nome!"));
    }

    @Test
    public void t06_deveInserirMovimentacaoComSucesso() {
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setConta_id(CONTA_ID);
        movimentacao.setDescricao("Descricao da Movimentacao");
        movimentacao.setEnvolvido("Envolvido na movimetação");
        movimentacao.setTipo("REC");
        movimentacao.setData_transacao("01/01/2021");
        movimentacao.setData_pagamento("31/03/2021");
        movimentacao.setValor(100F);
        movimentacao.setStatus(true);
            MOV_ID =  given()
//                    .header("Authorization","JWT " + TOKEN) // pode ser bearer
                .   body(movimentacao)
                .when()
                    .post("/transacoes")
                .then()
                    .log().all()
                    .statusCode(201)
                    .extract().path("id");
    }

    @Test
    public void t07_deveValidarCamposObrigatorios() {

        given()
//                .header("Authorization","JWT " + TOKEN) // pode ser bearer
                .   body("{}")
                .when()
                    .post("/transacoes")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("$", hasSize(8))
                .body("msg", hasItems(
                        "Data da Movimentação é obrigatório",
                        "Data do pagamento é obrigatório",
                        "Descrição é obrigatório",
                        "Interessado é obrigatório",
                        "Valor é obrigatório",
                        "Valor deve ser um número",
                        "Conta é obrigatório",
                        "Situação é obrigatório"
                        ));
    }
    @Test
    public void t08_naoDeveInserirMovimentacaoComDataFutura() {
        Movimentacao movimentacao = getMovimentacaoValida();
        movimentacao.setData_transacao(DateUtils.getDataDiferencaDias(2));
        given()
//                .header("Authorization","JWT " + TOKEN) // pode ser bearer
                .   body(movimentacao)
                .when()
                    .post("/transacoes")
                .then()
                    .log().all()
                    .statusCode(400)
                .body("$", hasSize(1))

                .body("msg",hasItem("Data da Movimentação deve ser menor ou igual à data atual"));
    }

    @Test
    public void t09_naoDeveRemoverContaComMovimentacao() {

                given()
//                    .header("Authorization","JWT " +  ) // pode ser bearer
                .when()
                    .delete("/contas/1163877")
                .then()
                    .log().all()
                    .statusCode(500)
                        .body("constraint",is("transacoes_conta_id_foreign"));
    }

    @Test
    public void t10_deveCalcularSaldoContas() {

        given()
//                .header("Authorization","JWT " + TOKEN) // pode ser bearer
                .when()
                    .get("/saldo")
                .then()
                    .log().all()
                    .statusCode(200)
                .body("find{it.conta_id == " + CONTA_ID +"}.saldo", is("100.00"));
    }

    @Test
    public void t11_deveRemoverUmaMovimentacao() {

                given()
//                    .header("Authorization","JWT " + TOKEN) // pode ser bearer
                        .pathParam("id", MOV_ID)
                .when()
                    .delete("/removerMovimentacao/{id}}")
                .then()
                    .log().all()
                .statusCode(404);
    }

    @Test
    public void t12_naoDeveAcessarAPISemToken() {
        FilterableRequestSpecification req = (FilterableRequestSpecification) requestSpecification;
        req.removeHeader("Authorization");
        given()
                .when()
                .get("/contas")
                .then()
                .statusCode(401);
    }


    private Movimentacao getMovimentacaoValida() {
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setConta_id(1163877);
        movimentacao.setDescricao("Descricao da Movimentacao");
        movimentacao.setEnvolvido("Envolvido na movimetação");
        movimentacao.setTipo("REC");
        movimentacao.setData_transacao(DateUtils.getDataDiferencaDias(-1));
        movimentacao.setData_pagamento(DateUtils.getDataDiferencaDias(5));
        movimentacao.setValor(100F);
        movimentacao.setStatus(true);
        return movimentacao;
    }


}
