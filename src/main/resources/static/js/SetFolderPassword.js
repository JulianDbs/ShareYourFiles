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
const setFolderPasswordArticle = document.getElementById("set-folder-password-article");
const setFolderPasswordForm = document.getElementById("set-folder-password-article-form");
const setFolderPasswordResult = document.getElementById("set-folder-password-article-result");
const setFolderPasswordResultMessage = document.getElementById("set-folder-password-article-result-message");
const setFolderPasswordError = document.getElementById("set-folder-password-article-error");
const setFolderPasswordErrorMessage = document.getElementById("set-folder-password-article-error-message");

function setFolderPasswordButtonPressed() {
    setFolderPasswordResult.style.display = "none";
    setFolderPasswordResultMessage.innerText = "";
    setFolderPasswordArticle.classList.toggle("show-article");
}

function setFolderPasswordContinueButtonPressed() {
    setFolderPasswordButtonPressed();
    document.getElementById("folder-go-back-form").submit();
}

function setFolderPasswordErrorButtonPressed() {
    setFolderPasswordError.style.display = "none";
    setFolderPasswordErrorMessage.textContent = "";
    setFolderPasswordForm.style.display = "flex";
}

setFolderPasswordForm.addEventListener('submit', event => {
    event.preventDefault();
    const formData = new FormData(setFolderPasswordForm);
    const formDataObj = {};
    formData.forEach((value, key) => (formDataObj[key] = value));
    const data = JSON.stringify(formDataObj);
    const requestOptions = { method: 'PATCH', headers: {'Content-Type':'application/json'}, body: data };
    fetch('/folder/set-password', requestOptions)
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
       setFolderPasswordResultMessage.textContent = message;
       setFolderPasswordResult.style.display = "flex";
       setFolderPasswordForm.style.display = "none";
    }

    function processError(message) {
       setFolderPasswordErrorMessage.textContent = message;
       setFolderPasswordError.style.display = "flex";
       setFolderPasswordForm.style.display = "none";
    }
});
