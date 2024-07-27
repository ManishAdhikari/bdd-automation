package org.company.util.sql;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.company.exception.AutomationException;
import org.company.util.FileUtils;
import org.company.util.LogUtils;

public class DatabaseUtils {
  private static Connection connection;

  public static boolean isDatabaseUpAndRunning() {
    try {
      return connection.getMetaData() != null;
    } catch (SQLException e) {
      return false;
    }
  }

  public static Connection getDatabaseConnection() {
    var dbUrl = FileUtils.getPropertyValue("dbUrl");
    var username = FileUtils.getPropertyValue("dbUsername");
    var password = FileUtils.getPropertyValue("dbPassword");
    try {
      connection = DriverManager.getConnection(dbUrl, username, password);
      LogUtils.debug("Database connection established");
    } catch (SQLException e) {
      LogUtils.error("Unable to establish db connection", e);
    }
    return connection;
  }

  public static void closeDatabaseConnection() {
    if (connection != null) {
      try {
        connection.close();
        LogUtils.debug("Database connection closed successfully");
      } catch (SQLException e) {
        LogUtils.error("Unable to close database connection", e);
      }
    } else {
      LogUtils.debug("No connection exist that can be closed");
    }
  }

  public static ResultSet executeQuery(String sqlQuery) {
    ResultSet resultSet = null;
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(sqlQuery);
      resultSet = preparedStatement.executeQuery();
    } catch (SQLException e) {
      handleException(sqlQuery, preparedStatement, e);
    }
    return resultSet;
  }

  public static void execute(String sqlQuery) {
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(sqlQuery);
      preparedStatement.execute();
    } catch (SQLException e) {
      handleException(sqlQuery, preparedStatement, e);
    }
  }

  private static void handleException(
      String sqlQuery, PreparedStatement preparedStatement, SQLException e) {
    if (preparedStatement != null) {
      try {
        preparedStatement.close();
      } catch (SQLException ex) {
        LogUtils.error(String.format("Unable to close statement: %s", sqlQuery), e);
      }
    }
    LogUtils.error(String.format("Unable to execute sql query: %s", sqlQuery), e);
  }

  public static Map<Integer, Map<String, String>> executeQueryAndGetResult(String sqlQuery) {
    var resultMap = new LinkedHashMap<Integer, Map<String, String>>();
    var resultSet = executeQuery(sqlQuery);
    if (resultSet != null) {
      try {
        var resultSetMetadata = resultSet.getMetaData();
        var columnCount = resultSetMetadata.getColumnCount();
        int i = 1;
        while (resultSet.next()) {
          var columnNameAndValue = new LinkedHashMap<String, String>();
          for (int j = 1; j <= columnCount; j++) {
            columnNameAndValue.put(resultSetMetadata.getColumnName(j), resultSet.getString(j));
          }
          resultMap.put(i, columnNameAndValue);
          i++;
        }
      } catch (SQLException e) {
        LogUtils.error(
            String.format("Unable to execute sql query and get result: %s", sqlQuery), e);
      }
    }
    return resultMap;
  }

  public static Map<String, String> executeQueryAndGetSingleResult(String sqlQuery) {
    return executeQueryAndGetResult(sqlQuery).get(1);
  }

  public static List<Map<String, String>> executeQueryAndGetTopNRows(
      String sqlQuery, int numOfRows) {
    return executeQueryAndGetResult(sqlQuery).values().stream().limit(numOfRows).toList();
  }

  public static List<Map<String, String>> executeQueryAndGetAllRows(String sqlQuery) {
    return executeQueryAndGetResult(sqlQuery).values().stream().toList();
  }

  public static List<Map<String, String>> executeQueryAndGetTopNRowsForColumnNames(
      String sqlQuery, List<String> columnNames, int numOfRows) {
    var queryResult = executeQueryAndGetTopNRows(sqlQuery, numOfRows);
    return queryResult.stream().map(row -> getSingleRowForColumnNames(row, columnNames)).toList();
  }

  public static List<Map<String, String>> executeQueryAndGetAllRowsForColumnNames(
      String sqlQuery, List<String> columnNames) {
    var queryResult = executeQueryAndGetAllRows(sqlQuery);
    return queryResult.stream().map(row -> getSingleRowForColumnNames(row, columnNames)).toList();
  }

  public static String executeQueryAndGetValueForColumnNumber(String sqlQuery, int columnNum) {
    if (columnNum == 0) throw new AutomationException("Column number can't be zero");
    var queryResult =
        executeQueryAndGetTopNRows(
            sqlQuery, 1); // decrement by 1 as ResultSet column number starts with '0'
    return !queryResult.isEmpty()
        ? queryResult.get(0).values().stream().toList().get(columnNum - 1)
        : StringUtils.EMPTY;
  }

  public static String executeQueryAndGetValueForColumnName(String sqlQuery, String columnName) {
    var queryResult = executeQueryAndGetTopNRows(sqlQuery, 1);
    return !queryResult.isEmpty() ? queryResult.get(0).get(columnName) : StringUtils.EMPTY;
  }

  private static Map<String, String> getSingleRowForColumnNames(
      Map<String, String> queryResult, List<String> columnNames) {
    var result = new LinkedHashMap<String, String>();
    if (!queryResult.isEmpty()) {
      columnNames.forEach(
          cn -> {
            if (queryResult.containsKey(cn)) {
              result.put(cn, queryResult.get(cn));
            } else {
              result.put(cn, null);
            }
          });
    }
    return result;
  }
}
