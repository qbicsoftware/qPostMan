package life.qbic.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringUtilTest {

    @Test
    public void endsWithIgnoreCase() {
        assertTrue(StringUtil.endsWithIgnoreCase("thisissomerandomTESTBLA", "randomTESTbla"));
        assertFalse(StringUtil.endsWithIgnoreCase("thisissomerandomTESTBLA", "ayyyyynope"));
    }

}
