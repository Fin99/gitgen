package com.tuneit.gen.day;

import com.tuneit.gen.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
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

public class Friday implements TaskChecker, TaskGen {
    @Override
    public Boolean checkTask(Variant variant) {
        try {
            if (!new Thursday().checkTask(variant)) {
                throw new UnsupportedOperationException("Thursday check task is failed");
            }

            mergeDevAndQuatrain2(variant);
            if (!checkMergeDevAndQuatrain2(variant)) {
                return false;
            }

            mergeDevAndMaster(variant);
            return checkMergeDevAndMaster(variant);
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void mergeDevAndQuatrain2(Variant variant) throws GitAPIException, IOException {
        String commitName = "Merge dev and quatrain2";
        File originDir = new File(variant.getOriginDirName());

        Git origin = Git.open(originDir);
        origin.checkout().setName("dev").call();

        ObjectId lastCommit = origin.getRepository().resolve("dev^{commit}");
        RevWalk walker = new RevWalk(origin.getRepository());
        String lastCommitName = walker.parseCommit(lastCommit).getFullMessage();

        if (lastCommitName.equals("Merge quatrain1 and quatrain3")) {
            origin.merge().include(origin.getRepository().findRef("quatrain2")).call();
            fixConflict(new File(variant.getOriginDirName() + "/poem"), variant);
            commit(originDir, commitName);
        }
    }

    private void mergeDevAndMaster(Variant variant) throws GitAPIException, IOException {
        File originDir = new File(variant.getOriginDirName());

        Git origin = Git.open(originDir);
        origin.checkout().setName("master").call();

        ObjectId lastCommit = origin.getRepository().resolve("master^{commit}");
        RevWalk walker = new RevWalk(origin.getRepository());
        String lastCommitName = walker.parseCommit(lastCommit).getFullMessage();

        if (lastCommitName.equals("initial commit")) {
            origin.merge().include(origin.getRepository().findRef("dev")).setFastForward(MergeCommand.FastForwardMode.FF_ONLY).call();
        }
    }

    private boolean checkMergeDevAndQuatrain2(Variant variant) throws IOException, GitAPIException {
        Git origin = Git.open(new File(variant.getOriginDirName()));
        Git stud = Git.open(new File(variant.getStudDirName()));

        ObjectId head = origin.getRepository().resolve("dev^{tree}");
        ObjectId previousHead = stud.getRepository().resolve("dev^{tree}");

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

    private void fixConflict(File file, Variant variant) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

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

    private boolean checkMergeDevAndMaster(Variant variant) throws IOException, GitAPIException {
        Git origin = Git.open(new File(variant.getOriginDirName()));
        Git stud = Git.open(new File(variant.getStudDirName()));

        ObjectId head = origin.getRepository().resolve("master^{tree}");
        ObjectId previousHead = stud.getRepository().resolve("master^{tree}");

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

    @Override
    public Task generateTask(Variant variant) {
        try {
            if (!new Thursday().checkTask(variant)) {
                throw new UnsupportedOperationException("Thursday check task is failed");
            }

            updateStudRepository(variant);
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void updateStudRepository(Variant variant) throws IOException, GitAPIException {
        Git stud = Git.open(new File(variant.getStudDirName()));

        stud.checkout().setName("quatrain2").call();
        stud.fetch().setRemote("origin").call();
        stud.reset().setMode(ResetCommand.ResetType.HARD).setRef("origin/quatrain2").call();
    }


    private void commit(File dir, String name) throws GitAPIException {
        Git git = Git.init().setDirectory(dir).call();

        git.add().addFilepattern("poem").call();
        git.commit().setMessage(name).call();
    }
}
