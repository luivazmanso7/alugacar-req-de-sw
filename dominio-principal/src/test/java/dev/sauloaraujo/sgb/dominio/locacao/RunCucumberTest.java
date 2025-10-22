package dev.sauloaraujo.sgb.dominio.locacao;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasspathResource("dev/sauloaraujo/sgb/dominio/locacao")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "dev.sauloaraujo.sgb.dominio.locacao")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
public class RunCucumberTest {
}
