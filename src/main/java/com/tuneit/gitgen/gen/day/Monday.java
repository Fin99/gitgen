package com.tuneit.gitgen.gen.day;

import com.tuneit.gitgen.data.Poems;
import com.tuneit.gitgen.data.Variant;
import com.tuneit.gitgen.gen.Repo;
import lombok.extern.slf4j.Slf4j;
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

import static com.tuneit.gitgen.gen.GitAPI.*;

@Slf4j
public class Monday extends Day {
    @Override
    public int check(Variant variant) {
        if (!new File(variant.getStudDirName()).exists() ||
                !new File(variant.getOriginDirName()).exists()) {
            return 0;
        }
        if (isMondayPassed(variant)) {
            return new Tuesday().check(variant);
        }
        return 1;
    }

    private boolean isMondayPassed(Variant variant) {
        try {
            Repo repo = new Repo(variant);
            String oldCommit = "Third quatrain is added";
            if (getFirstCommit(repo.getOrigin(), "quatrain3").getFullMessage().equals(oldCommit)) {
                doMonday(variant, repo.getOrigin(), new File(variant.getOriginPoem()));
            }

            List<DiffEntry> diffEntries = diffBetweenBranches(new Repo(variant), "quatrain3", "Third quatrain is fixed");
            return diffEntries != null && diffEntries.isEmpty();
        } catch (IOException e) {
            log.error("Check Monday is failed", e);
            return false;
        }
    }

    @Override
    public void fix(Variant variant, boolean doTask) {
        recreateRepositories(variant);

        createOriginRepository(variant);
        createStudRepository(variant);
        if (doTask) {
            Repo repo = new Repo(variant);
            doMonday(variant, repo.getOrigin(), new File(variant.getOriginPoem()));
            doMonday(variant, repo.getStud(), new File(variant.getStudPoem()));
        }
    }

    private void doMonday(Variant variant, Git repository, File poem) {
        String commitName = "Third quatrain is fixed";

        try {
            repository.checkout().setName("quatrain3").call();
            fixFileQuatrain3(variant.getRandom(), poem);
            commit(repository, commitName);
        } catch (GitAPIException | IOException e) {
            log.error("Do Monday task is failed", e);
        }
    }

    private void fixFileQuatrain3(Random random, File poem) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(poem))) {
            writer.write(Poems.getRandomPoem(random).getQuatrain3());
        }
    }

    private void recreateRepositories(Variant variant) {
        File studRepo = new File(variant.getStudDirName());
        File originRepo = new File(variant.getOriginDirName());
        File studPoem = new File(variant.getStudPoem());
        File originPoem = new File(variant.getOriginPoem());

        try {
            if (studRepo.exists()) {
                FileUtils.deleteDirectory(studRepo);
            }
            if (originRepo.exists()) {
                FileUtils.deleteDirectory(originRepo);
            }
        } catch (IOException e) {
            log.error("Delete repository is failed", e);
        }

        try {
            boolean createRepo = studRepo.mkdir() && originRepo.mkdir() &&
                    studPoem.createNewFile() && originPoem.createNewFile();
            if (!createRepo) {
                throw new IOException();
            }
        } catch (IOException e) {
            log.error("Create repository and file is failed");
        }
    }

    private void createOriginRepository(Variant variant) {
        try {
            File originDir = new File(variant.getOriginDirName());
            Git origin = Git.init().setDirectory(originDir).call();

            StoredConfig config = origin.getRepository().getConfig();
            config.setString("user", null, "name", variant.getUsername());
            config.setString("user", null, "email", variant.getUsername() + "@example.example");
            config.save();

            commit(origin, "initial commit");

            createOriginBranchQuatrain1(variant);
            createOriginBranchQuatrain3(variant);
        } catch (IOException | GitAPIException e) {
            log.error("Create origin repository is failed", e);
        }
    }

    private void createStudRepository(Variant variant) {
        try {
            File studDir = new File(variant.getStudDirName());
            File originDir = new File(variant.getOriginDirName());
            Git.init().setDirectory(studDir).call();

            FileUtils.copyDirectory(originDir, studDir);

            Git origin = Git.open(originDir);
            Git stud = Git.open(studDir);

            StoredConfig config = stud.getRepository().getConfig();
            config.setString("user", null, "name", variant.getUsername());
            config.setString("user", null, "email", variant.getUsername() + "@example.example");
            config.save();

            stud.remoteAdd().setName("origin").setUri(new URIish(origin.getRepository().getDirectory().getAbsolutePath())).call();
        } catch (IOException | URISyntaxException | GitAPIException e) {
            log.error("Create stud repository is failed", e);
        }
    }

    private void createOriginBranchQuatrain1(Variant variant) throws IOException, GitAPIException {
        Git originRepo = Git.open(new File(variant.getOriginDirName()));
        originRepo.checkout().setCreateBranch(true).setStartPoint("master").setName("quatrain1").call();
        updateFileQuatrain1(variant.getRandom(), new File(variant.getOriginPoem()));
        commit(originRepo, "First quatrain is added");
    }

    private void createOriginBranchQuatrain3(Variant variant) throws IOException, GitAPIException {
        Git originRepo = Git.open(new File(variant.getOriginDirName()));
        originRepo.checkout().setCreateBranch(true).setStartPoint("master").setName("quatrain3").call();
        updateFileQuatrain3(variant.getRandom(), new File(variant.getOriginPoem()));
        commit(originRepo, "Third quatrain is added");
    }

    private void updateFileQuatrain1(Random random, File poem) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(poem))) {
            writer.write(Poems.switchSymbols(Poems.getRandomPoem(random).getQuatrain1(), random));
        }
    }

    private void updateFileQuatrain3(Random random, File poem) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(poem))) {
            writer.write(Poems.switchLine(Poems.getRandomPoem(random).getQuatrain3(), random));
        }
    }
}
