package net.svishch.ksoap2.annotations;

import java.lang.reflect.Field;


public class SerializedNameUtil {

    public String getAnnotationValue(Field field) {
        SerializedName annotation = field.getAnnotation(SerializedName.class);
        String name = "";

        if (annotation == null && annotation.value() == null) {
           return name;
        }

        return annotation.value();
    }

}
