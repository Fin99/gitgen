package com.tuneit.gitgen.gen;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.eclipse.jgit.api.Git;

@Data
@AllArgsConstructor
public class Repo {
    private Git origin;
    private Git stud;
}
