package com.kraven.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by Android Developer on 9/5/16.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerService {

}
