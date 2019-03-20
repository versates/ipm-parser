package org.versates.ipm.parser;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class GeneralTestHelper {

    @Test
    public void testRegularExpression() {
        assertThat("<message>Asda</message>".replaceAll("<(/)?message>", "<$1header>"),
                is(equalTo("<header>Asda</header>")));
    }

    @Test
    public void testMti() {
        String mtiPattern = "^(1240|1442|1644|1740)$";
        assertThat("1240".matches(mtiPattern), is(equalTo(true)));
        assertThat("1442".matches(mtiPattern), is(equalTo(true)));
        assertThat("1644".matches(mtiPattern), is(equalTo(true)));
        assertThat("1740".matches(mtiPattern), is(equalTo(true)));
    }

}
