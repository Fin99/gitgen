package com.tuneit;

import com.tuneit.gen.Variant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FridayTest extends RepoData {
    {
        variant = new Variant(5, "test", 1);
    }

    @BeforeEach
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

    @Override
    void deleteRepo() {

    }

    @Test
    void checkTest() throws IOException {
        makeFriday();

        assertFalse(new File(variant.getStudDirName()).exists());
        assertFalse(new File(variant.getOriginDirName()).exists());
    }

    @Test
    void checkErrorTest() {
        makeFridayWithError();

        assertTrue(new File(variant.getStudDirName()).exists());
        assertTrue(new File(variant.getOriginDirName()).exists());
    }

    @Test
    void checkCorrectionTest() {
        makeFridayWithError();
        assertTrue(new File(variant.getStudDirName()).exists());
        assertTrue(new File(variant.getOriginDirName()).exists());

        makeFriday();
        assertFalse(new File(variant.getStudDirName()).exists());
        assertFalse(new File(variant.getOriginDirName()).exists());
    }
}
