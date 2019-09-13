package com.tuneit.gen;

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

import java.io.IOException;
import java.util.List;

public class GitAPI {
    public static void commit(Git git, String name) throws GitAPIException {
        git.add().addFilepattern("poem").call();
        git.commit().setMessage(name).call();
    }

    public static void reset(Git git, String branchName) throws GitAPIException {
        git.checkout().setName(branchName).call();
        git.reset().setMode(ResetCommand.ResetType.HARD).setRef("HEAD~1").call();
    }

    public static List<DiffEntry> diffBetweenBranches(Repo repo, String branchName, String commitName) throws IOException, GitAPIException {
        try {
            AbstractTreeIterator oldTreeParser = prepareTreeParser(repo.getOrigin().getRepository(), branchName);
            AbstractTreeIterator newTreeParser = prepareTreeParser(repo.getStud().getRepository(), branchName);

            return repo.getOrigin().diff().setOldTree(oldTreeParser).setNewTree(newTreeParser).call();
        } catch (JGitInternalException fall) {
            return null;
        }
    }

    private static AbstractTreeIterator prepareTreeParser(Repository repository, String ref) throws IOException {
        RevWalk walk = new RevWalk(repository);
        RevCommit commit = walk.parseCommit(repository.resolve(ref));

        RevTree tree = walk.parseTree(commit.getTree().getId());

        CanonicalTreeParser treeParser = new CanonicalTreeParser();
        ObjectReader reader = repository.newObjectReader();
        treeParser.reset(reader, tree.getId());

        return treeParser;
    }
}
