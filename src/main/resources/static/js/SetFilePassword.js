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
const setFilePasswordArticle = document.getElementById("set-file-password-article");
const setFilePasswordForm = document.getElementById("set-file-password-article-form");
const setFilePasswordResult = document.getElementById("set-file-password-article-result");
const setFilePasswordResultMessage = document.getElementById("set-file-password-article-result-message");
const setFilePasswordError = document.getElementById("set-file-password-article-error");
const setFilePasswordErrorMessage = document.getElementById("set-file-password-article-error-message");

function setFilePasswordButtonPressed() {
    setFilePasswordResult.style.display = "none";
    setFilePasswordResultMessage.innerText = "";
    setFilePasswordArticle.classList.toggle("show-article");
}

function setFilePasswordContinueButtonPressed() {
    setFilePasswordButtonPressed();
    document.getElementById("file-go-back-form").submit();
}

function setFilePasswordErrorButtonPressed() {
    setFilePasswordError.style.display = "none";
    setFilePasswordErrorMessage.textContent = "";
    setFilePasswordForm.style.display = "flex";
}

setFilePasswordForm.addEventListener('submit', event => {
    event.preventDefault();
    const formData = new FormData(setFilePasswordForm);
    const formDataObj = {};
    formData.forEach((value, key) => (formDataObj[key] = value));
    const data = JSON.stringify(formDataObj);
    const requestOptions = { method: 'PATCH', headers: {'Content-Type':'application/json'}, body: data };
    fetch('/file/set-password', requestOptions)
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
       setFilePasswordResultMessage.textContent = message;
       setFilePasswordResult.style.display = "flex";
       setFilePasswordForm.style.display = "none";
    }

    function processError(message) {
       setFilePasswordErrorMessage.textContent = message;
       setFilePasswordError.style.display = "flex";
       setFilePasswordForm.style.display = "none";
    }
});
