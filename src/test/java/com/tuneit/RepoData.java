package com.tuneit;

import com.tuneit.gen.TaskServiceDefault;
import com.tuneit.gen.Variant;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

abstract class RepoData {
    final File dirStud = new File("test1");
    final File dirOrigin = new File("test1origin");
    final File poemStud = new File("test1/poem");
    final File poemOrigin = new File("test1origin/poem");
    Git gitStud;
    Git gitOrigin;

    TaskService taskService = new TaskServiceDefault();

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

    void makeMonday() throws IOException, GitAPIException {
        gitStud.checkout().setName("quatrain3").call();
        BufferedWriter poemWriter = new BufferedWriter(new FileWriter(poemStud));

        poemWriter.write("Нам мнится: мир осиротелый\n" +
                "Неотразимый Рок настиг —\n" +
                "И мы, в борьбе, природой целой\n" +
                "Покинуты на нас самих.");

        poemWriter.close();

        gitStud.add().addFilepattern(".").call();
        gitStud.commit().setMessage("test commit").call();
    }

    void makeTuesday() throws IOException, GitAPIException {
        gitStud.checkout().setName("quatrain1").call();
        BufferedWriter poemWriter = new BufferedWriter(new FileWriter(poemStud));

        poemWriter.write("Часов однообразный бой,\n" +
                "Томительная ночи повесть!\n" +
                "Язык для всех равно чужой\n" +
                "И внятный каждому, как совесть!");

        poemWriter.close();

        gitStud.add().addFilepattern(".").call();
        gitStud.commit().setMessage("test commit").call();
    }

    void makeWednesday() throws IOException, GitAPIException {
        gitStud.checkout().setName("dev").call();
        BufferedWriter poemWriter = new BufferedWriter(new FileWriter(poemStud));

        poemWriter.write("Часов однообразный бой,\n" +
                "Томительная ночи повесть!\n" +
                "Язык для всех равно чужой\n" +
                "И внятный каждому, как совесть!\n\n" +
                "Нам мнится: мир осиротелый\n" +
                "Неотразимый Рок настиг —\n" +
                "И мы, в борьбе, природой целой\n" +
                "Покинуты на нас самих.");

        poemWriter.close();

        gitStud.add().addFilepattern(".").call();
        gitStud.commit().setMessage("test commit").call();
    }

    void makeThursday() throws IOException, GitAPIException {
        gitStud.checkout().setName("quatrain2").call();
        BufferedWriter poemWriter = new BufferedWriter(new FileWriter(poemStud));

        poemWriter.write("Кто без тоски внимал из нас,\n" +
                "Среди всемирного молчанья,\n" +
                "Глухие времени стенанья,\n" +
                "Пророчески-прощальный глас?");

        poemWriter.close();

        gitStud.add().addFilepattern(".").call();
        gitStud.commit().setMessage("test commit").call();
    }
}
