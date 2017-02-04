package io.yawp.repository.actions.parents;

import io.yawp.commons.http.annotation.GET;
import io.yawp.repository.actions.Action;
import io.yawp.repository.models.parents.Parent;

import java.util.Arrays;
import java.util.List;

public class ParentJava8Action extends Action<Parent> {

    @GET
    public String java8() {
        List<String> letters = Arrays.asList("x", "p", "t", "o");
        return letters.stream().reduce((s, l) -> s + l).get();
    }
}
