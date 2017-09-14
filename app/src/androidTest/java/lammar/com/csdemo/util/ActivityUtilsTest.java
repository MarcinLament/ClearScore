package lammar.com.csdemo.util;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Marcin Lament
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ActivityUtilsTest {

    @Test
    public void logHistory_ParcelableWriteRead() {
        assertThat(1, is(1));
    }
}
