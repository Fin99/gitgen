package com.tuneit.gen;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

//xml wrapper
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Poems {
    private static Poems singleton;
    @XmlElement(name = "poem")
    private List<Poem> poems;

    public static Poem getRandomPoem(Random random) {
        return getPoems().get(random.nextInt(getPoems().size()));
    }

    public static String deleteWord(String poem, Random random) {
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

    public static String switchSymbols(String poem, Random random) {
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

    public static String switchLine(String poem, Random random) {
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

    private static List<Poem> getPoems() {
        if (singleton == null) {
            try {
                JAXBContext jc = JAXBContext.newInstance(Poems.class);
                InputStream inputStream = Poems.class.getClassLoader().getResourceAsStream("poems.xml");
                Source source = new StreamSource(inputStream);
                Unmarshaller unmarshaller = jc.createUnmarshaller();
                singleton = unmarshaller.unmarshal(source, Poems.class).getValue();
                trimPoems();
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }

        return singleton.poems;
    }

    private static void trimPoems() {
        for (Poem poem : singleton.poems) {
            poem.setQuatrain1(trimQuatrain(poem.getQuatrain1()));
            poem.setQuatrain2(trimQuatrain(poem.getQuatrain2()));
            poem.setQuatrain3(trimQuatrain(poem.getQuatrain3()));
        }
    }

    private static String trimQuatrain(String quatrain) {
        StringBuilder quatrainBuilder = new StringBuilder();
        String[] splitQuatrain = quatrain.trim().split("\n");
        for (int i = 0; i < splitQuatrain.length; i++) {
            quatrainBuilder.append(splitQuatrain[i].trim());
            if (i != splitQuatrain.length - 1) {
                quatrainBuilder.append("\n");
            }
        }
        return quatrainBuilder.toString();
    }
}