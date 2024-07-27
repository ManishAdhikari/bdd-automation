package org.company.restapi;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.company.util.LogUtils;
import org.company.util.MapUtils;

public class RestService {

  public static Response callGetEndpoint(String url) {
    return callGetEndpoint(url, null, null, null);
  }

  public static Response callGetEndpoint(String url, Map<String, String> headers) {
    return callGetEndpoint(url, headers, null, null);
  }

  public static Response callGetEndpoint(
      String url, Map<String, String> headers, Map<String, String> queryParams) {
    return callGetEndpoint(url, headers, queryParams, null);
  }

  public static Response callGetEndpoint(
      String url,
      Map<String, String> headers,
      Map<String, String> queryParams,
      Map<String, String> pathParams) {
    LogUtils.debug("Calling GET api with url", url);
    return getRequestSpecification(headers, queryParams, pathParams).get(url);
  }

  public static Response callPostEndpoint(String url, String requestBody) {
    return callPostEndpoint(url, null, requestBody);
  }

  public static Response callPostEndpoint(
      String url, Map<String, String> headers, String requestBody) {
    LogUtils.debug("Calling POST api with url", url);
    return getRequestSpecification(headers, null, null).body(requestBody).post(url);
  }

  public static Response callPutEndpoint(String url, String requestBody) {
    return callPutEndpoint(url, null, requestBody);
  }

  public static Response callPutEndpoint(
      String url, Map<String, String> headers, String requestBody) {
    return callPutEndpoint(url, headers, null, null, requestBody);
  }

  public static Response callPutEndpoint(
      String url,
      Map<String, String> headers,
      Map<String, String> queryParams,
      Map<String, String> pathParams,
      String requestBody) {
    LogUtils.debug("Calling PUT api with url", url);
    return getRequestSpecification(headers, queryParams, pathParams)
        .body(StringUtils.isNotBlank(requestBody) ? requestBody : null)
        .put(url);
  }

  public static Response callPatchEndpoint(String url, String requestBody) {
    return callPatchEndpoint(url, null, requestBody);
  }

  public static Response callPatchEndpoint(
      String url, Map<String, String> headers, String requestBody) {
    return callPatchEndpoint(url, headers, null, null, requestBody);
  }

  public static Response callPatchEndpoint(
      String url,
      Map<String, String> headers,
      Map<String, String> queryParams,
      Map<String, String> pathParams,
      String requestBody) {
    LogUtils.debug("Calling PATCH api with url", url);
    return getRequestSpecification(headers, queryParams, pathParams)
        .body(StringUtils.isNotBlank(requestBody) ? requestBody : null)
        .patch(url);
  }

  public static Response callDeleteEndpoint(String url) {
    return callDeleteEndpoint(url, null);
  }

  public static Response callDeleteEndpoint(String url, Map<String, String> headers) {
    return getRequestSpecification(headers, null, null).delete(url);
  }

  public static Response callDeleteEndpoint(
      String url,
      Map<String, String> headers,
      Map<String, String> queryParams,
      Map<String, String> pathParams,
      String requestBody) {
    LogUtils.debug("Calling DELETE api with url", url);
    return getRequestSpecification(headers, queryParams, pathParams)
        .body(StringUtils.isNotBlank(requestBody) ? requestBody : null)
        .delete(url);
  }

  private static RequestSpecification getRequestSpecification(
      Map<String, String> headers,
      Map<String, String> queryParams,
      Map<String, String> pathParams) {
    var requestSpecification = given();
    if (!MapUtils.isNullOrEmpty(headers)) {
      requestSpecification.headers(headers);
    }
    if (!MapUtils.isNullOrEmpty(queryParams)) {
      requestSpecification.queryParams(queryParams);
    }
    if (!MapUtils.isNullOrEmpty(pathParams)) {
      requestSpecification.pathParams(pathParams);
    }
    return requestSpecification.when();
  }

  public static void printResponseBody(Response response) {
    LogUtils.debug("Response body", response.getBody().asPrettyString());
  }

  public static void printResponseHeaders(Response response) {
    LogUtils.debug("Response headers", response.getHeaders().asList());
  }

  public static void printCompleteResponse(Response response) {
    var completeResponse =
        new StringBuilder()
            .append("\n")
            .append("Status code: ")
            .append(response.getStatusCode())
            .append("\n")
            .append("Body: ")
            .append(response.getBody().asPrettyString())
            .append("\n")
            .append("Headers: ")
            .append(response.getHeaders().asList())
            .toString();
    LogUtils.debug("Response", completeResponse);
  }
}
