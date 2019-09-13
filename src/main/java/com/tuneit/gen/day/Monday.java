package com.tuneit.gen.day;

import com.tuneit.data.Poems;
import com.tuneit.data.Variant;
import com.tuneit.gen.Repo;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.URIish;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Random;

import static com.tuneit.gen.GitAPI.*;

public class Monday extends Day {
    @Override
    public Boolean checkTask(Variant variant) {
        try {
            init(variant);
            fixBranchQuatrain3(variant);
            List<DiffEntry> diffEntries = diffBetweenBranches(repo, "quatrain3", "Third quatrain is fixed");
            if (diffEntries == null) {
                return false;
            }
            boolean result = diffEntries.isEmpty();
            if (!result) {
                if (!getFirstCommit(repo.getStud(), "quatrain3").getFullMessage().equals("Third quatrain is added")) {
                    reset(repo.getStud(), "quatrain3");
                }
            }

            return result;
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void fixBranchQuatrain3(Variant variant) throws GitAPIException, IOException {
        String oldCommit = "Third quatrain is added";
        String commitName = "Third quatrain is fixed";

        if (getFirstCommit(repo.getOrigin(), "quatrain3").getFullMessage().equals(oldCommit)) {
            repo.getOrigin().checkout().setName("quatrain3").call();
            fixFileQuatrain3(variant.getRandom());
            commit(repo.getOrigin(), commitName);
        }
    }

    private void fixFileQuatrain3(Random random) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(poem))) {
            writer.write(Poems.getRandomPoem(random).getQuatrain3());
        }
    }

    @Override
    public void generateTask(Variant variant) {
        try {
            createOriginRepository(variant);
            repo = new Repo(Git.open(new File(variant.getOriginDirName())), null);
            poem = new File(variant.getOriginDirName() + "/poem");
            createBranchQuatrain1(variant);
            createBranchQuatrain3(variant);
            createStudRepository(variant);
        } catch (GitAPIException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getTaskText() {
        return "Задание первое: исправь ошибки в третьем абзаце.";
    }

    private void createOriginRepository(Variant variant) throws GitAPIException, IOException {
        File originDir = new File(variant.getOriginDirName());
        boolean mkdir = originDir.mkdir();
        if (!mkdir)
            throw new IllegalArgumentException("Duplicate directory");
        File poem = new File(variant.getOriginDirName() + "/poem");
        boolean newFile = poem.createNewFile();
        if (!newFile)
            throw new IllegalArgumentException("File already exist");
        Git origin = Git.init().setDirectory(originDir).call();

        StoredConfig config = origin.getRepository().getConfig();
        config.setString("user", null, "name", variant.getUsername());
        config.setString("user", null, "email", variant.getUsername() + "@example.example");
        config.save();

        commit(origin, "initial commit");
    }

    private void createBranchQuatrain1(Variant variant) throws IOException, GitAPIException {
        repo.getOrigin().checkout().setCreateBranch(true).setName("quatrain1").call();
        updateFileQuatrain1(variant.getRandom());
        commit(repo.getOrigin(), "First quatrain is added");
    }

    private void createBranchQuatrain3(Variant variant) throws IOException, GitAPIException {
        repo.getOrigin().checkout().setName("master").call();
        repo.getOrigin().checkout().setCreateBranch(true).setName("quatrain3").call();
        updateFileQuatrain3(variant.getRandom());
        commit(repo.getOrigin(), "Third quatrain is added");
    }

    private void createStudRepository(Variant variant) throws IOException, URISyntaxException, GitAPIException {
        File studDir = new File(variant.getStudDirName());
        boolean mkdir = studDir.mkdir();
        if (!mkdir)
            throw new IllegalArgumentException("Duplicate directory");

        File originDir = new File(variant.getOriginDirName());
        FileUtils.copyDirectory(originDir, studDir);

        Git origin = Git.open(originDir);
        Git stud = Git.open(studDir);

        StoredConfig config = stud.getRepository().getConfig();
        config.setString("user", null, "name", variant.getUsername());
        config.setString("user", null, "email", variant.getUsername() + "@example.example");
        config.save();

        stud.remoteAdd().setName("origin").setUri(new URIish(origin.getRepository().getDirectory().getAbsolutePath())).call();
    }


    private void updateFileQuatrain1(Random random) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(poem))) {
            writer.write(Poems.switchSymbols(Poems.getRandomPoem(random).getQuatrain1(), random));
        }
    }

    private void updateFileQuatrain3(Random random) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(poem))) {
            writer.write(Poems.switchLine(Poems.getRandomPoem(random).getQuatrain3(), random));
        }
    }

}
