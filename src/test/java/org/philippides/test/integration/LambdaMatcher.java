package org.philippides.test.integration;

import java.util.function.Predicate;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class LambdaMatcher<T> extends BaseMatcher<T> {
    private Predicate<T> predicate;
    private String description;

    public LambdaMatcher(Predicate<T> predicate, String description) {
        this.predicate = predicate;
        this.description = description;
    }

    @Override
    public boolean matches(Object item) {
        return predicate.test((T)item);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(this.description);
    }
}
