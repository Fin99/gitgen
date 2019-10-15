package com.tuneit.gitgen;

import com.tuneit.gitgen.bash.GitBashServiceDefault;
import com.tuneit.gitgen.data.Variant;
import com.tuneit.gitgen.gen.TaskServiceDefault;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.AfterEach;

import java.io.File;
import java.io.IOException;

@Slf4j
abstract class RepoData {
    final File dirStud = new File(System.getProperty("user.home") + "/gitgen/" +"/test1");
    final File dirOrigin = new File(System.getProperty("user.home") + "/gitgen/" +"/test1origin");
    final File poemStud = new File(System.getProperty("user.home") + "/gitgen/" +"/test1/poem");
    final File poemOrigin = new File(System.getProperty("user.home") + "/gitgen/" +"/test1origin/poem");
    Git gitStud;
    Git gitOrigin;

    TaskService taskService = new TaskServiceDefault();
    GitBashService bashService = new GitBashServiceDefault();

    Variant variant = new Variant("test", 1);

    void initGit() throws IOException {
        gitStud = Git.open(dirStud);
        gitOrigin = Git.open(dirOrigin);
    }

    @AfterEach
    void deleteRepo() throws IOException {
        FileUtils.deleteDirectory(dirStud);
        FileUtils.deleteDirectory(dirOrigin);
    }

    void makeMonday() {
        log.info(bashService.executeCommand("git checkout quatrain3", variant));
        log.info(bashService.poem("Нам мнится: мир осиротелый\n" +
                "Неотразимый Рок настиг —\n" +
                "И мы, в борьбе, природой целой\n" +
                "Покинуты на нас самих.", variant));
        log.info(bashService.executeCommand("git add .", variant));
        log.info(bashService.executeCommand("git commit -m \"test commit\"", variant));
    }

    void makeMondayWithError() {
        log.info(bashService.executeCommand("git checkout quatrain3", variant));
        log.info(bashService.poem("Нам мнится: мир осиротелый\n" +
                "Неотразимый Рок настиг —\n" +
                "И мы, в борьбе, прироой целой\n" +
                "Покинуты на нас самих.", variant));
        log.info(bashService.executeCommand("git add .", variant));
        log.info(bashService.executeCommand("git commit -m \"test commit\"", variant));
    }

    void makeTuesday() {
        log.info(bashService.executeCommand("git checkout quatrain1", variant));
        log.info(bashService.poem("Часов однообразный бой,\n" +
                "Томительная ночи повесть!\n" +
                "Язык для всех равно чужой\n" +
                "И внятный каждому, как совесть!", variant));
        log.info(bashService.executeCommand("git add .", variant));
        log.info(bashService.executeCommand("git commit -m \"test commit\"", variant));
    }

    void makeTuesdayWithError() {
        log.info(bashService.executeCommand("git checkout quatrain1", variant));
        log.info(bashService.poem("Часов однообразный бой,\n" +
                "Томительная ночи повесть!\n" +
                "Язык для всех равн чужой\n" +
                "И внятный каждому, как совесть!", variant));
        log.info(bashService.executeCommand("git add .", variant));
        log.info(bashService.executeCommand("git commit -m \"test commit\"", variant));
    }

    void makeWednesday() {
        log.info(bashService.executeCommand("git checkout dev", variant));
        log.info(bashService.executeCommand("git merge quatrain3", variant));
        log.info(bashService.poem("Часов однообразный бой,\n" +
                "Томительная ночи повесть!\n" +
                "Язык для всех равно чужой\n" +
                "И внятный каждому, как совесть!\n\n" +
                "Нам мнится: мир осиротелый\n" +
                "Неотразимый Рок настиг —\n" +
                "И мы, в борьбе, природой целой\n" +
                "Покинуты на нас самих.", variant));
        log.info(bashService.executeCommand("git add .", variant));
        log.info(bashService.executeCommand("git commit -m \"test commit\"", variant));
    }

    void makeWednesdayWithError() {
        log.info(bashService.executeCommand("git checkout dev", variant));
        log.info(bashService.executeCommand("git merge quatrain3", variant));
        log.info(bashService.poem("Часов однообразный бой,\n" +
                "Томительная ночи повесть!\n" +
                "Язык для всех равно чужой\n" +
                "И внятный каждому, как совесть!\n\n" +
                "Нам мнится: мир осиротелый\n" +
                "Неотразимый Рокнастиг —\n" +
                "И мы, в борьбе, природой целой\n" +
                "Покинуты на нас самих.", variant));
        log.info(bashService.executeCommand("git add .", variant));
        log.info(bashService.executeCommand("git commit -m \"test commit\"", variant));
    }

    void makeThursday() {
        log.info(bashService.executeCommand("git checkout quatrain2", variant));
        log.info(bashService.poem("Кто без тоски внимал из нас,\n" +
                "Среди всемирного молчанья,\n" +
                "Глухие времени стенанья,\n" +
                "Пророчески-прощальный глас?", variant));
        log.info(bashService.executeCommand("git add .", variant));
        log.info(bashService.executeCommand("git commit -m \"test commit\"", variant));
    }

    void makeThursdayWithError() {
        log.info(bashService.executeCommand("git checkout quatrain2", variant));
        log.info(bashService.poem("Кто без тоски внимал из нас,\n" +
                "Среди всемирного молчанья,\n" +
                "Глухие времени стенаья,\n" +
                "Пророчески-прощальный глас?", variant));
        log.info(bashService.executeCommand("git add .", variant));
        log.info(bashService.executeCommand("git commit -m \"test commit\"", variant));
    }

    void makeFriday() {
        log.info(bashService.executeCommand("git checkout dev", variant));
        log.info(bashService.executeCommand("git merge quatrain2", variant));
        log.info(bashService.poem("Часов однообразный бой,\n" +
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
                "Покинуты на нас самих.", variant));
        log.info(bashService.executeCommand("git add .", variant));
        log.info(bashService.executeCommand("git commit -m \"test commit\"", variant));
    }

    void makeFridayWithError() {
        log.info(bashService.executeCommand("git checkout dev", variant));
        log.info(bashService.executeCommand("git merge quatrain2", variant));
        log.info(bashService.poem("Часов однообразный бой,\n" +
                "Томительна ночи повесть!\n" +
                "Язык для всех равно чужой\n" +
                "И внятный каждому, как совесть!\n\n" +
                "Кто без тоски внимал из нас,\n" +
                "Среди всемирного молчанья,\n" +
                "Глухие времени стенанья,\n" +
                "Пророчески-прощальный глас?\n\n" +
                "Нам мнится: мир осиротелый\n" +
                "Неотразимый Рок настиг —\n" +
                "И мы, в борьбе, природой целой\n" +
                "Покинуты на нас самих.", variant));
        log.info(bashService.executeCommand("git add .", variant));
        log.info(bashService.executeCommand("git commit -m \"test commit\"", variant));
    }
}
