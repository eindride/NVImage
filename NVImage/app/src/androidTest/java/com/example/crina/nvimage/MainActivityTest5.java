package com.example.crina.nvimage;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest5 {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest5() {
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.upload_button), isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.Ghost),
                        withParent(withId(R.id.LL1))));
        appCompatImageButton2.perform(scrollTo(), click());

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_modif), isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withText("Horizontal"),
                        withParent(withId(R.id.LL2))));
        appCompatButton.perform(scrollTo(), click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withText("Rotate"),
                        withParent(withId(R.id.LL2))));
        appCompatButton2.perform(scrollTo(), click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.undo), withText("Undo"),
                        withParent(allOf(withId(R.id.container),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.undo), withText("Undo"),
                        withParent(allOf(withId(R.id.container),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction appCompatButton5 = onView(
                allOf(withText("Vertical"),
                        withParent(withId(R.id.LL2))));
        appCompatButton5.perform(scrollTo(), click());

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.navigation_adv), isDisplayed()));
        bottomNavigationItemView2.perform(click());

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.button), withText("RGB"),
                        withParent(withId(R.id.LL3))));
        appCompatButton6.perform(scrollTo(), click());

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withId(R.id.back2), isDisplayed()));
        appCompatImageButton3.perform(click());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.saveButton), withText("SAVE"),
                        withParent(allOf(withId(R.id.container),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        appCompatButton7.perform(click());

    }

}
