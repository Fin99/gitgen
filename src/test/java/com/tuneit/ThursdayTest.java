package com.tuneit;

import com.tuneit.gen.Variant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThursdayTest extends RepoData {
    {
        variant = new Variant(4, "test", 1);
    }

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

        assertTrue(taskService.checkTask(variant).getResult());
    }

    @Test
    void checkErrorTest() {
        makeThursdayWithError();

        assertFalse(taskService.checkTask(variant).getResult());
    }

    @Test
    void checkCorrectionTest() {
        makeThursdayWithError();
        assertFalse(taskService.checkTask(variant).getResult());

        makeThursday();
        assertTrue(taskService.checkTask(variant).getResult());
    }
}
