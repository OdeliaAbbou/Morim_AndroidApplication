package com.example.morim;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Suite qui regroupe tous les tests UI/Integration.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AuthActivityUIIntegrationTest.class,
        RegisterTest.class,
        ScheduleMeetingTest.class
})
public class AllInstrumentedTests {
}
