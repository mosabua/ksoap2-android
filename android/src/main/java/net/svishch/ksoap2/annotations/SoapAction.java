package net.svishch.ksoap2.annotations;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value={FIELD})
public @interface SoapAction {
}

