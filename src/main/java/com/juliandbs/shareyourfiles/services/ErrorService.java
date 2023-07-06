package com.juliandbs.shareyourfiles.services;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

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
 * This class is used as a service to handle the error view requests.
 * @author JulianDbs
 */
@Service("errorService")
public class ErrorService implements ErrorServiceI {

    private static final String CUSTOM_ERROR_VIEW_PATH = "errors/custom-error";

    @Override
    public ModelAndView handleError(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView(CUSTOM_ERROR_VIEW_PATH);
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            mav.addObject("statusCode", statusCode);
        } else {
            mav.addObject("statusCode", -1);
        }
        return mav;
    }

}
