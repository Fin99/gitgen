package com.tuneit.gen.day;

import com.tuneit.data.Poems;
import com.tuneit.data.Variant;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static com.tuneit.gen.GitAPI.*;

@Slf4j
public class Wednesday extends Day {
    @Override
    public Boolean checkTask(Variant variant) {
        boolean result = false;
        try {
            if (!new Tuesday().checkTask(variant)) {
                return false;
            }
            init(variant);
            mergeQuatrain1AndQuatrain3(variant);
            result = diffBetweenBranches(repo, "refs/heads/dev", "refs/heads/dev").isEmpty();
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }

        if (!result) {
            try {
                RevWalk revWalk = new RevWalk(repo.getStud().getRepository());
                ObjectId commitId = repo.getStud().getRepository().resolve("refs/heads/dev");
                RevCommit oldCommit = revWalk.parseCommit(commitId);
                revWalk.markStart(oldCommit);

                RevCommit commit = revWalk.next();
                while (commit != null && !commit.getFullMessage().equals("Merge quatrain1 and quatrain3")) {
                    commit = revWalk.next();
                }

                if (commit != null) {
                    result = diffBetweenBranches(repo, "refs/heads/dev", commit.toObjectId().getName()).isEmpty();
                }

                if (!result) {
                    revWalk = new RevWalk(repo.getStud().getRepository());
                    commitId = repo.getStud().getRepository().resolve("refs/heads/dev");
                    oldCommit = revWalk.parseCommit(commitId);
                    if (!oldCommit.getFullMessage().equals("First quatrain is fixed")) {
                        reset(repo.getStud(), "dev");
                    }
                }
            } catch (IOException | GitAPIException e1) {
                log.error(e1.getMessage());
                return false;
            }
        }


        return result;
    }

    private void mergeQuatrain1AndQuatrain3(Variant variant) throws GitAPIException, IOException {
        String oldCommit = "First quatrain is fixed";
        String commitName = "Merge quatrain1 and quatrain3";

        repo.getOrigin().checkout().setName("dev").call();

        ObjectId lastCommit = repo.getOrigin().getRepository().resolve("dev^{commit}");
        RevWalk walker = new RevWalk(repo.getOrigin().getRepository());
        String lastCommitName = walker.parseCommit(lastCommit).getFullMessage();

        if (lastCommitName.equals(oldCommit)) {
            repo.getOrigin().merge().include(repo.getOrigin().getRepository().findRef("quatrain3")).call();
            fixConflict(variant);
            commit(repo.getOrigin(), commitName);
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
    public void generateTask(Variant variant) {
        try {
            if (!new Tuesday().checkTask(variant)) {
                return;
            }
            init(variant);
            createBranchDev();
            updateStudRepository();
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getTaskText() {
        return "Задание третье: соедините первый и второй абзац в ветку dev";
    }

    private void createBranchDev() throws GitAPIException {
        repo.getOrigin().checkout().setName("quatrain1").call();
        repo.getOrigin().checkout().setCreateBranch(true).setName("dev").call();
    }

    private void updateStudRepository() throws GitAPIException {
        repo.getStud().checkout().setName("quatrain1").call();
        repo.getStud().fetch().setRemote("origin").call();
        repo.getStud().reset().setMode(ResetCommand.ResetType.HARD).setRef("origin/quatrain1").call();
        repo.getStud().checkout().setName("origin/dev").call();
        repo.getStud().checkout().setName("dev").setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).setCreateBranch(true).call();
    }
}
