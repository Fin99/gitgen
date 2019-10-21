package com.tuneit.gitgen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FridayTest extends RepoData {
    @BeforeEach
    void createTask() {
        makeMonday();
        makeTuesday();
        makeWednesday();
        makeThursday();
    }

    @Test
    void checkTest() {
        makeFriday();
        assertEquals(6 ,bashService.getDay(variant));
    }

    @Test
    void checkErrorTest() {
        makeFridayWithError();
        assertEquals(5, bashService.getDay(variant));
    }

    @Test
    void checkCorrectionTest() {
        makeFridayWithError();
        assertEquals(5, bashService.getDay(variant));

        makeFriday();
        assertEquals(6, bashService.getDay(variant));
    }
}
