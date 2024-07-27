package org.company.util;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.lessThan;

import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.json.JSONObject;

public class RestApiUtils {

  public static void validateResponseHeaders(
      Response response, Map<String, String> expectedHeaders) {
    response.then().assertThat().headers(expectedHeaders);
  }

  public static void validateResponseStatusCode(Response response, int expectedHttpStatusCode) {
    response.then().assertThat().statusCode(expectedHttpStatusCode);
  }

  public static void validateResponseTime(
      Response response, long expectedResponseTimeInMilliSeconds) {
    response.then().time(lessThan(expectedResponseTimeInMilliSeconds));
  }

  public static void validateIfResponseBodyContainsJsonKey(Response response, String jsonKey) {
    var retrievedKeyValue = response.jsonPath().get(jsonKey);
    if (retrievedKeyValue instanceof List) {
      Assertions.assertThat((List) retrievedKeyValue).doesNotContainNull();
    } else {
      Assertions.assertThat(retrievedKeyValue).isNotNull();
    }
  }

  public static void validateIfResponseBodyContainsJsonKeys(
      Response response, List<String> jsonKeys) {
    jsonKeys.forEach(jsonKey -> validateIfResponseBodyContainsJsonKey(response, jsonKey));
  }

  public static void validateJsonKeyValueInResponseBody(
      Response response, String jsonKey, Object expectedJsonKeyValue) {
    var retrievedKeyValue = response.jsonPath().get(jsonKey);
    if (retrievedKeyValue instanceof List) {
      Assertions.assertThat((List) retrievedKeyValue).contains(expectedJsonKeyValue);
    } else {
      Assertions.assertThat(retrievedKeyValue).isEqualTo(expectedJsonKeyValue);
    }
  }

  public static void validateJsonResponseBody(Response response, String expectedJsonBody) {
    response.then().assertThat().body(containsString(expectedJsonBody));
  }

  public static JSONObject getResponseAsJson(Response response) {
    return new JSONObject(response.getBody().asPrettyString());
  }

  public static String getKeyValueFromJsonResponse(Response response, String jsonKey) {
    return getResponseAsJson(response).get(jsonKey).toString();
  }

  public static String getValueForJsonKeyFromResponse(Response response, String jsonKey) {
    return response.jsonPath().get(jsonKey);
  }
}
