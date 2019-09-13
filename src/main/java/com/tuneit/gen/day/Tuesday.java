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
import java.util.Random;

public class Tuesday extends Day {
    @Override
    public Task checkTask(Variant variant) {
        boolean result = false;
        try {
            if (!new Monday().checkTask(variant).getResult()) {
                return new Task(false, "Ошибка при проверке предыдущего репозитория");
            }
            init(variant);
            fixBranchQuatrain1(variant);
            result = diffBetweenBranches("refs/heads/quatrain1", "refs/heads/quatrain1");

            if (!result) {
                RevWalk revWalk = new RevWalk(stud.getRepository());
                ObjectId commitId = stud.getRepository().resolve("refs/heads/quatrain1");
                RevCommit oldCommit = revWalk.parseCommit(commitId);
                if (!oldCommit.getFullMessage().equals("First quatrain is added")) {
                    reset("quatrain1");
                }
            }

            return new Task(result, result ? null : "Вы не выполнили задачу. Злой тестер откатил репозиторий.");
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
            return new Task(false, "Ошибка при работе с репозиторием");
        }
    }

    private void fixBranchQuatrain1(Variant variant) throws GitAPIException, IOException {
        String oldCommit = "First quatrain is added";
        String commitName = "First quatrain is fixed";

        origin.checkout().setName("quatrain1").call();

        ObjectId lastCommit = origin.getRepository().resolve("quatrain1^{commit}");
        RevWalk walker = new RevWalk(origin.getRepository());
        String lastCommitName = walker.parseCommit(lastCommit).getFullMessage();

        if (lastCommitName.equals(oldCommit)) {
            fixFileQuatrain1(variant.getRandom());
            commit(origin, commitName);
        }
    }

    private void fixFileQuatrain1(Random random) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(poem));
        String quatrain1 = Poems.getRandomPoem(random).getQuatrain1().trim();
        writer.write(quatrain1);
        writer.close();
    }

    @Override
    public Task generateTask(Variant variant) {
        try {
            if (!new Monday().checkTask(variant).getResult()) {
                return new Task(false, "Ошибка при проверке предыдущего репозитория");
            }
            init(variant);
            createBranchQuatrain2(variant);
            updateStudRepository();
            return new Task(true, "Задание второе: исправь ошибки в первом абзаце.");
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
            return new Task(false, "Ошибка при работе с репозиторием");
        }
    }

    @Override
    public String getTaskText() {
        return "Задание второе: исправь ошибки в первом абзаце.";
    }

    private void createBranchQuatrain2(Variant variant) throws IOException, GitAPIException {
        origin.checkout().setName("master").call();
        origin.checkout().setCreateBranch(true).setName("quatrain2").call();
        updateFileQuatrain2(variant.getRandom());
        commit(origin, "Second quatrain is added");
    }

    private void updateStudRepository() throws GitAPIException {
        stud.checkout().setName("quatrain3").call();
        stud.fetch().setRemote("origin").call();
        stud.reset().setMode(ResetCommand.ResetType.HARD).setRef("origin/quatrain3").call();
        stud.checkout().setName("origin/quatrain2").call();
        stud.checkout().setName("quatrain2").setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).setCreateBranch(true).call();
    }

    private void updateFileQuatrain2(Random random) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(poem));
        String quatrain2 = Poems.getRandomPoem(random).getQuatrain2().trim();
        writer.write(Poems.deleteWord(quatrain2, random));
        writer.close();
    }
}
