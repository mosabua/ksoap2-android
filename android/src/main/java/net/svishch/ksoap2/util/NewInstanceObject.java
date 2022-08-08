package net.svishch.ksoap2.util;

import net.svishch.ksoap2.annotations.SerializedNameUtil;
import net.svishch.ksoap2.tosoap.ToSoap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.logging.Logger;

public class NewInstanceObject {
    private Logger logger;

    public NewInstanceObject() {
        initLog();
    }

    private void initLog() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    public <T> T create(Class<T> classOfT) {
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

    public boolean isAnnotationValue(Field field, String value) {
        String annotationValue = new SerializedNameUtil().getAnnotationValue(field);

        return value.equals(annotationValue)
                || value.equalsIgnoreCase(field.getName());

    }

    /*
    public <T>  boolean isAnnotationValue(Field field, String valueName) {
        List<String> annotationNames = new SerializedNameUtil().getAnnotationValue(field);
        for (String fieldName : annotationNames) {
            if (valueName.equals(fieldName)
                    || valueName.equalsIgnoreCase(field.getName())) {
                return true;
            }
        }
        return false;
    }
     */

    public <T extends Annotation> boolean isAnnotation(Field field, Class<T> classAnnotation) {
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getTypeName().equals(classAnnotation.getTypeName())) {
                return true;
            }
        }
        return false;
    }

    private void printFields(Field[] fields) {
        for (Field field : fields) {
            field.setAccessible(true);
            System.out.printf("%s %s %s%n",
                    Modifier.toString(field.getModifiers()),
                    field.getType().getSimpleName(),
                    field.getName()
            );
        }
    }

    public Object getValue(Object obj, Field field) {

        Class<?> fieldType = field.getType();

        try {

            if (fieldType.isPrimitive() || fieldType.equals(String.class)
            ) {
                return field.get(obj);
            }

            sendLogger(String.format("No field  %s",fieldType.toString()));

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
        /*
        Class<?> fieldType = field.getType();
            else if (fieldType.equals(long.class)) {
                field.setLong(object, ParseSoapUtil.checkLong(value.toString()));
            } else if (fieldType.equals(int.class)) {
                field.setInt(object, ParseSoapUtil.checkInt(value.toString()));
            } else if (fieldType.equals(boolean.class)) {
                field.setBoolean(object, ParseSoapUtil.checkBoolean(value));
            } else if (fieldType.equals(List.class)) {
                this.setList(object, field, value);
            } else if (value.getClass().getTypeName().equals(SoapObject.class.getTypeName())) {
                field.set(object, formSoap((SoapObject) value, field.getType()));
            } else {
                System.err.println(field.getType() + " != " + value.getClass().getName());
            }
        */
    }

    private void sendLogger(String mess) {
        this.logger.info(mess);
    }
}
