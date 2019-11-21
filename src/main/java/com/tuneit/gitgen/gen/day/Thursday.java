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
import java.util.Random;

import static com.tuneit.gitgen.gen.GitAPI.*;

@Slf4j
public class Thursday extends Day {
    @Override
    public int check(Variant variant) {
        if (isThursdayPassed(variant)) {
            return new Friday().check(variant);
        }
        return 4;
    }

    private boolean isThursdayPassed(Variant variant) {
        try {
            Repo repo = new Repo(variant);
            String oldCommit = "Second quatrain is added";
            if (getFirstCommit(repo.getOrigin(), "quatrain2").getFullMessage().equals(oldCommit)) {
                doThursday(variant, repo.getOrigin(), new File(variant.getOriginPoem()));
            }

            List<DiffEntry> diffEntries = diffBetweenBranches(repo, "quatrain2", "Second quatrain is fixed");
            return diffEntries != null && diffEntries.isEmpty();
        } catch (IOException e) {
            log.error("Check Thursday is failed", e);
            return false;
        }
    }

    @Override
    public void fix(Variant variant, boolean doTask) {
        new Wednesday().fix(variant, true);

        updateStudRepository(variant);

        if (doTask) {
            Repo repo = new Repo(variant);
            doThursday(variant, repo.getOrigin(), new File(variant.getOriginPoem()));
            doThursday(variant, repo.getStud(), new File(variant.getStudPoem()));
        }
    }

    private void doThursday(Variant variant, Git repository, File poem) {
        String commitName = "Second quatrain is fixed";

        try {
            repository.checkout().setName("quatrain2").call();
            fixFileQuatrain2(variant.getRandom(), poem);
            commit(repository, commitName);
        } catch (GitAPIException | IOException e) {
            log.error("Do Thursday task is failed", e);
        }
    }

    private void fixFileQuatrain2(Random random, File poem) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(poem))) {
            writer.write(Poems.getRandomPoem(random).getQuatrain2());
        }
    }

    private void updateStudRepository(Variant variant) {
        try {
            Repo repo = new Repo(variant);
            repo.getStud().checkout().setName("dev").call();
            repo.getStud().fetch().setRemote("origin").call();
            repo.getStud().reset().setMode(ResetCommand.ResetType.HARD).setRef("origin/dev").call();
        } catch (GitAPIException e) {
            log.error("Update stud Thursday repository is failed", e);
        }
    }

}
