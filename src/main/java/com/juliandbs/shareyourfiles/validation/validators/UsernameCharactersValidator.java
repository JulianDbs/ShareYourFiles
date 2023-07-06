package com.juliandbs.shareyourfiles.validation.validators;

import com.juliandbs.shareyourfiles.validation.annotations.ValidUsernameCharacters;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
public class UsernameCharactersValidator implements ConstraintValidator<ValidUsernameCharacters, String> {
    private static final String USERNAME_PATTERN = "^[A-Za-z0-9]*$";
    private static final Pattern PATTERN = Pattern.compile(USERNAME_PATTERN);

    @Override
    public boolean isValid(final String usernameField, final ConstraintValidatorContext context) {
        Matcher matcher = PATTERN.matcher(usernameField);
        return matcher.matches();
    }

}
