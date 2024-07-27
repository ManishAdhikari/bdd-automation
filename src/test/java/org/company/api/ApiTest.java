package org.company.api;

import java.util.List;
import java.util.Map;
import org.company.restapi.RestService;
import org.company.util.FileUtils;
import org.company.util.RestApiUtils;
import org.testng.annotations.Test;

public class ApiTest {
  @Test
  void testGetApiCall() {
    var response = RestService.callGetEndpoint(FileUtils.getPropertyValue("api-get-all-url"));
    RestService.printResponseHeaders(response);
    RestApiUtils.validateResponseStatusCode(response, 200);
    RestApiUtils.validateResponseTime(response, 5000);
    RestApiUtils.validateIfResponseBodyContainsJsonKeys(
        response, List.of("id", "userId", "title", "body"));
    RestApiUtils.validateJsonKeyValueInResponseBody(response, "id", 1);
  }

  @Test
  void testGetApiCallWithPathParam() {
    var pathVariable = "/1";
    var response =
        RestService.callGetEndpoint(FileUtils.getPropertyValue("api-get-all-url") + pathVariable);
    RestService.printResponseBody(response);
    RestApiUtils.validateResponseStatusCode(response, 200);
    RestApiUtils.validateResponseTime(response, 5000);
    RestApiUtils.validateIfResponseBodyContainsJsonKeys(
        response, List.of("userId", "id", "title", "body"));
    RestApiUtils.validateJsonKeyValueInResponseBody(response, "id", 1);
  }

  @Test
  void testGetApiCallWithQueryParam() {
    var response =
        RestService.callGetEndpoint(
            FileUtils.getPropertyValue("api-get-all-url"), null, Map.of("userId", "1"));
    RestService.printCompleteResponse(response);
    RestApiUtils.validateResponseStatusCode(response, 200);
    RestApiUtils.validateResponseTime(response, 5000);
    RestApiUtils.validateIfResponseBodyContainsJsonKeys(
        response, List.of("userId", "id", "title", "body"));
    RestApiUtils.validateJsonKeyValueInResponseBody(response, "id", 1);
  }

  @Test
  void testGetApiCallWithPathAndQueryParam() {
    var pathVariable = "/1/comments";
    var response =
        RestService.callGetEndpoint(
            FileUtils.getPropertyValue("api-get-all-url") + pathVariable,
            null,
            Map.of("email", "Lew@alysha.tv"));
    RestService.printCompleteResponse(response);
    RestApiUtils.validateResponseStatusCode(response, 200);
    RestApiUtils.validateResponseTime(response, 5000);
    RestApiUtils.validateIfResponseBodyContainsJsonKeys(
        response, List.of("postId", "id", "name", "email", "body"));
    RestApiUtils.validateJsonKeyValueInResponseBody(response, "email", "Lew@alysha.tv");
  }

  @Test
  void testPostApiCall() {
    var requestBody =
        """
                {
                  title: 'foo',
                  body: 'bar',
                  userId: 1
                }""";
    var response =
        RestService.callPostEndpoint(FileUtils.getPropertyValue("api-post-url"), requestBody);
    RestService.printCompleteResponse(response);
    RestApiUtils.validateResponseStatusCode(response, 201);
    RestApiUtils.validateResponseTime(response, 5000);
    RestApiUtils.validateResponseHeaders(
        response, Map.of("Content-Type", "application/json; charset=utf-8"));
    RestApiUtils.validateIfResponseBodyContainsJsonKeys(response, List.of("id"));
  }

  @Test
  void testPutApiCall() {
    var requestBody =
        """
                {
                  id: 1,
                  title: 'foo',
                  body: 'this is updated body',
                  userId: 1
                }""";
    var headers = Map.of("'Content-type'", "'application/json; charset=UTF-8'");
    var response =
        RestService.callPutEndpoint(
            FileUtils.getPropertyValue("api-put-url"), headers, requestBody);
    RestService.printCompleteResponse(response);
    RestApiUtils.validateResponseStatusCode(response, 200);
    RestApiUtils.validateResponseTime(response, 5000);
    RestApiUtils.validateResponseHeaders(
        response, Map.of("Content-Type", "application/json; charset=utf-8"));
    RestApiUtils.validateIfResponseBodyContainsJsonKeys(response, List.of("id"));
  }

  @Test
  void testPatchApiCall() {
    var requestBody =
        """
                    {
                      name: Tony Stark
                    }""";
    var headers = Map.of("'Content-type'", "'application/json; charset=UTF-8'");
    var response =
        RestService.callPatchEndpoint(
            FileUtils.getPropertyValue("api-patch-url"), headers, requestBody);
    RestService.printCompleteResponse(response);
    RestApiUtils.validateResponseStatusCode(response, 200);
    RestApiUtils.validateResponseTime(response, 5000);
    RestApiUtils.validateResponseHeaders(
        response, Map.of("Content-Type", "application/json; charset=utf-8"));
    RestApiUtils.validateIfResponseBodyContainsJsonKeys(response, List.of("id"));
  }

  @Test
  void testDeleteApiCall() {
    var response = RestService.callDeleteEndpoint(FileUtils.getPropertyValue("api-delete-url"));
    RestService.printCompleteResponse(response);
    RestApiUtils.validateResponseStatusCode(response, 200);
    RestApiUtils.validateResponseTime(response, 5000);
    RestApiUtils.validateResponseHeaders(
        response, Map.of("Content-Type", "application/json; charset=utf-8"));
    RestApiUtils.validateJsonResponseBody(response, "{}");
  }
}
