package com.tuneit;

import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TuesdayTest extends RepoData {

    @BeforeEach
    void createTask() {
        makeMonday();
    }

    @Test
    void initTuesdayTest() throws GitAPIException, IOException {
        initGit();
        List<Ref> branches = gitStud.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();

        assertEquals(8, branches.size());
        branches.removeIf(branch -> branch.getName().equals("refs/heads/quatrain2"));
        assertEquals(7, branches.size());
        branches.removeIf(branch -> branch.getName().equals("refs/remotes/origin/master"));
        assertEquals(6, branches.size());

        branches = gitOrigin.branchList().call();

        assertEquals(4, branches.size());
    }

    @Test
    void checkTest() {
        makeTuesday();

        assertTrue(taskService.checkTask(variant));
    }

    @Test
    void checkErrorTest() {
        makeTuesdayWithError();

        assertFalse(taskService.checkTask(variant));
    }

    @Test
    void checkCorrectionTest() {
        makeTuesdayWithError();
        assertFalse(taskService.checkTask(variant));

        makeTuesday();
        assertTrue(taskService.checkTask(variant));
    }
}
