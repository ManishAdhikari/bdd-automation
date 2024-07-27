package org.company.database;

import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.company.util.sql.DatabaseUtils;
import org.company.util.LogUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DatabaseTest {

  @BeforeClass
  void setup() {
    DatabaseUtils.getDatabaseConnection();
  }

  @AfterClass
  void tearDown() {
    DatabaseUtils.closeDatabaseConnection();
  }

  @Test
  void testShouldExecuteQueryAndGetTopNRowsForGivenColumnNames() {
    var query = "SELECT * FROM hrd.employees";
    var actual =
        DatabaseUtils.executeQueryAndGetTopNRowsForColumnNames(query, List.of("first_name"), 3);
    LogUtils.info("Result", actual);
    Assert.assertEquals(3, actual.size());
    var expectedResult1 = Map.of("first_name", "Steven");
    var expectedResult2 = Map.of("first_name", "Neena");
    var expectedResult3 = Map.of("first_name", "Lex");
    Assertions.assertThat(actual)
        .containsExactly(expectedResult1, expectedResult2, expectedResult3);
  }

  @Test
  void testShouldExecuteQueryAndGetAllRowsForGivenColumnNames() {
    var query =
        "SELECT E.employee_id, E.first_name, E.last_name, E.email, "
            + "M.employee_id AS 'manager_id', M.first_name AS 'manager_first_name', M.last_name AS 'manager_last_name', M.email AS 'manager_email' "
            + "FROM hrd.employees E LEFT OUTER JOIN hrd.employees M ON E.manager_id = M.employee_id where E.manager_id = 100";
    var actual =
        DatabaseUtils.executeQueryAndGetAllRowsForColumnNames(
            query,
            List.of(
                "employee_id",
                "first_name",
                "last_name",
                "email",
                "manager_id",
                "manager_first_name",
                "manager_last_name",
                "manager_email"));
    LogUtils.info("Result", actual);
    var managerId = actual.stream().map(row -> row.get("manager_id")).toList();
    Assertions.assertThat(managerId).allMatch(mgrId -> "100".equals(mgrId));
  }

  @Test
  void testShouldExecuteQueryAndGetResultForColumnNumber() {
    var query = "SELECT COUNT(*) FROM hrd.countries WHERE region_id = 3";
    var actual = DatabaseUtils.executeQueryAndGetValueForColumnNumber(query, 1);
    LogUtils.info("Result", actual);
    Assertions.assertThat(actual).isEqualTo("6");
  }

  @Test
  void testShouldExecuteQueryAndGetSingleResult() {
    var query = "SELECT * FROM hrd.jobs WHERE job_title = 'Programmer'";
    var actual = DatabaseUtils.executeQueryAndGetSingleResult(query);
    LogUtils.info("Result", actual);
    Assertions.assertThat(actual.get("min_salary")).isEqualTo("4000.00");
    Assertions.assertThat(actual.get("max_salary")).isEqualTo("10000.00");
  }
}
