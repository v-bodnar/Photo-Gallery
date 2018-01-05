package net.omb.photogallery.annotations;

import java.lang.annotation.*;

@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithUser {
    String username() default "";
}
