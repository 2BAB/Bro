package me.xx2bab.bro.sample.profile;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PiCalculatorTest {


    private PiCalculator piCalculator;

    @Before
    public void setup() {
        piCalculator = new PiCalculator();
    }

    @Test
    public void calculate_SimpleValue() {
        Assert.assertEquals(piCalculator.calculate(), 3.1415926, 0.000000001);
    }

}
