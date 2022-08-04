package net.svishch.ksoap2.util;

import net.svishch.ksoap2.annotations.SerializedName;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewInstanceObject {
    public  <T> T create(Class<T> classOfT) {
        T object = null;
        try {

            Constructor<T> constructor = classOfT.getDeclaredConstructor();
            constructor.setAccessible(true);
            object = constructor.newInstance();

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return object;
    }

    public <T>  boolean isSetValue(Field field, String valueName) {
        List<String> annotationNames = this.getAnnotationValue(field);
        for (String fieldName : annotationNames) {
            if (valueName.equals(fieldName)
                    || valueName.equalsIgnoreCase(field.getName())) {
                return true;
            }
        }
        return false;
    }

    private <T> List<String> getAnnotationValue(Field field) {
        SerializedName annotation = field.getAnnotation(SerializedName.class);
        if (annotation == null) {
            String name = "";
            return Collections.singletonList(name);
        }

        String serializedName = annotation.value();
        String[] alternates = annotation.alternate();
        if (alternates.length == 0) {
            return Collections.singletonList(serializedName);
        }

        List<String> fieldNames = new ArrayList<>(alternates.length + 1);
        fieldNames.add(serializedName);
        Collections.addAll(fieldNames, alternates);
        return fieldNames;
    }
}
