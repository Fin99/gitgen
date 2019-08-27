package com.tuneit.gen.day;

import com.tuneit.gen.*;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
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
        Git origin = Git.open(new File(variant.getUsername() + variant.getVariant() + "origin"));
        Git stud = Git.open(new File(variant.getUsername() + variant.getVariant()));

        ObjectId head = origin.getRepository().resolve("HEAD^{tree}");
        ObjectId previousHead = stud.getRepository().resolve("HEAD^{tree}");

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
        String originDirName = variant.getUsername() + variant.getVariant() + "origin";
        File originDir = new File(originDirName); // TODO escape character

        Git origin = Git.open(originDir);
        origin.checkout().setName("quatrain3").call();
        fixFileQuatrain3(new File(originDirName + "/poem"), variant.getRandom());
        commit(originDir, "Third quatrain is fixed");
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
        String originDirName = variant.getUsername() + variant.getVariant() + "origin";
        File originDir = new File(originDirName); // TODO escape character
        boolean mkdir = originDir.mkdir();
        if (!mkdir)
            throw new IllegalArgumentException("Duplicate directory");
        File file = new File(originDirName + "/poem");
        boolean newFile = file.createNewFile();
        if (!newFile)
            throw new IllegalArgumentException("File already exist");
        commit(originDir, "initial commit");
    }

    private void createBranchQuatrain1(Variant variant) throws IOException, GitAPIException {
        String originDirName = variant.getUsername() + variant.getVariant() + "origin";
        File originDir = new File(originDirName); // TODO escape character

        Git origin = Git.open(originDir);
        origin.checkout().setCreateBranch(true).setName("quatrain1").call();
        updateFileQuatrain1(new File(originDirName + "/poem"), variant.getRandom());
        commit(originDir, "First quatrain is added");
    }

    private void createBranchQuatrain3(Variant variant) throws IOException, GitAPIException {
        String originDirName = variant.getUsername() + variant.getVariant() + "origin";
        File originDir = new File(originDirName); // TODO escape character

        Git origin = Git.open(originDir);
        origin.checkout().setName("master").call();
        origin.checkout().setCreateBranch(true).setName("quatrain3").call();
        updateFileQuatrain3(new File(originDirName + "/poem"), variant.getRandom());
        commit(originDir, "Third quatrain is added");
    }

    private void createStudRepository(Variant variant) throws IOException, URISyntaxException, GitAPIException {
        String studDirName = variant.getUsername() + variant.getVariant();
        File studDir = new File(studDirName); // TODO escape character
        boolean mkdir = studDir.mkdir();
        if (!mkdir)
            throw new IllegalArgumentException("Duplicate directory");

        File originDir = new File(studDirName + "origin"); // TODO escape character
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
        writer.write(switchSymbols(quatrain1, random));
        writer.close();
    }

    private void updateFileQuatrain3(File file, Random random) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String quatrain3 = Poems.getRandomPoem(random).getQuatrain3().trim();
        writer.write(switchLine(quatrain3, random));
        writer.close();
    }

    private String switchSymbols(String poem, Random random) {
        String[] splitPoem = poem.split("\n");

        StringBuilder resultPoem = new StringBuilder();
        for (int i = 0; i < splitPoem.length; i++) {
            int indexError = random.nextInt(splitPoem[i].length());
            resultPoem.append(splitPoem[i], 0, indexError);
            resultPoem.append((char) (32 + random.nextInt(1071)));
            resultPoem.append(splitPoem[i], indexError, splitPoem[i].length());

            if (i != splitPoem.length - 1) {
                resultPoem.append('\n');
            }
        }

        return resultPoem.toString();
    }

    private String switchLine(String poem, Random random) {
        String[] splitPoem = poem.split("\n");
        int firstSwitchLine = random.nextInt(splitPoem.length - 1);

        StringBuilder resultPoem = new StringBuilder();
        for (int i = 0; i < splitPoem.length; i++) {
            if (i == firstSwitchLine) {
                resultPoem.append(splitPoem[i + 1]);
            } else if (i == firstSwitchLine + 1) {
                resultPoem.append(splitPoem[i - 1]);
            } else {
                resultPoem.append(splitPoem[i]);
            }

            if (i != splitPoem.length - 1) {
                resultPoem.append('\n');
            }
        }

        return resultPoem.toString();
    }
}
