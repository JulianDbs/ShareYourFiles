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
const changeFilePasswordArticle = document.getElementById("change-file-password-article");
const changeFilePasswordForm = document.getElementById("change-file-password-article-form");
const changeFilePasswordResult = document.getElementById("change-file-password-article-result");
const changeFilePasswordResultMessage = document.getElementById("change-file-password-article-result-message");
const changeFilePasswordError = document.getElementById("change-file-password-article-error");
const changeFilePasswordErrorMessage = document.getElementById("change-file-password-article-error-message");

function changeFilePasswordButtonPressed() {
    changeFilePasswordResult.style.display = "none";
    changeFilePasswordResultMessage.innerText = "";
    changeFilePasswordArticle.classList.toggle("show-article");
}

function changeFilePasswordContinueButtonPressed() {
    changeFilePasswordButtonPressed();
    document.getElementById("file-refresh-form").submit();
}

function changeFilePasswordErrorButtonPressed() {
    changeFilePasswordError.style.display = "none";
    changeFilePasswordErrorMessage.textContent = "";
    changeFilePasswordForm.style.display = "flex";
}

changeFilePasswordForm.addEventListener('submit', event => {
    event.preventDefault();
    const formData = new FormData(changeFilePasswordForm);
    const formDataObj = {};
    formData.forEach((value, key) => (formDataObj[key] = value));
    const data = JSON.stringify(formDataObj);
    const requestOptions = { method: 'PATCH', headers: {'Content-Type':'application/json'}, body: data };
    fetch('/file/change-password', requestOptions)
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
        changeFilePasswordResultMessage.textContent = message;
        changeFilePasswordResult.style.display = "flex";
        changeFilePasswordForm.style.display = "none";
    }

    function processError(message) {
        changeFilePasswordErrorMessage.textContent = message;
        changeFilePasswordError.style.display = "flex";
        changeFilePasswordForm.style.display = "none";
    }
});
