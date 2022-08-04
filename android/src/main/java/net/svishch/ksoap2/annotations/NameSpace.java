package net.svishch.ksoap2.annotations;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * SOAP Envelope namespace
 * Author: Vladimir Svishch
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value={FIELD})
public @interface NameSpace {
}

