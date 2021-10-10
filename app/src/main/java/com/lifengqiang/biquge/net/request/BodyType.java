package com.lifengqiang.biquge.net.request;

import androidx.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
@StringDef({BodyType.NONE, BodyType.FORM, BodyType.URLENCODED, BodyType.RAW, BodyType.BINARY})
public @interface BodyType {
    String NONE = "none";
    String FORM = "form-data";
    String URLENCODED = "x-www-form-urlencoded";
    String RAW = "raw";
    String BINARY = "binary";
}
