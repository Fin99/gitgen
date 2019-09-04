package com.tuneit;

import com.tuneit.gen.Variant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FridayTest extends RepoData {
    {
        variant = new Variant(5, "test", 1);
    }

    @Override
    void createTask() {
        makeMonday();
        variant.setDay(2);
        taskService.generateTask(variant);
        makeTuesday();
        variant.setDay(3);
        taskService.generateTask(variant);
        makeWednesday();
        variant.setDay(4);
        taskService.generateTask(variant);
        makeThursday();
        variant.setDay(5);
        taskService.generateTask(variant);
    }

    @Test
    void checkTest() {
        makeFriday();

        assertTrue(taskService.checkTask(variant));
    }

    @Test
    void checkErrorTest() {
        makeFridayWithError();

        assertFalse(taskService.checkTask(variant));
    }

    @Test
    void checkCorrectionTest() {
        makeFridayWithError();
        assertFalse(taskService.checkTask(variant));

        makeFriday();
        assertTrue(taskService.checkTask(variant));
    }
}
