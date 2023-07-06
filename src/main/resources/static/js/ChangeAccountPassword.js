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
const changeAccountPasswordArticle = document.getElementById("change-account-password-article");
const changeAccountPasswordForm = document.getElementById("change-account-password-article-form");
const changeAccountPasswordResult = document.getElementById("change-account-password-article-result");
const changeAccountPasswordResultMessage = document.getElementById("change-account-password-article-result-message");
const changeAccountPasswordError = document.getElementById("change-account-password-article-error");
const changeAccountPasswordErrorMessage = document.getElementById("change-account-password-article-error-message");

function changeAccountPasswordButtonPressed() {
    changeAccountPasswordResult.style.display = "none";
    changeAccountPasswordResultMessage.innerText = "";
    changeAccountPasswordArticle.classList.toggle("show-article");
}

function changeAccountPasswordContinueButtonPressed() {
    changeAccountPasswordButtonPressed();
    document.getElementById("account-refresh-button").click();
}

function changeAccountPasswordErrorButtonPressed() {
    changeAccountPasswordError.style.display = "none";
    changeAccountPasswordErrorMessage.textContent = "";
    changeAccountPasswordForm.style.display = "flex";
}

changeAccountPasswordForm.addEventListener('submit', event => {
    event.preventDefault();
    const formData = new FormData(changeAccountPasswordForm);
    const formDataObj = {};
    formData.forEach((value, key) => (formDataObj[key] = value));
    const data = JSON.stringify(formDataObj);
    const requestOptions = { method: 'PATCH', headers: {'Content-Type':'application/json'}, body: data };
    fetch('/user/change-password', requestOptions)
        .then(response => response.json())
        .then(
            response => {
                if(response.status >= 400) {
                    processError(response.message);
                }
                if(response.status == 200) {
                   processResult(response.message);
                }
                return response;
            },
            error => {
            processError(error.message);
            }
        );

    function processResult(message) {
        changeAccountPasswordResultMessage.textContent = message;
        changeAccountPasswordResult.style.display = "flex";
        changeAccountPasswordForm.style.display = "none";
    }

    function processError(message) {
        changeAccountPasswordErrorMessage.textContent = message;
        changeAccountPasswordError.style.display = "flex";
        changeAccountPasswordForm.style.display = "none";
    }
});
