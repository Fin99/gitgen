package com.tuneit.gen.day;

import com.tuneit.gen.*;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
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

public class Wednesday implements TaskChecker, TaskGen {
    @Override
    public Boolean checkTask(Variant variant) {
        try {
            if (!new Tuesday().checkTask(variant)) {
                throw new UnsupportedOperationException("Tuesday check task is failed");
            }
            mergeQuatrain1AndQuatrain3(variant);
            return checkMergeQuatrain1AndQuatrain3(variant);
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void mergeQuatrain1AndQuatrain3(Variant variant) throws GitAPIException, IOException {
        String commitName = "Merge quatrain1 and quatrain3";
        File originDir = new File(variant.getOriginDirName());

        Git origin = Git.open(originDir);
        origin.checkout().setName("dev").call();

        ObjectId lastCommit = origin.getRepository().resolve("dev^{commit}");
        RevWalk walker = new RevWalk(origin.getRepository());
        String lastCommitName = walker.parseCommit(lastCommit).getFullMessage();

        if (!lastCommitName.equals(commitName)) {
            origin.merge().include(origin.getRepository().findRef("quatrain3")).call();
            fixConflict(new File(variant.getOriginDirName() + "/poem"), variant);
            commit(originDir, commitName);
        }
    }

    private boolean checkMergeQuatrain1AndQuatrain3(Variant variant) throws IOException, GitAPIException {
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
        String quatrain3 = Poems.getRandomPoem(variant.getRandom()).getQuatrain3().trim();
        writer.write(quatrain3);
        writer.close();
    }

    @Override
    public Task generateTask(Variant variant) {
        try {

            if (!new Tuesday().checkTask(variant)) {
                throw new UnsupportedOperationException("Tuesday check task is failed");
            }

            createBranchDev(variant);
            updateStudRepository(variant);
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createBranchDev(Variant variant) throws IOException, GitAPIException {
        File originDir = new File(variant.getOriginDirName());

        Git origin = Git.open(originDir);
        origin.checkout().setName("quatrain1").call();
        origin.checkout().setCreateBranch(true).setName("dev").call();
    }

    private void updateStudRepository(Variant variant) throws IOException, GitAPIException {
        Git stud = Git.open(new File(variant.getStudDirName()));

        stud.checkout().setName("quatrain1");
        stud.fetch().setRemote("origin").call();
        stud.reset().setMode(ResetCommand.ResetType.HARD).setRef("origin/quatrain1").call();
        stud.checkout().setName("origin/dev").call();
        stud.checkout().setName("dev").setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).setCreateBranch(true).call();
    }


    private void commit(File dir, String name) throws GitAPIException {
        Git git = Git.init().setDirectory(dir).call();

        git.add().addFilepattern("poem").call();
        git.commit().setMessage(name).call();
    }
}
