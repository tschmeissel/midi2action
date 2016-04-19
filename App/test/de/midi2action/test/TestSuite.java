package de.midi2action.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ConfigurationPropertiesTest.class,
		GlobalConfigurationTest.class, MidiInDeviceProxyTest.class })
public class TestSuite {

}
