package com.tuneit.gitgen.gen;

import com.tuneit.gitgen.data.Variant;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GitAPI {
    public static void deleteRepositories(Variant variant) {
        try {
            FileUtils.deleteDirectory(new File(variant.getOriginDirName()));
            FileUtils.deleteDirectory(new File(variant.getStudDirName()));
        } catch (IOException e) {
            throw new IllegalStateException("Stud or origin repository is incorrect");
        }
    }

    public static void commit(Git git, String name) throws GitAPIException {
        git.add().addFilepattern("poem").call();
        git.commit().setMessage(name).call();
    }

    public static RevCommit getFirstCommit(Git git, String branchName) throws IOException {
        RevWalk revWalk = new RevWalk(git.getRepository());
        ObjectId commitId = git.getRepository().findRef(branchName).getObjectId();
        return revWalk.parseCommit(commitId);
    }

    public static List<DiffEntry> diffBetweenBranches(Repo repo, String branchName, String commitName) {
        RevCommit originCommit = null;
        try {
            originCommit = findCommit(repo.getOrigin(), branchName, commitName);
            AbstractTreeIterator oldTreeParser = prepareTreeParser(repo.getOrigin().getRepository(), originCommit.toObjectId().getName());
            AbstractTreeIterator newTreeParser = prepareTreeParser(repo.getStud().getRepository(), branchName);

            List<DiffEntry> diff = repo.getOrigin().diff().setOldTree(newTreeParser).setNewTree(oldTreeParser).call();
            if (diff.isEmpty()) {
                return diff;
            }
        } catch (JGitInternalException | IOException | GitAPIException ignored) {

        }

        try {
            RevCommit studCommit = findCommit(repo.getOrigin(), branchName, commitName);
            if (studCommit == null) {
                return null;
            }
            AbstractTreeIterator oldTreeParser = prepareTreeParser(repo.getOrigin().getRepository(), originCommit.toObjectId().getName());
            AbstractTreeIterator newTreeParser = prepareTreeParser(repo.getStud().getRepository(), studCommit.toObjectId().getName());

            return repo.getOrigin().diff().setOldTree(oldTreeParser).setNewTree(newTreeParser).call();
        } catch (JGitInternalException | IOException | GitAPIException ignored) {
            return null;
        }
    }

    private static RevCommit findCommit(Git origin, String branchName, String commitName) throws IOException {
        RevWalk revWalk = new RevWalk(origin.getRepository());
        ObjectId commitId = origin.getRepository().findRef(branchName).getObjectId();
        RevCommit oldCommit = revWalk.parseCommit(commitId);
        revWalk.markStart(oldCommit);
        RevCommit commit = revWalk.next();

        while (commit != null && !commit.getFullMessage().equals(commitName)) {
            commit = revWalk.next();

        }
        return commit;
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
