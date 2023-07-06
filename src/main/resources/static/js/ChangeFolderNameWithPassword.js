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
const changeFolderNameArticle = document.getElementById("change-folder-name-article");
const changeFolderNameForm = document.getElementById("change-folder-name-article-form");
const changeFolderNameResult = document.getElementById("change-folder-name-article-result");
const changeFolderNameResultMessage = document.getElementById("change-folder-name-article-result-message");
const changeFolderNameError = document.getElementById("change-folder-name-article-error");
const changeFolderNameErrorMessage = document.getElementById("change-folder-name-article-error-message");

function changeFolderNameButtonPressed() {
    changeFolderNameResult.style.display = "none";
    changeFolderNameResultMessage.innerText = "";
    changeFolderNameArticle.classList.toggle("show-article");
}

function changeFolderNameContinueButtonPressed() {
    changeFolderNameButtonPressed();
    document.getElementById("folder-go-back-form").submit();
}

function changeFolderNameErrorButtonPressed() {
    changeFolderNameError.style.display = "none";
    changeFolderNameErrorMessage.textContent = "";
    changeFolderNameForm.style.display = "flex";
}

changeFolderNameForm.addEventListener('submit', event => {
    event.preventDefault();
    const formData = new FormData(changeFolderNameForm);
    const formDataObj = {};
    formData.forEach((value, key) => (formDataObj[key] = value));
    const data = JSON.stringify(formDataObj);
    const requestOptions = { method: 'PATCH', headers: {'Content-Type':'application/json'}, body: data };
    fetch('/folder/change-folder-name', requestOptions)
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
        changeFolderNameResultMessage.textContent = message;
        changeFolderNameResult.style.display = "flex";
        changeFolderNameForm.style.display = "none";
    }

    function processError(message) {
        changeFolderNameErrorMessage.textContent = message;
        changeFolderNameError.style.display = "flex";
        changeFolderNameForm.style.display = "none";
    }
});
