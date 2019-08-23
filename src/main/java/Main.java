import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Main {
    public static final String poem1 = "Часов однообразный бой,\n" +
            "Томительная ночи повесть!\n" +
            "Язык для всех равно чужой\n" +
            "И внятный каждому, как совесть!";

    public static final String poem2 = "Кто без тоски внимал из нас,\n" +
            "Среди всемирного молчанья,\n" +
            "Глухие времени стенанья,\n" +
            "Пророчески-прощальный глас?";

    public static final String poem3 = "Нам мнится: мир осиротелый\n" +
            "Неотразимый Рок настиг —\n" +
            "И мы, в борьбе, природой целой\n" +
            "Покинуты на нас самих.";

    public static final String[] poems = new String[]{poem1, poem2, poem3};


    public static final String nameTestDir = "test";
    public static int indexCommit = 0;

    public static final String getFileName() {
        return nameTestDir + "/c" + indexCommit++ + ".txt ";
    }

    public static void main(String[] args) throws IOException {

        File testDir = new File(nameTestDir);
        testDir.mkdir();


        File file = new File(getFileName());
        file.createNewFile();

        String oldPoem = "";

        for (String poem : poems) {
            Random random = new Random(1024);
            file = new File(getFileName());
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(oldPoem);
            writer.write(deleteLine(updateError(deleteWord(poem, random), random), random));
            writer.flush();

            random = new Random(1024);
            file = new File(getFileName());
            file.createNewFile();
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(oldPoem);
            writer.write(updateError(deleteWord(poem, random), random));
            writer.flush();

            random = new Random(1024);
            file = new File(getFileName());
            file.createNewFile();
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(oldPoem);
            writer.write(deleteWord(poem, random));
            writer.flush();

            file = new File(getFileName());
            file.createNewFile();
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(oldPoem);
            writer.write(poem);
            writer.flush();

            oldPoem += poem + "\n\n";
        }
    }

    private static String deleteLine(String poem, Random random) {
        String[] splitPoem = poem.split("\n");
        int removedLine = random.nextInt(splitPoem.length);

        StringBuilder resultPoem = new StringBuilder();
        for (int i = 0; i < splitPoem.length; i++) {
            if (i != removedLine) {
                resultPoem.append(splitPoem[i]);
            }

            if (i != splitPoem.length - 1) {
                resultPoem.append('\n');
            }
        }

        return resultPoem.toString();
    }

    private static String updateError(String poem, Random random) {
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

    private static String deleteWord(String poem, Random random) {
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
