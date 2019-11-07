package com.tuneit.gitgen.gen.day;

import com.tuneit.gitgen.data.Poems;
import com.tuneit.gitgen.data.Variant;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static com.tuneit.gitgen.gen.GitAPI.*;

@Slf4j
public class Wednesday extends Day {
    @Override
    public Boolean checkTask(Variant variant) {
        try {
            if (!new Tuesday().checkTask(variant)) {
                return false;
            }
            init(variant);
            generateTask(variant);
            mergeQuatrain1AndQuatrain3(variant);
            List<DiffEntry> diffEntries = diffBetweenBranches(repo, "dev", "Merge quatrain1 and quatrain3");
            boolean result = diffEntries != null && diffEntries.isEmpty();

            if (!result) {
                if (!getFirstCommit(repo.getStud(), "dev").getFullMessage().equals("First quatrain is fixed")) {
                    reset(repo.getStud(), "dev");
                }
            }

            return result;
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void mergeQuatrain1AndQuatrain3(Variant variant) throws GitAPIException, IOException {
        String oldCommit = "First quatrain is fixed";
        String commitName = "Merge quatrain1 and quatrain3";

        if (getFirstCommit(repo.getOrigin(), "dev").getFullMessage().equals(oldCommit)) {
            repo.getOrigin().checkout().setName("dev").call();
            repo.getOrigin().merge().include(repo.getOrigin().getRepository().findRef("quatrain3")).call();
            fixConflict(variant);
            commit(repo.getOrigin(), commitName);
        }
    }

    private void fixConflict(Variant variant) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(poem))) {
            writer.write(Poems.getRandomPoem(variant.getRandom()).getQuatrain1());
            writer.write("\n\n");
            writer.write(Poems.getRandomPoem(variant.getRandom()).getQuatrain3());
        }
    }

    @Override
    public void generateTask(Variant variant) {
        try {
            if (!new Tuesday().checkTask(variant)) {
                return;
            }
            init(variant);
            if (repo.getOrigin().branchList().call().stream().noneMatch(ref -> ref.getName().contains("refs/heads/dev"))) {
                createBranchDev();
                updateStudRepository();
            }
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getTaskText() {
        return "Задание третье: соедините первый и третий абзац в ветку dev. " +
                "Абзацы должны разделяться пустой строкой, а первый абзац стихотворения уже добавили в ветку dev.";
    }

    private void createBranchDev() throws GitAPIException {
        repo.getOrigin().checkout().setCreateBranch(true).setStartPoint("quatrain1").setName("dev").call();
    }

    private void updateStudRepository() throws GitAPIException {
        repo.getStud().checkout().setName("quatrain1").call();
        repo.getStud().fetch().setRemote("origin").call();
        repo.getStud().reset().setMode(ResetCommand.ResetType.HARD).setRef("origin/quatrain1").call();
        repo.getStud().checkout().setName("origin/dev").call();
        repo.getStud().checkout().setName("dev").setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).setCreateBranch(true).call();
    }
}
