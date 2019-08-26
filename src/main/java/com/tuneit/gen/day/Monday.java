package com.tuneit.gen.day;

import com.tuneit.gen.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Monday implements TaskChecker, TaskGen {
    @Override
    public Boolean checkTask(Variant variant) {
        return null;
    }

    @Override
    public Task generateTask(Variant variant) {
        try {
            updateRepository(variant);
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
        return null; // TODO replace null value
    }

    private void updateRepository(Variant variant) throws GitAPIException, IOException {
        File gitDir = new File(variant.getUsername()); // TODO escape character
        boolean mkdir = gitDir.mkdir();
        if (!mkdir)
            throw new IllegalArgumentException("Duplicate directory");
        File file = new File(variant.getUsername() + "/poem");
        boolean newFile = file.createNewFile();
        if (!newFile)
            throw new IllegalArgumentException("File already exist");
        commit(gitDir, "initial commit");

        Git git = Git.open(gitDir);
        git.checkout().setCreateBranch(true).setName("quatrain1").call();
        quatrain1(file, variant.getRandom());
        commit(gitDir, "First quatrain is added");

        git.checkout().setName("master").call();
        git.checkout().setCreateBranch(true).setName("quatrain2").call();
        quatrain2(file, variant.getRandom());
        commit(gitDir, "Second quatrain is added");
    }

    private void commit(File dir, String name) throws GitAPIException {
        Git git = Git.init().setDirectory(dir).call();

        git.add().addFilepattern("poem").call();
        git.commit().setMessage(name).call();
    }

    private void quatrain1(File file, Random random) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String quatrain1 = Poems.getRandomPoem(random).getQuatrain1().trim();
        writer.write(switchSymbols(quatrain1, random));
        writer.close();
    }

    private void quatrain2(File file, Random random) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String quatrain1 = Poems.getRandomPoem(random).getQuatrain1().trim();
        writer.write(deleteWord(quatrain1, random));
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

    private String deleteWord(String poem, Random random) {
        String[] splitPoem = poem.split("\n");

        StringBuilder resultPoem = new StringBuilder();
        for (int i = 0; i < splitPoem.length; i++) {
            String[] splitLine = splitPoem[i].split(" ");
            int indexDeletedWord = random.nextInt(splitLine.length);
            for (int j = 0; j < splitLine.length; j++) {
                if (j != indexDeletedWord) {
                    resultPoem.append(splitLine[j]);
                    if (j != splitLine.length - 1) {
                        resultPoem.append(' ');
                    }
                }
            }

            if (i != splitPoem.length - 1) {
                resultPoem.append('\n');
            }
        }

        return resultPoem.toString();
    }
}
