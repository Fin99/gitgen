package com.tuneit.gitgen.data;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Poem {
    @XmlElement(name = "quatrain1")
    private String quatrain1;

    @XmlElement(name = "quatrain2")
    private String quatrain2;

    @XmlElement(name = "quatrain3")
    private String quatrain3;


}
