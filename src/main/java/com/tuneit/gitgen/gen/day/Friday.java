package com.tuneit.gitgen.gen.day;

import com.tuneit.gitgen.data.Poems;
import com.tuneit.gitgen.data.Variant;
import com.tuneit.gitgen.gen.Repo;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static com.tuneit.gitgen.gen.GitAPI.*;

@Slf4j
public class Friday extends Day {

    @Override
    public int check(Variant variant, Integer day) {
        if (day != 5 && isFridayPassed(variant)) {
            return 6;
        }
        return 5;
    }

    @Override
    public int check(Variant variant) {
        return check(variant, 6);
    }

    private boolean isFridayPassed(Variant variant) {
        try {
            Repo repo = new Repo(variant);
            String oldCommit = "Merge quatrain1 and quatrain3";
            if (getFirstCommit(repo.getOrigin(), "dev").getFullMessage().equals(oldCommit)) {
                doFriday(variant, repo.getOrigin(), new File(variant.getOriginPoem()));
            }

            List<DiffEntry> diffEntries = diffBetweenBranches(repo, "dev", "Merge dev and quatrain2");
            return diffEntries != null && diffEntries.isEmpty();
        } catch (IOException e) {
            log.error("Check Friday is failed", e);
            return false;
        }
    }

    @Override
    public void fix(Variant variant, boolean doTask) {
        new Thursday().fix(variant, true);

        updateStudRepository(variant);

        if (doTask) {
            Repo repo = new Repo(variant);
            doFriday(variant, repo.getOrigin(), new File(variant.getOriginPoem()));
            doFriday(variant, repo.getStud(), new File(variant.getStudPoem()));
        }
    }

    private void doFriday(Variant variant, Git repository, File poem) {
        String commitName = "Merge dev and quatrain2";

        try {
            repository.checkout().setName("dev").call();
            repository.merge().include(repository.getRepository().findRef("quatrain2")).call();
            fixConflict(variant, poem);
            commit(repository, commitName);
        } catch (GitAPIException | IOException e) {
            log.error("Do Friday task is failed", e);
        }
    }

    private void fixConflict(Variant variant, File poem) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(poem))) {
            writer.write(Poems.getRandomPoem(variant.getRandom()).getQuatrain1());
            writer.write("\n\n");
            writer.write(Poems.getRandomPoem(variant.getRandom()).getQuatrain2());
            writer.write("\n\n");
            writer.write(Poems.getRandomPoem(variant.getRandom()).getQuatrain3());
        }
    }

    private void updateStudRepository(Variant variant) {
        try {
            Repo repo = new Repo(variant);
            repo.getStud().checkout().setName("quatrain2").call();
            repo.getStud().fetch().setRemote("origin").call();
            repo.getStud().reset().setMode(ResetCommand.ResetType.HARD).setRef("origin/quatrain2").call();
        } catch (GitAPIException e) {
            log.error("Update stud Friday repository is failed", e);
        }
    }
}
