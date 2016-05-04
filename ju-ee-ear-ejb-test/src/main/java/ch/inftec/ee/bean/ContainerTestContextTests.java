package ch.inftec.ee.bean;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ContainerTestContextIT.class, ContainerTestContextRemoteIT.class })
public class ContainerTestContextTests {

}
