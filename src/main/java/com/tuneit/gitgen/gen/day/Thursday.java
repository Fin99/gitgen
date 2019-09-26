package com.tuneit.gitgen.gen.day;

import com.tuneit.gitgen.data.Poems;
import com.tuneit.gitgen.data.Variant;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static com.tuneit.gitgen.gen.GitAPI.*;

public class Thursday extends Day {
    @Override
    public Boolean checkTask(Variant variant) {
        try {
            if (!new Wednesday().checkTask(variant)) {
                return false;
            }
            init(variant);
            fixBranchQuatrain2(variant);
            List<DiffEntry> diffEntries = diffBetweenBranches(repo, "quatrain2", "Second quatrain is fixed");
            boolean result = diffEntries != null && diffEntries.isEmpty();

            if (!result) {
                if (!getFirstCommit(repo.getStud(), "quatrain2").getFullMessage().equals("Second quatrain is added")) {
                    reset(repo.getStud(), "quatrain2");
                }
            }

            return result;
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void fixBranchQuatrain2(Variant variant) throws GitAPIException, IOException {
        String oldCommit = "Second quatrain is added";
        String commitName = "Second quatrain is fixed";

        if (getFirstCommit(repo.getOrigin(), "quatrain2").getFullMessage().equals(oldCommit)) {
            repo.getOrigin().checkout().setName("quatrain2").call();
            fixFileQuatrain2(variant.getRandom());
            commit(repo.getOrigin(), commitName);
        }
    }

    private void fixFileQuatrain2(Random random) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(poem))) {
            writer.write(Poems.getRandomPoem(random).getQuatrain2());
        }
    }

    @Override
    public void generateTask(Variant variant) {
        try {
            if (!new Wednesday().checkTask(variant)) {
                return;
            }
            init(variant);
            updateStudRepository();
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getTaskText() {
        return "Задание четвертое: исправь ошибки во втором абзаце.";
    }

    private void updateStudRepository() throws GitAPIException {
        repo.getStud().checkout().setName("dev").call();
        repo.getStud().fetch().setRemote("origin").call();
        repo.getStud().reset().setMode(ResetCommand.ResetType.HARD).setRef("origin/dev").call();
    }
}
