package com.tuneit;

import com.tuneit.bash.CommandResult;
import com.tuneit.bash.GitBashServiceDefault;
import com.tuneit.gen.TaskServiceDefault;
import com.tuneit.gen.Variant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.AfterEach;

import java.io.File;
import java.io.IOException;

@Slf4j
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
        Variant variant = new Variant(1, "test", 1);
        bashService.init(variant);
        log.info(bashService.executeCommand("git checkout quatrain3", variant).getCommandResult());
        log.info(bashService.poem("Нам мнится: мир осиротелый\n" +
                "Неотразимый Рок настиг —\n" +
                "И мы, в борьбе, природой целой\n" +
                "Покинуты на нас самих.", variant));
        log.info(bashService.executeCommand("git add .", variant).getCommandResult());
        CommandResult result = bashService.executeCommand("git commit -m \"test commit\"", variant);
        log.info(result.getCommandResult());
        log.info(result.getTask());
    }

    void makeMondayWithError() {
        Variant variant = new Variant(1, "test", 1);
        log.info(bashService.executeCommand("git checkout quatrain3", variant).getCommandResult());
        log.info(bashService.poem("Нам мнится: мир осиротелый\n" +
                "Неотразимый Рок настиг —\n" +
                "И мы, в борьбе, прироой целой\n" +
                "Покинуты на нас самих.", variant));
        log.info(bashService.executeCommand("git add .", variant).getCommandResult());
        CommandResult result = bashService.executeCommand("git commit -m \"test commit\"", variant);
        log.info(result.getCommandResult());
        log.info(result.getTask());
    }

    void makeTuesday() {
        Variant variant = new Variant(2, "test", 1);
        log.info(bashService.executeCommand("git checkout quatrain1", variant).getCommandResult());
        log.info(bashService.poem("Часов однообразный бой,\n" +
                "Томительная ночи повесть!\n" +
                "Язык для всех равно чужой\n" +
                "И внятный каждому, как совесть!", variant));
        log.info(bashService.executeCommand("git add .", variant).getCommandResult());
        CommandResult result = bashService.executeCommand("git commit -m \"test commit\"", variant);
        log.info(result.getCommandResult());
        log.info(result.getTask());
    }

    void makeTuesdayWithError() {
        Variant variant = new Variant(2, "test", 1);
        log.info(bashService.executeCommand("git checkout quatrain1", variant).getCommandResult());
        log.info(bashService.poem("Часов однообразный бой,\n" +
                "Томительная ночи повесть!\n" +
                "Язык для всех равн чужой\n" +
                "И внятный каждому, как совесть!", variant));
        log.info(bashService.executeCommand("git add .", variant).getCommandResult());
        CommandResult result = bashService.executeCommand("git commit -m \"test commit\"", variant);
        log.info(result.getCommandResult());
        log.info(result.getTask());
    }

    void makeWednesday() {
        Variant variant = new Variant(3, "test", 1);
        log.info(bashService.executeCommand("git checkout dev", variant).getCommandResult());
        log.info(bashService.executeCommand("git merge quatrain3", variant).getCommandResult());
        log.info(bashService.poem("Часов однообразный бой,\n" +
                "Томительная ночи повесть!\n" +
                "Язык для всех равно чужой\n" +
                "И внятный каждому, как совесть!\n\n" +
                "Нам мнится: мир осиротелый\n" +
                "Неотразимый Рок настиг —\n" +
                "И мы, в борьбе, природой целой\n" +
                "Покинуты на нас самих.", variant));
        log.info(bashService.executeCommand("git add .", variant).getCommandResult());
        CommandResult result = bashService.executeCommand("git commit -m \"test commit\"", variant);
        log.info(result.getCommandResult());
        log.info(result.getTask());
    }

    void makeWednesdayWithError() {
        Variant variant = new Variant(3, "test", 1);
        log.info(bashService.executeCommand("git checkout dev", variant).getCommandResult());
        log.info(bashService.executeCommand("git merge quatrain3", variant).getCommandResult());
        log.info(bashService.poem("Часов однообразный бой,\n" +
                "Томительная ночи повесть!\n" +
                "Язык для всех равно чужой\n" +
                "И внятный каждому, как совесть!\n\n" +
                "Нам мнится: мир осиротелый\n" +
                "Неотразимый Рокнастиг —\n" +
                "И мы, в борьбе, природой целой\n" +
                "Покинуты на нас самих.", variant));
        log.info(bashService.executeCommand("git add .", variant).getCommandResult());
        CommandResult result = bashService.executeCommand("git commit -m \"test commit\"", variant);
        log.info(result.getCommandResult());
        log.info(result.getTask());
    }

    void makeThursday() {
        Variant variant = new Variant(4, "test", 1);
        log.info(bashService.executeCommand("git checkout quatrain2", variant).getCommandResult());
        log.info(bashService.poem("Кто без тоски внимал из нас,\n" +
                "Среди всемирного молчанья,\n" +
                "Глухие времени стенанья,\n" +
                "Пророчески-прощальный глас?", variant));
        log.info(bashService.executeCommand("git add .", variant).getCommandResult());
        CommandResult result = bashService.executeCommand("git commit -m \"test commit\"", variant);
        log.info(result.getCommandResult());
        log.info(result.getTask());
    }

    void makeThursdayWithError() {
        Variant variant = new Variant(4, "test", 1);
        log.info(bashService.executeCommand("git checkout quatrain2", variant).getCommandResult());
        log.info(bashService.poem("Кто без тоски внимал из нас,\n" +
                "Среди всемирного молчанья,\n" +
                "Глухие времени стенаья,\n" +
                "Пророчески-прощальный глас?", variant));
        log.info(bashService.executeCommand("git add .", variant).getCommandResult());
        CommandResult result = bashService.executeCommand("git commit -m \"test commit\"", variant);
        log.info(result.getCommandResult());
        log.info(result.getTask());
    }

    void makeFriday() {
        Variant variant = new Variant(5, "test", 1);
        log.info(bashService.executeCommand("git checkout dev", variant).getCommandResult());
        log.info(bashService.executeCommand("git merge quatrain2", variant).getCommandResult());
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
        log.info(bashService.executeCommand("git add .", variant).getCommandResult());
        CommandResult result = bashService.executeCommand("git commit -m \"test commit\"", variant);
        log.info(result.getCommandResult());
        log.info(result.getTask());
    }

    void makeFridayWithError() {
        Variant variant = new Variant(5, "test", 1);
        log.info(bashService.executeCommand("git checkout dev", variant).getCommandResult());
        log.info(bashService.executeCommand("git merge quatrain2", variant).getCommandResult());
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
        log.info(bashService.executeCommand("git add .", variant).getCommandResult());
        CommandResult result = bashService.executeCommand("git commit -m \"test commit\"", variant);
        log.info(result.getCommandResult());
        log.info(result.getTask());
    }
}
