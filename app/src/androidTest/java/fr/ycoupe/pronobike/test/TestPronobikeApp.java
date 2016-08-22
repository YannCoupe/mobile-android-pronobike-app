package fr.ycoupe.pronobike.test;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.activities.LoginActivity;
import fr.ycoupe.pronobike.models.Pilot;
import fr.ycoupe.pronobike.sqlite.PilotDAO;
import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by yanncoupe on 17/08/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestPronobikeApp {

    @ClassRule
    public static final TestRule classRule = new LocaleTestRule();

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void ScreenshotTest() throws Exception{

        onView(withId(R.id.auth_edittext_email)).perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.auth_edittext_password)).perform(clearText(), closeSoftKeyboard());

        // Login connect
        onView(withId(R.id.auth_edittext_email)).perform(clearText(), typeText("nicklaus.young@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.auth_edittext_password)).perform(clearText(), typeText("nicklaus"), closeSoftKeyboard());

        sleep(1);

        Screengrab.screenshot("Login");

        onView(withId(R.id.auth_button_connect)).perform(click());

        sleep(4);

        Screengrab.screenshot("Games");

        onView(withId(R.id.game_list)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        Screengrab.screenshot("GameDetails");

        onView(withId(R.id.game_detail_list)).perform(click());

        Screengrab.screenshot("Latests");

        Espresso.pressBack();

        onView(withId(R.id.game_detail_button_bet)).perform(click());

        final ArrayList<Pilot> pilots = PilotDAO.pilotsWithIdGame(InstrumentationRegistry.getTargetContext(), 42);

        String [] array = new String[pilots.size()];
        for(int i = 0; i < pilots.size(); i++){
            array[i] = pilots.get(i).getNumber() + " " + pilots.get(i).getFirstname() + " " + pilots.get(i).getLastname();
        }

        onView(withId(R.id.bet_rank_first)).perform(click());
        sleep(1);
        onView(withText(array[1])).inRoot(isDialog()).perform(click());
        sleep(1);
        onView(withId(R.id.bet_rank_second)).perform(click());
        sleep(1);
        onView(withText(array[2])).inRoot(isDialog()).perform(click());
        sleep(1);
        onView(withId(R.id.bet_rank_third)).perform(click());
        sleep(1);
        onView(withText(array[5])).inRoot(isDialog()).perform(click());
        sleep(1);

        Screengrab.screenshot("Forecast");

        Espresso.pressBack();
        sleep(1);
        Espresso.pressBack();
        sleep(1);

        swipe(true);

        Screengrab.screenshot("Rank");

    }

    private void swipe(final boolean left){
        sleep(1);

        onView(withId(R.id.main_viewpager)).perform(left ? swipeLeft() : swipeRight());

        sleep(1);
    }

    private void sleep(final int second){
        try { Thread.sleep(second * 1000); } catch(InterruptedException e) {}
    }

    @NonNull
    public static ViewInteraction getRootView(@NonNull Activity activity, @NonNull String text) {
        return onView(withText(text)).inRoot(withDecorView(not(is(activity.getWindow().getDecorView()))));
    }

}
