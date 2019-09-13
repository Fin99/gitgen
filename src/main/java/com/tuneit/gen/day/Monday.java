package com.tuneit.gen.day;

import com.tuneit.data.Poems;
import com.tuneit.data.Variant;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.URIish;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;

public class Monday extends Day {
    @Override
    public Boolean checkTask(Variant variant) {
        try {
            init(variant);
            fixBranchQuatrain3(variant);
            boolean result = diffBetweenBranches("refs/heads/quatrain3", "refs/heads/quatrain3");
            if (!result) {
                RevWalk revWalk = new RevWalk(stud.getRepository());
                ObjectId commitId = stud.getRepository().resolve("refs/heads/quatrain3");
                RevCommit oldCommit = revWalk.parseCommit(commitId);
                if (!oldCommit.getFullMessage().equals("Third quatrain is added")) {
                    reset("quatrain3");
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

        origin.checkout().setName("quatrain3").call();

        ObjectId lastCommit = origin.getRepository().findRef("HEAD").getObjectId();
        RevWalk walker = new RevWalk(origin.getRepository());
        String lastCommitName = walker.parseCommit(lastCommit).getFullMessage();

        if (lastCommitName.equals(oldCommit)) {
            fixFileQuatrain3(variant.getRandom());
            commit(origin, commitName);
        }
    }

    private void fixFileQuatrain3(Random random) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(poem));
        String quatrain3 = Poems.getRandomPoem(random).getQuatrain3().trim();
        writer.write(quatrain3);
        writer.close();
    }

    @Override
    public void generateTask(Variant variant) {
        try {
            createOriginRepository(variant);
            origin = Git.open(new File(variant.getOriginDirName()));
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
        origin.checkout().setCreateBranch(true).setName("quatrain1").call();
        updateFileQuatrain1(variant.getRandom());
        commit(origin, "First quatrain is added");
    }

    private void createBranchQuatrain3(Variant variant) throws IOException, GitAPIException {
        origin.checkout().setName("master").call();
        origin.checkout().setCreateBranch(true).setName("quatrain3").call();
        updateFileQuatrain3(variant.getRandom());
        commit(origin, "Third quatrain is added");
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
        BufferedWriter writer = new BufferedWriter(new FileWriter(poem));
        String quatrain1 = Poems.getRandomPoem(random).getQuatrain1().trim();
        writer.write(Poems.switchSymbols(quatrain1, random));
        writer.close();
    }

    private void updateFileQuatrain3(Random random) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(poem));
        String quatrain3 = Poems.getRandomPoem(random).getQuatrain3().trim();
        writer.write(Poems.switchLine(quatrain3, random));
        writer.close();
    }

}
