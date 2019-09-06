package com.tuneit.gen.day;

import com.tuneit.gen.Poems;
import com.tuneit.gen.Task;
import com.tuneit.gen.Variant;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Wednesday extends Day {
    @Override
    public Boolean checkTask(Variant variant) {
        boolean result = false;
        try {
            if (!new Tuesday().checkTask(variant)) {
                throw new UnsupportedOperationException("Tuesday check task is failed");
            }
            init(variant);
            mergeQuatrain1AndQuatrain3(variant);
            result = diffBetweenBranches("refs/heads/dev", "refs/heads/dev");
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }

        if (!result) {
            try {
                RevWalk revWalk = new RevWalk(stud.getRepository());
                ObjectId commitId = stud.getRepository().resolve("refs/heads/dev");
                RevCommit oldCommit = revWalk.parseCommit(commitId);
                revWalk.markStart(oldCommit);

                RevCommit commit = revWalk.next();
                while (commit != null && !commit.getFullMessage().equals("Merge quatrain1 and quatrain3")) {
                    commit = revWalk.next();
                }

                if (commit != null) {
                    result = diffBetweenBranches("refs/heads/dev", commit.toObjectId().getName());
                }

                if (!result) {
                    reset("dev");
                }
            } catch (IOException | GitAPIException e1) {
                e1.printStackTrace();
            }
        }

        return result;
    }

    private void mergeQuatrain1AndQuatrain3(Variant variant) throws GitAPIException, IOException {
        String oldCommit = "First quatrain is fixed";
        String commitName = "Merge quatrain1 and quatrain3";

        origin.checkout().setName("dev").call();

        ObjectId lastCommit = origin.getRepository().resolve("dev^{commit}");
        RevWalk walker = new RevWalk(origin.getRepository());
        String lastCommitName = walker.parseCommit(lastCommit).getFullMessage();

        if (lastCommitName.equals(oldCommit)) {
            origin.merge().include(origin.getRepository().findRef("quatrain3")).call();
            fixConflict(variant);
            commit(origin, commitName);
        }
    }

    private void fixConflict(Variant variant) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(poem));
        String quatrain1 = Poems.getRandomPoem(variant.getRandom()).getQuatrain1().trim();
        writer.write(quatrain1);
        writer.write("\n\n");
        String quatrain3 = Poems.getRandomPoem(variant.getRandom()).getQuatrain3().trim();
        writer.write(quatrain3);
        writer.close();
    }

    @Override
    public Task generateTask(Variant variant) {
        try {
            if (!new Tuesday().checkTask(variant)) {
                throw new UnsupportedOperationException("Tuesday check task is failed");
            }
            init(variant);
            createBranchDev();
            updateStudRepository();
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createBranchDev() throws GitAPIException {
        origin.checkout().setName("quatrain1").call();
        origin.checkout().setCreateBranch(true).setName("dev").call();
    }

    private void updateStudRepository() throws GitAPIException {
        stud.checkout().setName("quatrain1").call();
        stud.fetch().setRemote("origin").call();
        stud.reset().setMode(ResetCommand.ResetType.HARD).setRef("origin/quatrain1").call();
        stud.checkout().setName("origin/dev").call();
        stud.checkout().setName("dev").setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).setCreateBranch(true).call();
    }
}
