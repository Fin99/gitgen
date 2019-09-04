package com.tuneit.gen.day;

import com.tuneit.gen.TaskChecker;
import com.tuneit.gen.TaskGen;
import com.tuneit.gen.Variant;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class Day implements TaskChecker, TaskGen {
    Git origin;
    Git stud;
    File poem;

    void init(Variant variant) throws IOException {
        origin = Git.open(new File(variant.getOriginDirName()));
        stud = Git.open(new File(variant.getStudDirName()));
        poem = new File(variant.getOriginDirName() + "/poem");
    }


    void commit(Git git, String name) throws GitAPIException {
        git.add().addFilepattern("poem").call();
        git.commit().setMessage(name).call();
    }

    void reset(String branchName) throws GitAPIException {
        stud.checkout().setName(branchName).call();
        stud.reset().setMode(ResetCommand.ResetType.HARD).setRef("HEAD~1").call();
    }

    boolean diffBetweenBranches(String originRef, String studRef) throws IOException, GitAPIException {
        try {
            AbstractTreeIterator oldTreeParser = prepareTreeParser(origin.getRepository(), originRef);
            AbstractTreeIterator newTreeParser = prepareTreeParser(stud.getRepository(), studRef);

            List<DiffEntry> diffEntries = origin.diff().setOldTree(oldTreeParser).setNewTree(newTreeParser).call();

            return diffEntries.isEmpty();
        } catch (JGitInternalException fall) {
            return false;
        }
    }

    AbstractTreeIterator prepareTreeParser(Repository repository, String ref) throws IOException {
        RevWalk walk = new RevWalk(repository);
        RevCommit commit = walk.parseCommit(repository.resolve(ref));

        RevTree tree = walk.parseTree(commit.getTree().getId());

        CanonicalTreeParser treeParser = new CanonicalTreeParser();
        ObjectReader reader = repository.newObjectReader();
        treeParser.reset(reader, tree.getId());

        return treeParser;
    }

    void removeRepo(Variant variant) {
        try {
            FileUtils.deleteDirectory(new File(variant.getOriginDirName()));
            FileUtils.deleteDirectory(new File(variant.getStudDirName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
