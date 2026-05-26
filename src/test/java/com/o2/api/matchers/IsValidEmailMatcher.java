package com.o2.api.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class IsValidEmailMatcher extends TypeSafeMatcher<String> {

    private IsValidEmailMatcher() {
    }

    public static IsValidEmailMatcher isValidEmail() {
        return new IsValidEmailMatcher();
    }

    @Override
    protected boolean matchesSafely(String email) {
        return email != null && email.contains("@") && email.indexOf("@") > 0
                && email.indexOf("@") < email.length() - 1;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a valid email address containing '@'");
    }

    @Override
    protected void describeMismatchSafely(String item, Description mismatchDescription) {
        mismatchDescription.appendText("was ").appendValue(item);
    }
}