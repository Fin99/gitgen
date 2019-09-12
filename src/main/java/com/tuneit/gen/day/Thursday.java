package com.tuneit.gen.day;

import com.tuneit.gen.Poems;
import com.tuneit.gen.Task;
import com.tuneit.gen.Variant;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Thursday extends Day {
    @Override
    public Task checkTask(Variant variant) {
        boolean result = false;
        try {
            if (!new Wednesday().checkTask(variant).getResult()) {
                return new Task(false, "Ошибка при проверке предыдущего репозитория");
            }
            init(variant);
            fixBranchQuatrain2(variant);
            result = diffBetweenBranches("refs/heads/quatrain2", "refs/heads/quatrain2");

            if (!result) {
                RevWalk revWalk = new RevWalk(stud.getRepository());
                ObjectId commitId = stud.getRepository().resolve("refs/heads/quatrain2");
                RevCommit oldCommit = revWalk.parseCommit(commitId);
                if (!oldCommit.getFullMessage().equals("Second quatrain is added")) {
                    reset("quatrain2");
                }
            }

            return new Task(result, result ? null : "Вы не выполнили задачу. Злой тестер откатил репозиторий.");
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
            return new Task(false, "Ошибка при работе с репозиторием");
        }
    }

    private void fixBranchQuatrain2(Variant variant) throws GitAPIException, IOException {
        String oldCommit = "Second quatrain is added";
        String commitName = "Second quatrain is fixed";

        origin.checkout().setName("quatrain2").call();

        ObjectId lastCommit = origin.getRepository().resolve("quatrain2^{commit}");
        RevWalk walker = new RevWalk(origin.getRepository());
        String lastCommitName = walker.parseCommit(lastCommit).getFullMessage();

        if (lastCommitName.equals(oldCommit)) {
            fixFileQuatrain2(variant.getRandom());
            commit(origin, commitName);
        }
    }

    private void fixFileQuatrain2(Random random) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(poem));
        String quatrain2 = Poems.getRandomPoem(random).getQuatrain2().trim();
        writer.write(quatrain2);
        writer.close();
    }

    @Override
    public Task generateTask(Variant variant) {
        try {
            if (!new Wednesday().checkTask(variant).getResult()) {
                return new Task(false, "Ошибка при проверке предыдущего репозитория");
            }
            init(variant);
            updateStudRepository();
            return new Task(true, "Задание четвертое: исправь ошибки во втором абзаце.");
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
            return new Task(false, "Ошибка при работе с репозиторием");
        }
    }

    private void updateStudRepository() throws GitAPIException {
        stud.checkout().setName("dev").call();
        stud.fetch().setRemote("origin").call();
        stud.reset().setMode(ResetCommand.ResetType.HARD).setRef("origin/dev").call();
    }
}
