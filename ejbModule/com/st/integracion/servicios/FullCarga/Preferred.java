package com.st.integracion.servicios.FullCarga;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

@Qualifier
@Retention(value = RetentionPolicy.RUNTIME )
//@Target({ElementType.ANNOTATION_TYPE.METHOD})
public @interface Preferred {

}
