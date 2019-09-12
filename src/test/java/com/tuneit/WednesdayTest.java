package com.tuneit;

import com.tuneit.gen.Variant;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WednesdayTest extends RepoData {
    {
        variant = new Variant(3, "test", 1);
    }

    @BeforeEach
    void createTask() {
        makeMonday();
        variant.setDay(2);
        makeTuesday();
        variant.setDay(3);
    }

    @Test
    void initTuesdayTest() throws GitAPIException, IOException {
        initGit();
        List<Ref> branches = gitStud.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();

        assertEquals(10, branches.size());
        branches.removeIf(branch -> branch.getName().equals("refs/heads/dev"));
        assertEquals(9, branches.size());
        branches.removeIf(branch -> branch.getName().equals("refs/remotes/origin/dev"));
        assertEquals(8, branches.size());

        branches = gitOrigin.branchList().call();

        assertEquals(5, branches.size());
    }

    @Test
    void checkTest() {
        makeWednesday();

        assertTrue(taskService.checkTask(variant).getResult());
    }

    @Test
    void checkErrorTest() {
        makeWednesdayWithError();

        assertFalse(taskService.checkTask(variant).getResult());
    }

    @Test
    void checkCorrectionTest() {
        makeWednesdayWithError();
        assertFalse(taskService.checkTask(variant).getResult());

        makeWednesday();
        assertTrue(taskService.checkTask(variant).getResult());
    }
}
