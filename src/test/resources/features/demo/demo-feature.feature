Feature: Demo feature

  @ApiTest
  Scenario Outline: Test the dummy api scenario
    Given API host "<host>" is reachable
    When GET API endpoint having url "<url>" is triggered with headers
      | Accept          | text/html    |
      | Accept-Language | en-GB        |
    Then Validate if GET API returns response with http status code <expected-response-status-code>
    Examples:
      | host                                  | url                                             | expected-response-status-code |
      | https://jsonplaceholder.typicode.com  | https://jsonplaceholder.typicode.com/users/1    | 200                        |

  @DatabaseTest
  Scenario Outline: Test the dummy database scenario
    Given Connect to "sql-server" database
    When SQL query "sql-1" from file "<sql-file>" at path "<sql-file-path>" is executed
    Then Validate if result contains <expected-number-of-rows> rows
    Then Validate if result contains row number <row-number> column name "<column-name>" value as "<expected-value>"
    Examples:
      | sql-file        | sql-file-path                         | expected-number-of-rows | row-number | column-name | expected-value |
      | sql-query.json  | src/test/resources/features/demo/sql  | 6                       | 1          | region_id   | 3              |