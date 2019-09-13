package com.tuneit.gen.day;

import com.tuneit.data.Poems;
import com.tuneit.data.Variant;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static com.tuneit.gen.GitAPI.*;

public class Friday extends Day {
    @Override
    public Boolean checkTask(Variant variant) {
        try {
            if (!new Thursday().checkTask(variant)) {
                return false;
            }
            init(variant);
            mergeDevAndQuatrain2(variant);

            boolean result = diffBetweenBranches(repo, "refs/heads/dev", "refs/heads/dev").isEmpty();

            if (!result) {
                if (!getFirstCommit(repo.getStud(), "dev").getFullMessage().equals("Merge quatrain1 and quatrain3")) {
                    reset(repo.getStud(), "dev");
                }
            }

            if (result) {
                removeRepo(variant);
            }

            return result;
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void mergeDevAndQuatrain2(Variant variant) throws GitAPIException, IOException {
        String oldCommit = "Merge quatrain1 and quatrain3";
        String commitName = "Merge dev and quatrain2";

        repo.getOrigin().checkout().setName("dev").call();

        ObjectId lastCommit = repo.getOrigin().getRepository().resolve("dev^{commit}");
        RevWalk walker = new RevWalk(repo.getOrigin().getRepository());
        String lastCommitName = walker.parseCommit(lastCommit).getFullMessage();

        if (lastCommitName.equals(oldCommit)) {
            repo.getOrigin().merge().include(repo.getOrigin().getRepository().findRef("quatrain2")).call();
            fixConflict(variant);
            commit(repo.getOrigin(), commitName);
        }
    }

    private void fixConflict(Variant variant) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(poem));

        String quatrain1 = Poems.getRandomPoem(variant.getRandom()).getQuatrain1().trim();
        writer.write(quatrain1);
        writer.write("\n\n");
        String quatrain2 = Poems.getRandomPoem(variant.getRandom()).getQuatrain2().trim();
        writer.write(quatrain2);
        writer.write("\n\n");
        String quatrain3 = Poems.getRandomPoem(variant.getRandom()).getQuatrain3().trim();
        writer.write(quatrain3);

        writer.close();
    }

    @Override
    public void generateTask(Variant variant) {
        try {
            if (!new Thursday().checkTask(variant)) {
                return;
            }
            init(variant);
            updateStudRepository();
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getTaskText() {
        return "Задание пятое: присоедините тертий абзац к ветке dev.";
    }

    private void updateStudRepository() throws GitAPIException {
        repo.getStud().checkout().setName("quatrain2").call();
        repo.getStud().fetch().setRemote("origin").call();
        repo.getStud().reset().setMode(ResetCommand.ResetType.HARD).setRef("origin/quatrain2").call();
    }
}
