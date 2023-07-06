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
const changeFolderPasswordArticle = document.getElementById("change-folder-password-article");
const changeFolderPasswordForm = document.getElementById("change-folder-password-article-form");
const changeFolderPasswordResult = document.getElementById("change-folder-password-article-result");
const changeFolderPasswordResultMessage = document.getElementById("change-folder-password-article-result-message");
const changeFolderPasswordError = document.getElementById("change-folder-password-article-error");
const changeFolderPasswordErrorMessage = document.getElementById("change-folder-password-article-error-message");

function changeFolderPasswordButtonPressed() {
    changeFolderPasswordResult.style.display = "none";
    changeFolderPasswordResultMessage.innerText = "";
    changeFolderPasswordArticle.classList.toggle("show-article");
}

function changeFolderPasswordContinueButtonPressed() {
    changeFolderPasswordButtonPressed();
    document.getElementById("folder-go-back-form").submit();
}

function changeFolderPasswordErrorButtonPressed() {
    changeFolderPasswordError.style.display = "none";
    changeFolderPasswordErrorMessage.textContent = "";
    changeFolderPasswordForm.style.display = "flex";
}

changeFolderPasswordForm.addEventListener('submit', event => {
    event.preventDefault();
    const formData = new FormData(changeFolderPasswordForm);
    const formDataObj = {};
    formData.forEach((value, key) => (formDataObj[key] = value));
    const data = JSON.stringify(formDataObj);
    const requestOptions = { method: 'PATCH', headers: {'Content-Type':'application/json'}, body: data };
    fetch('/folder/change-password', requestOptions)
        .then(response => response.json())
        .then(
            response => {
                if(response.status >= 400) {
                    console.log("a");
                    processError(response.message);
                }
                if(response.status == 200) {
                    console.log("b");
                    processResult(response.message);
                }
                return response;
            },
            error => {
                console.log("c");
                processError(error.message);
            }
        );

    function processResult(message) {
        changeFolderPasswordResultMessage.textContent = message;
        changeFolderPasswordResult.style.display = "flex";
        changeFolderPasswordForm.style.display = "none";
    }

    function processError(message) {
        changeFolderPasswordErrorMessage.textContent = message;
        changeFolderPasswordError.style.display = "flex";
        changeFolderPasswordForm.style.display = "none";
    }
});
