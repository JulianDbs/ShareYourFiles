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
const changeAccountUsernameArticle = document.getElementById("change-account-username-article");
const changeAccountUsernameForm = document.getElementById("change-account-username-article-form");
const changeAccountUsernameResult = document.getElementById("change-account-username-article-result");
const changeAccountUsernameResultMessage = document.getElementById("change-account-username-article-result-message");
const changeAccountUsernameError = document.getElementById("change-account-username-article-error");
const changeAccountUsernameErrorMessage = document.getElementById("change-account-username-article-error-message");

function changeAccountUsernameButtonPressed() {
    changeAccountUsernameResult.style.display = "none";
    changeAccountUsernameResultMessage.innerText = "";
    changeAccountUsernameArticle.classList.toggle("show-article");
}

function changeAccountUsernameContinueButtonPressed() {
    changeAccountUsernameButtonPressed();
    document.getElementById("account-refresh-button").click();
}

function changeAccountUsernameErrorButtonPressed() {
    changeAccountUsernameError.style.display = "none";
    changeAccountUsernameErrorMessage.textContent = "";
    changeAccountUsernameForm.style.display = "flex";
}

changeAccountUsernameForm.addEventListener('submit', event => {
    event.preventDefault();
    const formData = new FormData(changeAccountUsernameForm);
    const formDataObj = {};
    formData.forEach((value, key) => (formDataObj[key] = value));
    const data = JSON.stringify(formDataObj);
    const requestOptions = { method: 'PATCH', headers: {'Content-Type':'application/json'}, body: data };
    fetch('/user/change-username', requestOptions)
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
        changeAccountUsernameResultMessage.textContent = message;
        changeAccountUsernameResult.style.display = "flex";
        changeAccountUsernameForm.style.display = "none";
    }

    function processError(message) {
        changeAccountUsernameErrorMessage.textContent = message;
        changeAccountUsernameError.style.display = "flex";
        changeAccountUsernameForm.style.display = "none";
    }
});
