package com.tuneit.gitgen.gen.day;

import com.tuneit.gitgen.data.Poems;
import com.tuneit.gitgen.data.Variant;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static com.tuneit.gitgen.gen.GitAPI.*;

public class Tuesday extends Day {
    @Override
    public Boolean checkTask(Variant variant) {
        try {
            if (!new Monday().checkTask(variant)) {
                return false;
            }
            init(variant);
            fixBranchQuatrain1(variant);
            List<DiffEntry> diffEntries = diffBetweenBranches(repo, "quatrain1", "First quatrain is fixed");
            boolean result = diffEntries != null && diffEntries.isEmpty();

            if (!result) {
                if (!getFirstCommit(repo.getStud(), "quatrain1").getFullMessage().equals("First quatrain is added")) {
                    reset(repo.getStud(), "quatrain1");
                }
            }

            return result;
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void fixBranchQuatrain1(Variant variant) throws GitAPIException, IOException {
        String oldCommit = "First quatrain is added";
        String commitName = "First quatrain is fixed";

        if (getFirstCommit(repo.getOrigin(), "quatrain1").getFullMessage().equals(oldCommit)) {
            repo.getOrigin().checkout().setName("quatrain1").call();
            fixFileQuatrain1(variant.getRandom());
            commit(repo.getOrigin(), commitName);
        }
    }

    private void fixFileQuatrain1(Random random) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(poem))) {
            writer.write(Poems.getRandomPoem(random).getQuatrain1());
        }
    }

    @Override
    public void generateTask(Variant variant) {
        try {
            if (!new Monday().checkTask(variant)) {
                return;
            }
            init(variant);
            createBranchQuatrain2(variant);
            updateStudRepository();
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getTaskText() {
        return "Задание второе: исправь ошибки в первом абзаце.";
    }

    private void createBranchQuatrain2(Variant variant) throws IOException, GitAPIException {
        repo.getOrigin().checkout().setCreateBranch(true).setName("quatrain2").setStartPoint("master").call();
        updateFileQuatrain2(variant.getRandom());
        commit(repo.getOrigin(), "Second quatrain is added");
    }

    private void updateStudRepository() throws GitAPIException {
        repo.getStud().checkout().setName("quatrain3").call();
        repo.getStud().fetch().setRemote("origin").call();
        repo.getStud().reset().setMode(ResetCommand.ResetType.HARD).setRef("origin/quatrain3").call();
        repo.getStud().checkout().setName("origin/quatrain2").call();
        repo.getStud().checkout().setName("quatrain2").setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).setCreateBranch(true).call();
    }

    private void updateFileQuatrain2(Random random) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(poem))) {
            writer.write(Poems.deleteWord(Poems.getRandomPoem(random).getQuatrain2(), random));
        }
    }
}
