package com.tuneit.gitgen.gen;

import com.tuneit.gitgen.data.Variant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.io.IOException;

@Data
@AllArgsConstructor
@Slf4j
public class Repo {
    private Git origin;
    private Git stud;

    public Repo(Variant variant) {
        try {
            origin = Git.open(new File(variant.getOriginDirName()));
            stud = Git.open(new File(variant.getStudDirName()));
        } catch (IOException gitException) {
            log.error("Repo init is failed", gitException);
        }
    }
}
