package com.tuneit;

import com.tuneit.gen.Variant;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WednesdayTest extends RepoData {
    {
        variant = new Variant(3, "test", 1);
    }

    @Override
    void createTask() throws IOException, GitAPIException {
        makeMonday();
        variant.setDay(2);
        taskService.generateTask(variant);
        makeTuesday();
        variant.setDay(3);
        taskService.generateTask(variant);
    }

    @Test
    void initTuesdayTest() throws GitAPIException {
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
    public void checkTest() throws IOException, GitAPIException {
        makeWednesday();

        assertTrue(taskService.checkTask(variant));
    }
}
