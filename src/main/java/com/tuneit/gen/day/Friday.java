package com.tuneit.gen.day;

import com.tuneit.bash.CommandResult;
import com.tuneit.gen.Poems;
import com.tuneit.gen.Variant;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Friday extends Day {
    @Override
    public Boolean checkTask(Variant variant) {
        boolean result = false;
        try {
            if (!new Thursday().checkTask(variant)) {
                throw new UnsupportedOperationException("Thursday check task is failed");
            }
            init(variant);
            mergeDevAndQuatrain2(variant);
            result = diffBetweenBranches("refs/heads/dev", "refs/heads/dev");

            if (!result) {
                reset("dev");
            }

            if (result) {
                removeRepo(variant);
            }
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void mergeDevAndQuatrain2(Variant variant) throws GitAPIException, IOException {
        String oldCommit = "Merge quatrain1 and quatrain3";
        String commitName = "Merge dev and quatrain2";

        origin.checkout().setName("dev").call();

        ObjectId lastCommit = origin.getRepository().resolve("dev^{commit}");
        RevWalk walker = new RevWalk(origin.getRepository());
        String lastCommitName = walker.parseCommit(lastCommit).getFullMessage();

        if (lastCommitName.equals(oldCommit)) {
            origin.merge().include(origin.getRepository().findRef("quatrain2")).call();
            fixConflict(variant);
            commit(origin, commitName);
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
    public CommandResult generateTask(Variant variant) {
        try {
            if (!new Thursday().checkTask(variant)) {
                throw new UnsupportedOperationException("Thursday check task is failed");
            }
            init(variant);
            updateStudRepository();
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateStudRepository() throws GitAPIException {
        stud.checkout().setName("quatrain2").call();
        stud.fetch().setRemote("origin").call();
        stud.reset().setMode(ResetCommand.ResetType.HARD).setRef("origin/quatrain2").call();
    }
}
