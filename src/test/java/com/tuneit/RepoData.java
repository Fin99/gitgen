package com.tuneit;

import com.tuneit.bash.GitBashServiceDefault;
import com.tuneit.gen.TaskServiceDefault;
import com.tuneit.gen.Variant;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;

abstract class RepoData {
    final File dirStud = new File("test1");
    final File dirOrigin = new File("test1origin");
    final File poemStud = new File("test1/poem");
    final File poemOrigin = new File("test1origin/poem");
    Git gitStud;
    Git gitOrigin;

    TaskService taskService = new TaskServiceDefault();
    GitBashService bashService = new GitBashServiceDefault();

    Variant variant;

    @BeforeEach
    void createRepo() throws IOException, GitAPIException {
        taskService.generateTask(new Variant(1, variant.getUsername(), variant.getVariant()));
        gitStud = Git.open(dirStud);
        gitOrigin = Git.open(dirOrigin);
        createTask();
    }

    abstract void createTask() throws IOException, GitAPIException;

    @AfterEach
    void deleteRepo() throws IOException {
        FileUtils.deleteDirectory(dirStud);
        FileUtils.deleteDirectory(dirOrigin);
    }

    void makeMonday() {
        Variant variant = new Variant(1, "test", 1);
        bashService.executeCommand("git checkout quatrain3", variant);
        bashService.poem("Нам мнится: мир осиротелый\n" +
                "Неотразимый Рок настиг —\n" +
                "И мы, в борьбе, природой целой\n" +
                "Покинуты на нас самих.", variant);
        bashService.executeCommand("git add .", variant);
        bashService.executeCommand("git commit -m \"test commit\"", variant);
    }

    void makeTuesday() {
        Variant variant = new Variant(2, "test", 1);
        bashService.executeCommand("git checkout quatrain1", variant);
        bashService.poem("Часов однообразный бой,\n" +
                "Томительная ночи повесть!\n" +
                "Язык для всех равно чужой\n" +
                "И внятный каждому, как совесть!", variant);
        bashService.executeCommand("git add .", variant);
        bashService.executeCommand("git commit -m \"test commit\"", variant);
    }

    void makeWednesday() {
        Variant variant = new Variant(3, "test", 1);
        bashService.executeCommand("git checkout dev", variant);
        bashService.executeCommand("git merge quatrain3", variant);
        bashService.poem("Часов однообразный бой,\n" +
                "Томительная ночи повесть!\n" +
                "Язык для всех равно чужой\n" +
                "И внятный каждому, как совесть!\n\n" +
                "Нам мнится: мир осиротелый\n" +
                "Неотразимый Рок настиг —\n" +
                "И мы, в борьбе, природой целой\n" +
                "Покинуты на нас самих.", variant);
        bashService.executeCommand("git add .", variant);
        bashService.executeCommand("git commit -m \"test commit\"", variant);
    }

    void makeThursday() {
        Variant variant = new Variant(4, "test", 1);
        bashService.executeCommand("git checkout quatrain2", variant);
        bashService.poem("Кто без тоски внимал из нас,\n" +
                "Среди всемирного молчанья,\n" +
                "Глухие времени стенанья,\n" +
                "Пророчески-прощальный глас?", variant);
        bashService.executeCommand("git add .", variant);
        bashService.executeCommand("git commit -m \"test commit\"", variant);
    }

    void makeFriday() {
        Variant variant = new Variant(5, "test", 1);
        bashService.executeCommand("git checkout dev", variant);
        bashService.executeCommand("git merge quatrain2", variant);
        bashService.poem("Часов однообразный бой,\n" +
                "Томительная ночи повесть!\n" +
                "Язык для всех равно чужой\n" +
                "И внятный каждому, как совесть!\n\n" +
                "Кто без тоски внимал из нас,\n" +
                "Среди всемирного молчанья,\n" +
                "Глухие времени стенанья,\n" +
                "Пророчески-прощальный глас?\n\n" +
                "Нам мнится: мир осиротелый\n" +
                "Неотразимый Рок настиг —\n" +
                "И мы, в борьбе, природой целой\n" +
                "Покинуты на нас самих.", variant);
        bashService.executeCommand("git add .", variant);
        bashService.executeCommand("git commit -m \"merge\"", variant);

        bashService.executeCommand("git checkout master", variant);
        bashService.executeCommand("git merge dev", variant);
    }
}
