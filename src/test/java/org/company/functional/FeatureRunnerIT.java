package org.company.functional;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.company.util.sql.DatabaseUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;

@CucumberOptions(
    features = {"classpath:features/demo"},
    // features = {"classpath:features"},
    glue = "org.company",
    plugin = {
      "pretty",
      "html:target/cucumber/cucumber-html-report.html",
      "json:target/cucumber/cucumber.json"
    } /*,tags = "@ApiTest"*/)
public class FeatureRunnerIT extends AbstractTestNGCucumberTests {

  @Override
  @DataProvider(parallel = true)
  public Object[][] scenarios() {
    return super.scenarios();
  }

  @AfterClass
  public void disconnectDependentServices() {
    DatabaseUtils.closeDatabaseConnection();
  }
}
