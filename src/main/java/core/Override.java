package core;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface Override {
	int id() default 0;
}
