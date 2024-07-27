package org.company.util;

import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;
import org.company.exception.AutomationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtils {

  private static final String INFO = "INFO";
  private static final String DEBUG = "DEBUG";
  private static final String TRACE = "TRACE";
  private static final String WARN = "WARN";
  private static final String LOG_PLACEHOLDER = "{}";
  private static final String COLON_SEPARATOR = " : ";
  private static final String DOT_REGEX = "\\.";
  private static final String DOT_SYMBOL = ".";

  private static Logger logger = LoggerFactory.getLogger(LogUtils.class);

  public static void info(String logStatement, Object... values) {
    print(getCallerMethod(), INFO, logStatement, values);
  }

  public static void debug(String logStatement, Object... values) {
    print(getCallerMethod(), DEBUG, logStatement, values);
  }

  public static void trace(String logStatement, Object... values) {
    print(getCallerMethod(), TRACE, logStatement, values);
  }

  public static void warn(String logStatement, Object... values) {
    print(getCallerMethod(), WARN, logStatement, values);
  }

  public static void error(String logStatement, Exception exception) {
    printFail(getCallerMethod(), logStatement, exception);
  }

  private static void print(
      String callerMethod, String level, String logStatement, Object... values) {
    var configuredLogLevel = FileUtils.getPropertyValue("log-level");
    var log = new StringBuilder().append(callerMethod).append(logStatement).toString();
    if (!logStatement.contains(LOG_PLACEHOLDER) && values.length > 0) {
      log =
          new StringBuilder()
              .append(log)
              .append(COLON_SEPARATOR)
              .append(LOG_PLACEHOLDER)
              .toString();
    }
    if (level.equalsIgnoreCase(configuredLogLevel)) {
      switch (configuredLogLevel.toUpperCase()) {
        case INFO -> logger.info(log, values);
        case DEBUG -> logger.debug(log, values);
        case TRACE -> logger.trace(log, values);
        case WARN -> logger.warn(log, values);
        default -> throw new AutomationException(
            "Invalid log level defined in service test-data. Possible values are: INFO, DEBUG, TRACE, WARN");
      }
    }
  }

  private static void printFail(String callerMethod, String logStatement, Exception e) {
    var log = new StringBuilder().append(callerMethod).append(logStatement).toString();
    logger.error(log, e);
    throw new AutomationException("Automation framework exception occurred");
  }

  private static String getCallerMethod() {
    StackWalker stackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    StackWalker.StackFrame stackFrame =
        stackWalker.walk(stackFrameStream -> stackFrameStream.skip(2).findFirst().orElse(null));
    if (stackFrame == null) {
      return StringUtils.EMPTY;
    }
    var fullQualifiedClassNameArr = stackFrame.getClassName().split(DOT_REGEX);
    var className = new StringBuilder();
    IntStream.range(0, fullQualifiedClassNameArr.length)
        .forEach(
            i -> {
              if (i != fullQualifiedClassNameArr.length - 1) {
                className.append(fullQualifiedClassNameArr[i].charAt(0)).append(DOT_SYMBOL);
              } else {
                className.append(fullQualifiedClassNameArr[i]);
              }
            });
    return String.format(
        "[%s#%s:%s] : ", className, stackFrame.getMethodName(), stackFrame.getLineNumber());
  }
}
