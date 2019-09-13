package com.tuneit.gen.day;

import com.tuneit.data.Poems;
import com.tuneit.data.Variant;
import com.tuneit.gen.Task;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Friday extends Day {
    @Override
    public Task checkTask(Variant variant) {
        try {
            if (!new Thursday().checkTask(variant).getResult()) {
                return new Task(false, "Ошибка при проверке предыдущего репозитория");
            }
            init(variant);
            mergeDevAndQuatrain2(variant);

            boolean result = diffBetweenBranches("refs/heads/dev", "refs/heads/dev");

            if (!result) {
                RevWalk revWalk = new RevWalk(stud.getRepository());
                ObjectId commitId = stud.getRepository().resolve("refs/heads/dev");
                RevCommit oldCommit = revWalk.parseCommit(commitId);
                if (!oldCommit.getFullMessage().equals("Merge quatrain1 and quatrain3")) {
                    reset("dev");
                }
            }

            if (result) {
                removeRepo(variant);
            }

            return new Task(result, result ? null : "Вы не выполнили задачу. Злой тестер откатил репозиторий.");
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
            return new Task(false, "Ошибка при работе с репозиторием");
        }
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
    public Task generateTask(Variant variant) {
        try {
            if (!new Thursday().checkTask(variant).getResult()) {
                return new Task(false, "Ошибка при проверке предыдущего репозитория");
            }
            init(variant);
            updateStudRepository();
            return new Task(true, "Задание пятое: присоедините тертий абзац к ветке dev.");
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
            return new Task(false, "Ошибка при работе с репозиторием");
        }
    }

    @Override
    public String getTaskText() {
        return "Задание пятое: присоедините тертий абзац к ветке dev.";
    }

    private void updateStudRepository() throws GitAPIException {
        stud.checkout().setName("quatrain2").call();
        stud.fetch().setRemote("origin").call();
        stud.reset().setMode(ResetCommand.ResetType.HARD).setRef("origin/quatrain2").call();
    }
}
