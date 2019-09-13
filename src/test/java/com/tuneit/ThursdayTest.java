package com.tuneit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThursdayTest extends RepoData {

    @BeforeEach
    void createTask() {
        makeMonday();
        variant.setDay(2);
        makeTuesday();
        variant.setDay(3);
        makeWednesday();
        variant.setDay(4);
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
