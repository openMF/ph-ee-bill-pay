package org.mifos.pheebillpay.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateHeaders {

    String[] requiredHeaders();

    Class<?> validatorClass();

    String validationFunction();
}
