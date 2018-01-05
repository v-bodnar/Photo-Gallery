package net.omb.photogallery.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This will log start of each method in the class and its execution time at the end
 *
 * Created by volodymyr.bodnar on 9/8/2017.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {
}
