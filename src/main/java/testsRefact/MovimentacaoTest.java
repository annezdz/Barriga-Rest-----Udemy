package testsRefact;

import core.BaseTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;
import tests.Movimentacao;
import utils.BarrigaUtils;
import utils.DateUtils;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.Matchers.*;

public class MovimentacaoTest extends BaseTest {

    @Test
    public void deveInserirMovimentacaoComSucesso() {
        Movimentacao movimentacao = getMovimentacaoValida();
        given()
                .   body(movimentacao)
                .when()
                .post("/transacoes")
                .then()
                .log().all()
                .statusCode(201)
                .body("descricao",is("Descricao da Movimentacao"))
                .body("envolvido",is("Envolvido na movimetação"))
                .body("tipo",is("REC"))
                .body("valor",is("100.00"));
    }

    @Test
    public void deveValidarCamposObrigatorios() {

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
    public void naoDeveInserirMovimentacaoComDataFutura() {
        Movimentacao movimentacao = getMovimentacaoValida();
        movimentacao.setData_transacao(DateUtils.getDataDiferencaDias(2));
        given()
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
    public void naoDeveRemoverContaComMovimentacao() {

        Integer CONTA_ID = BarrigaUtils.getId("Conta com movimentacao");

        given()
                .pathParam("id",CONTA_ID)
                .when()
                .delete("/contas/{id}")
                .then()
                .log().all()
                .statusCode(500)
                .body("constraint",is("transacoes_conta_id_foreign"));
    }
    @Test
    public void deveRemoverUmaMovimentacao() {
        Integer MOV_ID = BarrigaUtils.getIdDescricao("Movimentacao para exclusao");

        given()
                .pathParam("id", MOV_ID)
                .when()
                .delete("/removerMovimentacao/{id}}")
                .then()
                .log().all()
                .statusCode(404);
    }

    private Movimentacao getMovimentacaoValida() {
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setConta_id(BarrigaUtils.getId("Conta para movimentacoes"));
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
