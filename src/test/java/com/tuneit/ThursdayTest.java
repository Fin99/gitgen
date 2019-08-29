package com.tuneit;

import com.tuneit.gen.Variant;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ThursdayTest extends RepoData {
    {
        variant = new Variant(4, "test", 1);
    }

    @Override
    void createTask() throws IOException, GitAPIException {
        makeMonday();
        variant.setDay(2);
        taskService.generateTask(variant);
        makeTuesday();
        variant.setDay(3);
        taskService.generateTask(variant);
        makeWednesday();
        variant.setDay(4);
        taskService.generateTask(variant);
    }

    @Test
    public void checkTest() throws IOException, GitAPIException {
        makeThursday();

        assertTrue(taskService.checkTask(variant));
    }
}