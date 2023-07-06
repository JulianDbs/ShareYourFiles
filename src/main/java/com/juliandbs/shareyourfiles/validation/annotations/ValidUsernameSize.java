package com.juliandbs.shareyourfiles.validation.annotations;

import com.juliandbs.shareyourfiles.validation.validators.UsernameSizeValidator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

/**
 * @author JulianDbs
 */
@Documented
@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Constraint(validatedBy = UsernameSizeValidator.class)
@Retention(RUNTIME)
public @interface ValidUsernameSize {

    String message() default "Username length must be greater than 3 and less than 21";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}




