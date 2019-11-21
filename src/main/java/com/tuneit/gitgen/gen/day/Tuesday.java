package com.tuneit.gitgen.gen.day;

import com.tuneit.gitgen.data.Poems;
import com.tuneit.gitgen.data.Variant;
import com.tuneit.gitgen.gen.Repo;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static com.tuneit.gitgen.gen.GitAPI.*;

@Slf4j
public class Tuesday extends Day {
    @Override
    public int check(Variant variant) {
        if (isTuesdayPassed(variant)) {
            return new Wednesday().check(variant);
        }
        return 2;
    }

    private boolean isTuesdayPassed(Variant variant) {
        try {
            Repo repo = new Repo(variant);
            String oldCommit = "First quatrain is added";
            if (getFirstCommit(repo.getOrigin(), "quatrain1").getFullMessage().equals(oldCommit)) {
                doTuesday(variant, repo.getOrigin(), new File(variant.getOriginPoem()));
            }

            List<DiffEntry> diffEntries = diffBetweenBranches(repo, "quatrain1", "First quatrain is fixed");
            return diffEntries != null && diffEntries.isEmpty();
        } catch (IOException e) {
            log.error("Check Tuesday is failed", e);
            return false;
        }
    }

    @Override
    public void fix(Variant variant, boolean doTask) {
        new Monday().fix(variant, true);

        updateOriginRepository(variant);
        updateStudRepository(variant);

        if (doTask) {
            Repo repo = new Repo(variant);
            doTuesday(variant, repo.getOrigin(), new File(variant.getOriginPoem()));
            doTuesday(variant, repo.getStud(), new File(variant.getStudPoem()));
        }
    }

    private void doTuesday(Variant variant, Git repository, File poem) {
        String commitName = "First quatrain is fixed";

        try {
            repository.checkout().setName("quatrain1").call();
            fixFileQuatrain1(variant.getRandom(), poem);
            commit(repository, commitName);
        } catch (GitAPIException | IOException e) {
            log.error("Do Tuesday task is failed", e);
        }
    }

    private void fixFileQuatrain1(Random random, File poem) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(poem))) {
            writer.write(Poems.getRandomPoem(random).getQuatrain1());
        }
    }

    private void updateOriginRepository(Variant variant) {
        try {
            Repo repo = new Repo(variant);
            repo.getOrigin().checkout().setCreateBranch(true).setName("quatrain2").setStartPoint("master").call();
            updateFileQuatrain2(variant.getRandom(), new File(variant.getOriginPoem()));
            commit(repo.getOrigin(), "Second quatrain is added");
        } catch (IOException | GitAPIException e) {
            log.error("Update origin Tuesday repository is failed", e);
        }
    }

    private void updateStudRepository(Variant variant) {
        try {
            Repo repo = new Repo(variant);
            repo.getStud().checkout().setName("quatrain3").call();
            repo.getStud().fetch().setRemote("origin").call();
            repo.getStud().reset().setMode(ResetCommand.ResetType.HARD).setRef("origin/quatrain3").call();
            repo.getStud().checkout().setName("origin/quatrain2").call();
            repo.getStud().checkout().setName("quatrain2").setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).setCreateBranch(true).call();
        } catch (GitAPIException e) {
            log.error("Update stud Tuesday repository is failed", e);
        }
    }

    private void updateFileQuatrain2(Random random, File poem) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(poem))) {
            writer.write(Poems.deleteWord(Poems.getRandomPoem(random).getQuatrain2(), random));
        }
    }
}
