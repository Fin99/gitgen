package com.tuneit.gen.day;

import com.tuneit.data.Poems;
import com.tuneit.data.Variant;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import static com.tuneit.gen.GitAPI.*;

public class Tuesday extends Day {
    @Override
    public Boolean checkTask(Variant variant) {
        try {
            if (!new Monday().checkTask(variant)) {
                return false;
            }
            init(variant);
            fixBranchQuatrain1(variant);
            boolean result = diffBetweenBranches(repo, "refs/heads/quatrain1", "refs/heads/quatrain1").isEmpty();

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

        repo.getOrigin().checkout().setName("quatrain1").call();

        ObjectId lastCommit = repo.getOrigin().getRepository().resolve("quatrain1^{commit}");
        RevWalk walker = new RevWalk(repo.getOrigin().getRepository());
        String lastCommitName = walker.parseCommit(lastCommit).getFullMessage();

        if (lastCommitName.equals(oldCommit)) {
            fixFileQuatrain1(variant.getRandom());
            commit(repo.getOrigin(), commitName);
        }
    }

    private void fixFileQuatrain1(Random random) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(poem));
        String quatrain1 = Poems.getRandomPoem(random).getQuatrain1().trim();
        writer.write(quatrain1);
        writer.close();
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
        repo.getOrigin().checkout().setName("master").call();
        repo.getOrigin().checkout().setCreateBranch(true).setName("quatrain2").call();
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
        BufferedWriter writer = new BufferedWriter(new FileWriter(poem));
        String quatrain2 = Poems.getRandomPoem(random).getQuatrain2().trim();
        writer.write(Poems.deleteWord(quatrain2, random));
        writer.close();
    }
}
