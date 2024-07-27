package org.company.util;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.lessThan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import java.util.*;

import org.assertj.core.api.Assertions;
import org.company.exception.AutomationException;
import org.company.restapi.RestService;
import org.json.JSONArray;
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

  public static JSONObject getResponseAsJsonObject(Response response) {
    var responseBody = RestService.getResponseBody(response);
    if (responseBody.startsWith("{")) {
      return new JSONObject(responseBody);
    }
    throw new AutomationException("Invalid json");
  }

  public static JSONArray getResponseAsJsonArray(Response response) {
    var responseBody = RestService.getResponseBody(response);
    if (responseBody.startsWith("[")) {
      return new JSONArray(responseBody);
    }
    throw new AutomationException("Invalid json");
  }

  public static boolean hasKey(Response response, String jsonKey) {
    var responseBody = RestService.getResponseBody(response);
    if (responseBody.startsWith("[")) {
      return getResponseAsJsonArray(response).toList().stream().anyMatch(je -> {
          if (je instanceof Map<?,?>) {
              return ( (Map<?, ?>) je ).containsKey(jsonKey);
          } else if (je instanceof List<?>) {
            throw new AutomationException("Nested json array support is not available at the moment");
          }
        throw new AutomationException("Invalid json");
      });
    } else if (responseBody.startsWith("{")) {
      return getResponseAsJsonObject(response).has(jsonKey);
    }
    throw new AutomationException("Invalid json");
  }

  public static boolean isValuePresentForKeyInResponse(Response response, String jsonKey) {
    var responseBody = RestService.getResponseBody(response);
    if (hasKey(response, jsonKey) && responseBody.startsWith("[")) {
      return getResponseAsJsonArray(response).toList().stream().anyMatch(je -> {
        if (je instanceof Map<?,?>) {
          return Objects.nonNull(((Map<?, ?>) je).get(jsonKey));
        } else if (je instanceof List<?>) {
          throw new AutomationException("Nested json array support is not available at the moment");
        }
        throw new AutomationException("Invalid json");
      });
    } else if (responseBody.startsWith("{")) {
      return hasKey(response, jsonKey) && !getResponseAsJsonObject(response).isNull(jsonKey);
    }
    throw new AutomationException("Invalid json");
  }

  public static Object getValueForJsonKeyFromResponse(Response response, String jsonKey) {
    var responseBody = response.getBody();
    if (responseBody instanceof List<?>) {
      return responseBody.jsonPath().getList(jsonKey);
    }
    return responseBody.jsonPath().get(jsonKey);
  }
}
