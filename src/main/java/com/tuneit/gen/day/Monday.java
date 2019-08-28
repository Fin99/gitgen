package com.tuneit.gen.day;

import com.tuneit.gen.*;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Random;

public class Monday implements TaskChecker, TaskGen {
    @Override
    public Boolean checkTask(Variant variant) {
        try {
            fixBranchQuatrain3(variant);
            return checkBranchQuatrain3(variant);
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkBranchQuatrain3(Variant variant) throws IOException, GitAPIException {
        Git origin = Git.open(new File(variant.getOriginDirName()));
        Git stud = Git.open(new File(variant.getStudDirName()));

        ObjectId head = origin.getRepository().resolve("quatrain3^{tree}");
        ObjectId previousHead = stud.getRepository().resolve("quatrain3^{tree}");

        ObjectReader reader = origin.getRepository().newObjectReader();

        CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
        oldTreeIter.reset(reader, previousHead);
        CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
        newTreeIter.reset(reader, head);

        List<DiffEntry> diffEntries = origin.diff().setOldTree(oldTreeIter).setNewTree(newTreeIter).call();
        if (!diffEntries.isEmpty()) {
            for (DiffEntry diff : diffEntries) {
                DiffFormatter formatter = new DiffFormatter(System.out);
                formatter.setRepository(origin.getRepository());
                formatter.format(diff);
            }
        }
        return diffEntries.isEmpty();
    }

    private void fixBranchQuatrain3(Variant variant) throws GitAPIException, IOException {
        File originDir = new File(variant.getOriginDirName());
        String commitName = "Third quatrain is fixed";

        Git origin = Git.open(originDir);
        origin.checkout().setName("quatrain3").call();

        ObjectId lastCommit = origin.getRepository().resolve("HEAD");
        RevWalk walker = new RevWalk(origin.getRepository());
        String lastCommitName = walker.parseCommit(lastCommit).getFullMessage();

        if (lastCommitName.equals("Third quatrain is added")) {
            fixFileQuatrain3(new File(variant.getOriginDirName() + "/poem"), variant.getRandom());
            commit(originDir, commitName);
        }
    }

    private void fixFileQuatrain3(File file, Random random) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String quatrain3 = Poems.getRandomPoem(random).getQuatrain3().trim();
        writer.write(quatrain3);
        writer.close();
    }

    @Override
    public Task generateTask(Variant variant) {
        try {
            createOriginRepository(variant);
            createBranchQuatrain1(variant);
            createBranchQuatrain3(variant);
            createStudRepository(variant);
        } catch (GitAPIException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null; // TODO replace null value
    }

    private void createOriginRepository(Variant variant) throws GitAPIException, IOException {
        File originDir = new File(variant.getOriginDirName());
        boolean mkdir = originDir.mkdir();
        if (!mkdir)
            throw new IllegalArgumentException("Duplicate directory");
        File file = new File(variant.getOriginDirName() + "/poem");
        boolean newFile = file.createNewFile();
        if (!newFile)
            throw new IllegalArgumentException("File already exist");
        commit(originDir, "initial commit");
    }

    private void createBranchQuatrain1(Variant variant) throws IOException, GitAPIException {
        File originDir = new File(variant.getOriginDirName());

        Git origin = Git.open(originDir);
        origin.checkout().setCreateBranch(true).setName("quatrain1").call();
        updateFileQuatrain1(new File(variant.getOriginDirName() + "/poem"), variant.getRandom());
        commit(originDir, "First quatrain is added");
    }

    private void createBranchQuatrain3(Variant variant) throws IOException, GitAPIException {
        File originDir = new File(variant.getOriginDirName());

        Git origin = Git.open(originDir);
        origin.checkout().setName("master").call();
        origin.checkout().setCreateBranch(true).setName("quatrain3").call();
        updateFileQuatrain3(new File(variant.getOriginDirName() + "/poem"), variant.getRandom());
        commit(originDir, "Third quatrain is added");
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
        stud.remoteAdd().setName("origin").setUri(new URIish(origin.getRepository().getDirectory().getAbsolutePath())).call();
    }

    private void commit(File dir, String name) throws GitAPIException {
        Git git = Git.init().setDirectory(dir).call();

        git.add().addFilepattern("poem").call();
        git.commit().setMessage(name).call();
    }

    private void updateFileQuatrain1(File file, Random random) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String quatrain1 = Poems.getRandomPoem(random).getQuatrain1().trim();
        writer.write(Poems.switchSymbols(quatrain1, random));
        writer.close();
    }

    private void updateFileQuatrain3(File file, Random random) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String quatrain3 = Poems.getRandomPoem(random).getQuatrain3().trim();
        writer.write(Poems.switchLine(quatrain3, random));
        writer.close();
    }

}
