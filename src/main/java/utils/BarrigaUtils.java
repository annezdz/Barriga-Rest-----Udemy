package utils;

import io.restassured.RestAssured;

public class BarrigaUtils {

    public static Integer getId(String nome) {
        return RestAssured.get("/contas?id"+nome).then().extract().path("id[1]");
    }

    public static Integer getIdDescricao(String desc) {

        return RestAssured.get("/transacoes?id"+desc).then().extract().path("id[0]");
    }
}
