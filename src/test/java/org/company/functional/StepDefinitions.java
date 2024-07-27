package org.company.functional;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.company.restapi.RestService;
import org.company.util.sql.DatabaseUtils;
import org.company.util.FileUtils;
import org.company.util.LogUtils;
import org.company.util.RestApiUtils;

public class StepDefinitions {

  private ScenarioContext scenarioContext;

  @Before
  public void initScenario() {
    LogUtils.debug("Initialization finished for scenario");
    scenarioContext = new ScenarioContext();
  }

  @Given("Connect to {string} database")
  public void connectToDatabase(String database) {
    Assertions.assertThat(DatabaseUtils.getDatabaseConnection()).isNotNull();
    LogUtils.debug("{} database is up & running", database);
  }

  @Given("API host {string} is reachable")
  public void isApiReachable(String host) {
    var response = RestService.callGetEndpoint(host);
    RestApiUtils.validateResponseStatusCode(response, 200);
    LogUtils.debug("API host is reachable");
  }

  @When("GET API endpoint having url {string} is triggered with headers")
  public void apiEndpointIsTriggered(String url, DataTable dataTable) {
    Map<String, String> headers = dataTable.asMap();
    LogUtils.debug("GET API endpoint having url {} is triggered with headers {}", url, headers);
    var response = RestService.callGetEndpoint(url, headers);
    scenarioContext.setContext("api-response", response);
  }

  @Then("Validate if GET API returns response with http status code {int}")
  public void validateIfApiReturnsResponseWithHttpStatusCode(int expectedResponseStatusCode) {
    LogUtils.debug(
        "Validating if API returned the response with status code", expectedResponseStatusCode);
    var apiResponse = (Response) scenarioContext.getContext("api-response");
    RestApiUtils.validateResponseStatusCode(apiResponse, expectedResponseStatusCode);
  }

  @When("SQL query {string} from file {string} at path {string} is executed")
  public void whenSqlQueryFromFileAtPathIsExecuted(
      String queryIdentifier, String fileName, String path) {
    LogUtils.debug("Executing sql query {} inside file {}/{}", queryIdentifier, fileName, path);
    var json = FileUtils.readFileAsJsonObject(path, fileName);
    var query = json.get(queryIdentifier).toString();
    LogUtils.debug("Query", query);
    var result = DatabaseUtils.executeQueryAndGetAllRows(query);
    scenarioContext.setContext("query-result", result);
  }

  @Then("Validate if result contains {int} rows")
  public void thenValidateIfResultContainsExpectedNumOfRows(int expectedNumOfRows) {
    LogUtils.debug("Validating if sql query result contains {} number of rows", expectedNumOfRows);
    var queryResult = (List<Map<String, String>>) scenarioContext.getContext("query-result");
    Assertions.assertThat(queryResult.size()).isEqualTo(expectedNumOfRows);
  }

  @Then("Validate if result contains row number {int} column name {string} value as {string}")
  public void thenValidateIfResultContainsExpectedValueForColumnName(
      int rowNum, String columnName, String columnValue) {
    LogUtils.debug(
        "Validating if sql query result in row number {} contains column name {} and column value {}",
        rowNum,
        columnName,
        columnValue);
    var queryResult = (List<Map<String, String>>) scenarioContext.getContext("query-result");
    Assertions.assertThat(queryResult.get(rowNum).get(columnName)).isEqualTo(columnValue);
  }
}
