package core;


import io.restassured.http.ContentType;

import java.sql.Struct;

public interface Constantes {

    String APP_BASE_URL = "https://barrigarest.wcaquino.me";
    Integer APP_PORT = 443;
    String APP_BASE_PATH = "";

    ContentType APP_CONTENT_TYPE = ContentType.JSON;

    Long MAX_TIMEOUT = 10000L;

}
