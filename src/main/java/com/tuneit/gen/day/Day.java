package com.tuneit.gen.day;

import com.tuneit.data.Variant;
import com.tuneit.gen.Repo;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.io.IOException;

public abstract class Day {
    Repo repo;
    File poem;

    public abstract Boolean checkTask(Variant variant);

    public abstract void generateTask(Variant variant);

    public abstract String getTaskText();

    void init(Variant variant) throws IOException {
        repo = new Repo(Git.open(new File(variant.getOriginDirName())), Git.open(new File(variant.getStudDirName())));
        poem = new File(variant.getOriginDirName() + "/poem");
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
