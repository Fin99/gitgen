package com.tuneit.gitgen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FridayTest extends RepoData {
    @BeforeEach
    void createTask() {
        makeMonday();
        variant.setDay(2);
        makeTuesday();
        variant.setDay(3);
        makeWednesday();
        variant.setDay(4);
        makeThursday();
        variant.setDay(5);
    }

    @Test
    void checkTest() {
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
