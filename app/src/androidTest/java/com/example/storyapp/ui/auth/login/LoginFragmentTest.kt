package com.example.storyapp.ui.auth.login

import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.storyapp.R
import com.example.storyapp.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.hamcrest.Matcher
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginFragmentTest {

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testLoginSuccess() {
        launchFragmentInContainer<LoginFragment>(
            themeResId = R.style.Theme_StoryApp
        )
        onView(withId(R.id.emailEditText))
            .perform(typeText("rizki12345@gmail.com"), closeSoftKeyboard())

        onView(withId(R.id.passwordEditText))
            .perform(typeText("rizki12345"), closeSoftKeyboard())

        onView(withId(R.id.loginButton)).perform(click())

        onView(isRoot()).perform(waitFor(10000))

        onView(withId(R.id.rvStory)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginFailed() {
        launchFragmentInContainer<LoginFragment>(
            themeResId = R.style.Theme_StoryApp
        )

        onView(withId(R.id.emailEditText))
            .perform(typeText("invalid@example.com"), closeSoftKeyboard())

        onView(withId(R.id.passwordEditText)).perform(
            typeText("wrong_password"),
            closeSoftKeyboard()
        )

        onView(withId(R.id.loginButton)).perform(click())

        onView(isRoot()).perform(waitFor(4000))

        onView(withId(R.id.fragment_login)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginWithLogOut() {
        launchFragmentInContainer<LoginFragment>(
            themeResId = R.style.Theme_StoryApp
        )

        onView(withId(R.id.emailEditText))
            .perform(typeText("rizki12345@gmail.com"), closeSoftKeyboard())

        onView(withId(R.id.passwordEditText))
            .perform(typeText("rizki12345"), closeSoftKeyboard())

        onView(withId(R.id.loginButton)).perform(click())

        onView(isRoot()).perform(waitFor(10000))

        onView(withId(R.id.rvStory)).check(matches(isDisplayed()))

        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText(R.string.logout)).perform(click())

        onView(withId(R.id.fragment_welcome)).check(matches(isDisplayed()))
    }
}

private fun waitFor(millis: Long): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isRoot()
        }

        override fun getDescription(): String {
            return "wait for $millis milliseconds"
        }

        override fun perform(uiController: UiController, view: View?) {
            uiController.loopMainThreadForAtLeast(millis)
        }
    }
}