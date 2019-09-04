package com.tuneit;

import com.tuneit.gen.Variant;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MondayTest extends RepoData {

    {
        variant = new Variant(1, "test", 1);
    }

    @Override
    void createTask() {
    }

    @Test
    void initMondayTest() throws GitAPIException {
        assertTrue(dirStud.exists());
        assertTrue(dirOrigin.exists());

        assertTrue(poemStud.exists());
        assertTrue(poemOrigin.exists());

        List<Ref> branches = gitStud.branchList().call();

        assertEquals(3, branches.size());
        branches.removeIf(branch -> branch.getName().equals("refs/heads/master"));
        assertEquals(2, branches.size());
        branches.removeIf(branch -> branch.getName().equals("refs/heads/quatrain1"));
        assertEquals(1, branches.size());
        branches.removeIf(branch -> branch.getName().equals("refs/heads/quatrain3"));
        assertTrue(branches.isEmpty());

        branches = gitOrigin.branchList().call();

        assertEquals(3, branches.size());
        branches.removeIf(branch -> branch.getName().equals("refs/heads/master"));
        assertEquals(2, branches.size());
        branches.removeIf(branch -> branch.getName().equals("refs/heads/quatrain1"));
        assertEquals(1, branches.size());
        branches.removeIf(branch -> branch.getName().equals("refs/heads/quatrain3"));
        assertTrue(branches.isEmpty());
    }

    @Test
    void checkTest() {
        makeMonday();

        assertTrue(taskService.checkTask(variant));
    }

    @Test
    void checkErrorTest() {
        makeMondayWithError();

        assertFalse(taskService.checkTask(variant));
    }

    @Test
    void checkCorrectionTest() {
        makeMondayWithError();
        assertFalse(taskService.checkTask(variant));

        makeMonday();
        assertTrue(taskService.checkTask(variant));
    }
}
