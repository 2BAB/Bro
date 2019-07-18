package me.xx2bab.bro.common.anno;

import java.lang.annotation.ElementType;
import java.util.List;

/**
 * Created on 2019-07-17
 */
public class AnnotatedElement {

    public String name;
    public ElementType type;
    public String clazz;
    public List<Annotation> annotations;

}
