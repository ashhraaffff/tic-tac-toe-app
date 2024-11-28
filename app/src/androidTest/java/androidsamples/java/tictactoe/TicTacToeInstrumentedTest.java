package androidsamples.java.tictactoe;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TicTacToeInstrumentedTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);
    @Before
    public void ensureLoggedOut() throws InterruptedException {
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
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("androidsamples.java.tictactoe", appContext.getPackageName());
    }
    @Test
    public void testLoginAndDashboard() throws InterruptedException {
        onView(withId(R.id.edit_email))
                .perform(typeText("f20220389@goa.bits-pilani.ac.in"));
        onView(withId(R.id.edit_password))
                .perform(typeText("adi1234"));
        closeSoftKeyboard();

        onView(withId(R.id.btn_log_in)).perform(click());
        Thread.sleep(5000);

        onView(withId(R.id.won_score))
                .check(matches(isDisplayed()));
        onView(withId(R.id.lost_score))
                .check(matches(isDisplayed()));
        onView(withId(R.id.fab_new_game))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testCreateOnePlayerGameAndWin() throws InterruptedException {
        onView(withId(R.id.edit_email)).perform(replaceText("f20220389@goa.bits-pilani.ac.in"));
        onView(withId(R.id.edit_password)).perform(replaceText("adi1234"));

        onView(withId(R.id.btn_log_in)).perform(click());

        Thread.sleep(3000);


        final int[] initialWins = {0};
        ActivityScenario<MainActivity> mainScenario = ActivityScenario.launch(MainActivity.class);
        mainScenario.onActivity(activity -> {
            String winsText = ((TextView) activity.findViewById(R.id.won_score)).getText().toString();
            initialWins[0] = Integer.parseInt(winsText);
        });

        onView(withId(R.id.fab_new_game)).perform(click());

        onView(withText("Which type of game do you want to create?"))
                .check(matches(isDisplayed()));

        onView(withText("ONE-PLAYER")).perform(click());

        //till here its working
        mainScenario.onActivity(activity -> {
            GameFragment gameFragment = (GameFragment) activity.getSupportFragmentManager()
                    .findFragmentById(R.id.gameContainer);

            if (gameFragment != null) {
                List<Integer> mockMoves = Arrays.asList(4, 5, 6);
                gameFragment.setAIManually(true, mockMoves);
            }
        });
        onView(withId(R.id.image1)).perform(click());
        onView(withId(R.id.image2)).perform(click());
        onView(withId(R.id.image3)).perform(click());

        onView(withText("Congratulations!"))
                .check(matches(isDisplayed()));

        onView(withText("OK")).perform(click());

        mainScenario.onActivity(activity -> {
            String updatedWinsText = ((TextView) activity.findViewById(R.id.won_score)).getText().toString();
            int updatedWins = Integer.parseInt(updatedWinsText);
            assertEquals(initialWins[0] + 1, updatedWins);
        });
    }

    @Test
    public void testCreateOnePlayerGameAndLose() throws InterruptedException {
        onView(withId(R.id.edit_email)).perform(replaceText("f20220389@goa.bits-pilani.ac.in"));
        onView(withId(R.id.edit_password)).perform(replaceText("adi1234"));

        onView(withId(R.id.btn_log_in)).perform(click());

        Thread.sleep(3000);


        final int[] initialLosses = {0};
        ActivityScenario<MainActivity> mainScenario = ActivityScenario.launch(MainActivity.class);
        mainScenario.onActivity(activity -> {
            String winsText = ((TextView) activity.findViewById(R.id.lost_score)).getText().toString();
            initialLosses[0] = Integer.parseInt(winsText);
        });

        onView(withId(R.id.fab_new_game)).perform(click());

        onView(withText("Which type of game do you want to create?"))
                .check(matches(isDisplayed()));

        onView(withText("ONE-PLAYER")).perform(click());

        mainScenario.onActivity(activity -> {

            GameFragment gameFragment = (GameFragment) activity.getSupportFragmentManager()
                    .findFragmentById(R.id.gameContainer);

            if (gameFragment != null) {
                List<Integer> mockMoves = Arrays.asList(4, 5, 6);
                gameFragment.setAIManually(true, mockMoves);
            }
        });
        onView(withId(R.id.image1)).perform(click());
        onView(withId(R.id.image2)).perform(click());
        onView(withId(R.id.image7)).perform(click());

        onView(withText("Sorry!"))
                .check(matches(isDisplayed()));

        onView(withText("OK")).perform(click());

        mainScenario.onActivity(activity -> {
            String updatedLossesText = ((TextView) activity.findViewById(R.id.lost_score)).getText().toString();
            int updatedLosses = Integer.parseInt(updatedLossesText);
            assertEquals(initialLosses[0] + 1, updatedLosses);
        });
    }

    @Test
    public void testCreateOnePlayerGameAndForfeit() throws InterruptedException {
        // Login first
        onView(withId(R.id.edit_email)).perform(replaceText("f20220389@goa.bits-pilani.ac.in"));
        onView(withId(R.id.edit_password)).perform(replaceText("adi1234"));

        onView(withId(R.id.btn_log_in)).perform(click());

        Thread.sleep(3000);


        final int[] initialLosses = {0};
        ActivityScenario<MainActivity> mainScenario = ActivityScenario.launch(MainActivity.class);
        mainScenario.onActivity(activity -> {
            String winsText = ((TextView) activity.findViewById(R.id.lost_score)).getText().toString();
            initialLosses[0] = Integer.parseInt(winsText);
        });

        onView(withId(R.id.fab_new_game)).perform(click());

        onView(withText("Which type of game do you want to create?"))
                .check(matches(isDisplayed()));

        onView(withText("ONE-PLAYER")).perform(click());

        mainScenario.onActivity(activity -> {
            GameFragment gameFragment = (GameFragment) activity.getSupportFragmentManager()
                    .findFragmentById(R.id.gameContainer);

            if (gameFragment != null) {
                // Set mock AI moves before the game begins
                List<Integer> mockMoves = Arrays.asList(4, 5, 6);
                gameFragment.setAIManually(true, mockMoves);
            }
        });
        onView(withId(R.id.image1)).perform(click());
        onView(withId(R.id.quit_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.quit_btn)).perform(click());
        onView(withText("YES")).perform(click());

        mainScenario.onActivity(activity -> {
            String updatedLossesText = ((TextView) activity.findViewById(R.id.lost_score)).getText().toString();
            int updatedLosses = Integer.parseInt(updatedLossesText);
            assertEquals(initialLosses[0], updatedLosses);
        });
    }
}