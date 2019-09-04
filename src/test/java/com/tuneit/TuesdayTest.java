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

class TuesdayTest extends RepoData {
    {
        variant = new Variant(2, "test", 1);
    }

    @Override
    void createTask() throws IOException, GitAPIException {
        makeMonday();
        taskService.generateTask(variant);
    }

    @Test
    void initTuesdayTest() throws GitAPIException {
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
    public void checkTest() throws IOException, GitAPIException {
        makeTuesday();

        assertTrue(taskService.checkTask(variant));
    }
}
