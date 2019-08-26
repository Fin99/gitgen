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
    @XmlElement(name = "poem")
    private List<Poem> poems;

    private static Poems singleton;

    public static Poem getRandomPoem(Random random) {
        return getPoems().get(random.nextInt(getPoems().size()));
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