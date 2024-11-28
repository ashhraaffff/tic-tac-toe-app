package androidsamples.java.tictactoe;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.junit.Before;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TicTacToeEspressoTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void logOut() throws InterruptedException {
        try {
            openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
            onView(withText(R.string.logout)).perform(click());
            Thread.sleep(2000);
        }
        catch (Exception e) {
        }
        onView(withId(R.id.btn_log_in)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginButton() {
        onView(withId(R.id.edit_email)).perform(typeText("f20220389@goa.bits-pilani.ac.in"));
        onView(withId(R.id.edit_password)).perform(typeText("adi123"));
        onView(withId(R.id.btn_log_in)).check(matches(isClickable()));
    }

    @Test
    public void navigateToDashboardFromLogin() throws InterruptedException {
        onView(withId(R.id.edit_email)).perform(typeText("f20220389@goa.bits-pilani.ac.in"));
        onView(withId(R.id.edit_password)).perform(typeText("adi1234"));
        closeSoftKeyboard();
        onView(withId(R.id.btn_log_in)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.won_score)).check(matches(isDisplayed()));
        onView(withId(R.id.lost_score)).check(matches(isDisplayed()));
        onView(withId(R.id.fab_new_game)).check(matches(isDisplayed()));
    }


    @Test
    public void logoutFromDashboardToLogin() throws InterruptedException {
        onView(withId(R.id.edit_email)).perform(typeText("f20220389@goa.bits-pilani.ac.in"));
        onView(withId(R.id.edit_password)).perform(typeText("adi1234"));
        closeSoftKeyboard();
        onView(withId(R.id.btn_log_in)).perform(click());
        Thread.sleep(5000);
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText(R.string.logout)).perform(click());
        onView(withId(R.id.btn_log_in)).check(matches(isDisplayed()));
    }

    @Test
    public void navigateToGameFragmentFromDashboard() throws InterruptedException {
        onView(withId(R.id.edit_email)).perform(typeText("f20220389@goa.bits-pilani.ac.in"));
        onView(withId(R.id.edit_password)).perform(typeText("adi1234"));
        closeSoftKeyboard();
        onView(withId(R.id.btn_log_in)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.fab_new_game)).perform(click());
        onView(withText(R.string.two_player)).perform(click());
        onView(withId(R.id.nicknameText)).check(matches(isDisplayed()));
        onView(withText("OK")).perform(click());
    }

    @Test
    public void testSinglePlayerMode() throws InterruptedException {
        onView(withId(R.id.edit_email)).perform(typeText("f20220389@goa.bits-pilani.ac.in"));
        onView(withId(R.id.edit_password)).perform(typeText("adi1234"));
        closeSoftKeyboard();
        onView(withId(R.id.btn_log_in)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.fab_new_game)).check(matches(isDisplayed())).perform(click());
        onView(withText(R.string.one_player)).perform(click());
        onView(withId(R.id.image1)).check(matches(isDisplayed()));
        onView(withId(R.id.image1)).perform(click());
    }


    @Test
    public void testQuitButton() throws InterruptedException {
        onView(withId(R.id.edit_email)).perform(typeText("f20220389@goa.bits-pilani.ac.in"));
        onView(withId(R.id.edit_password)).perform(typeText("adi1234"));
        closeSoftKeyboard();
        onView(withId(R.id.btn_log_in)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.fab_new_game)).check(matches(isDisplayed())).perform(click());
        onView(withText(R.string.one_player)).perform(click());
        onView(withId(R.id.image1)).check(matches(isDisplayed()));
        onView(withId(R.id.image1)).perform(click());
        onView(withId(R.id.quit_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.quit_btn)).perform(click());
        onView(withText("YES")).perform(click());

    }

}