package com.ticketingservice;

import org.junit.runner.RunWith;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Chandramohan on 4/29/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class, initializers = ConfigFileApplicationContextInitializer.class)
@TestPropertySource(properties = {"ticket.timeoutinseconds=20"})
public abstract class SpringJunitRunner {
}
