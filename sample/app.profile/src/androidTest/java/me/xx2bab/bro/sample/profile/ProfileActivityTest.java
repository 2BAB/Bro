package me.xx2bab.bro.sample.profile;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import me.xx2bab.bro.sample.profile.R;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

/**
 * Created on 2019-05-28
 *
 * @author El <bingquan.zhang@honestbee.com>
 */
@RunWith(AndroidJUnit4.class)
class ProfileActivityTest {

    @Test
    public void simple_test() {
        ActivityScenario.launch(SettingsActivity.class);

        Espresso.onView(ViewMatchers.withId(R.id.settings_title))
                .check(ViewAssertions.matches(isDisplayed()));

    }
}
