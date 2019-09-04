package com.tuneit;

import com.tuneit.gen.Variant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThursdayTest extends RepoData {
    {
        variant = new Variant(4, "test", 1);
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
    }

    @Test
    void checkTest() {
        makeThursday();

        assertTrue(taskService.checkTask(variant));
    }

    @Test
    void checkErrorTest() {
        makeThursdayWithError();

        assertFalse(taskService.checkTask(variant));
    }

    @Test
    void checkCorrectionTest() {
        makeThursdayWithError();
        assertFalse(taskService.checkTask(variant));

        makeThursday();
        assertTrue(taskService.checkTask(variant));
    }
}
