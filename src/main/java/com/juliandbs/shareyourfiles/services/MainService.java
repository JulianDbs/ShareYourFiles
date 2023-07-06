package com.juliandbs.shareyourfiles.services;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

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
 * This class is used as a service that handle some extra view requests.
 * @author JulianDbs
 */
@Service("mainService")
public class MainService implements MainServiceI {

    private static final String HOME_VIEW_PATH = "home/home";

    @Override
    public ModelAndView getIndexView(Authentication authentication) {
        ModelAndView mav;
        if (authentication == null) {
            mav = new ModelAndView(HOME_VIEW_PATH);
        } else {

            mav = new ModelAndView("redirect:/desktop");
        }
        return mav;
    }
}
