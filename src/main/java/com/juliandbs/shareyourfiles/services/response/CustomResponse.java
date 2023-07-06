package com.juliandbs.shareyourfiles.services.response;

import org.springframework.ui.Model;

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
 * This class is used as a json response.
 * @author JulianDbs
 */
public class CustomResponse {

    private final String url;

    private final Model model;

    public CustomResponse(String url, Model model) throws NullPointerException {
        if (url == null || model == null)
            throw new NullPointerException();
        this.url = url;
        this.model = model;
    }

    public String getUrl() {return url;}

    public Model getModel() {return model;}

    @Override
    public boolean equals(Object object) throws NullPointerException {
        boolean result = false;
        if (object == null)
            throw new NullPointerException();
        if (object instanceof CustomResponse customResponse) {
            result = customResponse.getUrl().equals(url) &&
                    customResponse.getModel().toString().equals(model.toString());
        }
        return result;
    }

    @Override
    public int hashCode() {
        return url.hashCode() + model.hashCode();
    }

    @Override
    public String toString() {
        return url + " "  + model;
    }
}
