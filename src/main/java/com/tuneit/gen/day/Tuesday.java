package com.tuneit.gen.day;

import com.tuneit.gen.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class Tuesday implements TaskChecker, TaskGen {
    @Override
    public Boolean checkTask(Variant variant) {
        try {
            if (!new Monday().checkTask(variant)) {
                throw new UnsupportedOperationException("Monday check task is failed");
            }
            fixBranchQuatrain1(variant);
            return checkBranchQuatrain1(variant);
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void fixBranchQuatrain1(Variant variant) throws GitAPIException, IOException {
        String commitName = "First quatrain is fixed";
        File originDir = new File(variant.getOriginDirName());

        Git origin = Git.open(originDir);
        origin.checkout().setName("quatrain1").call();

        ObjectId lastCommit = origin.getRepository().resolve("quatrain1^{commit}");
        RevWalk walker = new RevWalk(origin.getRepository());
        String lastCommitName = walker.parseCommit(lastCommit).getFullMessage();

        if (!lastCommitName.equals(commitName)) {
            fixFileQuatrain1(new File(variant.getOriginDirName() + "/poem"), variant.getRandom());
            commit(originDir, commitName);
        }
    }

    private boolean checkBranchQuatrain1(Variant variant) throws IOException, GitAPIException {
        Git origin = Git.open(new File(variant.getOriginDirName()));
        Git stud = Git.open(new File(variant.getStudDirName()));

        ObjectId head = origin.getRepository().resolve("quatrain1^{tree}");
        ObjectId previousHead = stud.getRepository().resolve("quatrain1^{tree}");

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

    private void fixFileQuatrain1(File file, Random random) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String quatrain1 = Poems.getRandomPoem(random).getQuatrain1().trim();
        writer.write(quatrain1);
        writer.close();
    }

    @Override
    public Task generateTask(Variant variant) {
        try {
            if (!new Monday().checkTask(variant)) {
                throw new UnsupportedOperationException("Monday check task is failed");
            }

            createBranchQuatrain2(variant);
            updateStudRepository(variant);

        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void createBranchQuatrain2(Variant variant) throws IOException, GitAPIException {
        String originDirName = variant.getUsername() + variant.getVariant() + "origin";
        File originDir = new File(originDirName);

        Git origin = Git.open(originDir);
        origin.checkout().setName("master").call();
        origin.checkout().setCreateBranch(true).setName("quatrain2").call();
        updateFileQuatrain2(new File(originDirName + "/poem"), variant.getRandom());
        commit(originDir, "Second quatrain is added");
    }

    private void updateStudRepository(Variant variant) throws IOException, GitAPIException {
        Git stud = Git.open(new File(variant.getStudDirName()));

        stud.fetch().setForceUpdate(true).setRemote("origin").call();
        stud.reset().setMode(ResetCommand.ResetType.HARD).setRef("origin/quatrain3").call();
    }

    private void updateFileQuatrain2(File file, Random random) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String quatrain2 = Poems.getRandomPoem(random).getQuatrain2().trim();
        writer.write(Poems.deleteWord(quatrain2, random));
        writer.close();
    }


    private void commit(File dir, String name) throws GitAPIException {
        Git git = Git.init().setDirectory(dir).call();

        git.add().addFilepattern("poem").call();
        git.commit().setMessage(name).call();
    }


}
