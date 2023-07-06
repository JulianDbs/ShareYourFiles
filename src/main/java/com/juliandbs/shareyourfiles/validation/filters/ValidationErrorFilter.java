package com.juliandbs.shareyourfiles.validation.filters;

import com.juliandbs.shareyourfiles.validation.annotations.ValidUsername;
import com.juliandbs.shareyourfiles.validation.annotations.ValidUsernameCharacters;
import com.juliandbs.shareyourfiles.validation.annotations.ValidUsernameSize;
import com.juliandbs.shareyourfiles.validation.annotations.ValidEmail;
import com.juliandbs.shareyourfiles.validation.annotations.ValidEmailPattern;
import com.juliandbs.shareyourfiles.validation.annotations.PasswordMatches;
import com.juliandbs.shareyourfiles.validation.annotations.ValidPassword;
import com.juliandbs.shareyourfiles.validation.annotations.ValidPasswordSize;
import com.juliandbs.shareyourfiles.validation.annotations.ValidMatchingPassword;
import com.juliandbs.shareyourfiles.validation.annotations.ValidMatchingPasswordSize;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

import java.lang.NullPointerException;

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
@Component("validationErrorFilter")
public class ValidationErrorFilter {
    private static final List<String> usernameClassList = new LinkedList<>();
    private static final List<String> emailClassList = new LinkedList<>();
    private static final List<String> passwordClassList = new LinkedList<>();
    private static final List<String> matchingPasswordClassList = new LinkedList<>();

    static {
        usernameClassList.add(ValidUsername.class.getSimpleName());
        usernameClassList.add(ValidUsernameCharacters.class.getSimpleName());
        usernameClassList.add(ValidUsernameSize.class.getSimpleName());
        emailClassList.add(ValidEmail.class.getSimpleName());
        emailClassList.add(ValidEmailPattern.class.getSimpleName());
        passwordClassList.add(PasswordMatches.class.getSimpleName());
        passwordClassList.add(ValidPassword.class.getSimpleName());
        passwordClassList.add(ValidPasswordSize.class.getSimpleName());
        matchingPasswordClassList.add(PasswordMatches.class.getSimpleName());
        matchingPasswordClassList.add(ValidMatchingPassword.class.getSimpleName());
        matchingPasswordClassList.add(ValidMatchingPasswordSize.class.getSimpleName());
    }

    public List<String> getUsernameErrors(Errors errors) throws NullPointerException {
        if (errors == null)
            throw new NullPointerException();
        return filterList(errors, usernameClassList);
    }

    public List<String> getEmailErrors(Errors errors) throws NullPointerException {
        if (errors == null)
            throw new NullPointerException();
        return filterList(errors, emailClassList);
    }

    public List<String> getPasswordErrors(Errors errors) throws NullPointerException {
        if (errors == null)
            throw new NullPointerException();
        return filterList(errors, passwordClassList);
    }

    public List<String> getMatchingPasswordErrors(Errors errors) throws NullPointerException {
        if (errors == null)
            throw new NullPointerException();
        return filterList(errors, matchingPasswordClassList);
    }

    private List<String> filterList(Errors errors, List<String> classList) {
        List<ObjectError> errorList = errors.getAllErrors();
        List<ObjectError> finalList = new LinkedList<>();
        for (String usernameClass : classList) {
            finalList.addAll(
                    errorList.stream()
                            .filter(e -> Objects.requireNonNull(e.getCode()).equals(usernameClass))
                            .collect(Collectors.toCollection(LinkedList::new))
            );
        }
        return finalList.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
