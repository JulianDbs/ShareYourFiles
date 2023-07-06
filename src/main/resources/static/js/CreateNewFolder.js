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
const createNewFolderArticle = document.getElementById("create-folder-article");
const createNewFolderForm = document.getElementById("create-folder-article-form");
const createNewFolderResult = document.getElementById("create-folder-article-result");
const createNewFolderResultMessage = document.getElementById("create-folder-article-result-message");
const createNewFolderError = document.getElementById("create-folder-article-error");
const createNewFolderErrorMessage = document.getElementById("create-folder-article-error-message");
const errorMessageElement = document.getElementById("create-folder-article-form-bad-request-message");

function createFolderButtonPressed() {
    createNewFolderArticle.classList.toggle("show-article");
}

function dcfHavePasswordCheckBoxPressed() {
    document.getElementById("create-folder-article-form-password-div").classList.toggle("show-create-folder-password-div");
    document.getElementById("create-folder-article-form-match-password-div").classList.toggle("show-create-folder-password-div");
}

function createFolderBackgroundPressed() {
    window.location.reload();
}

function createFolderContinueButtonPressed() {
    window.location.reload();
}

function createFolderErrorButtonPressed() {
    createNewFolderError.style.display = "none";
    createNewFolderErrorMessage.textContent = "";
    createNewFolderForm.style.display = "flex";
}

function processError(message) {
    createNewFolderForm.style.display = "none";
    createNewFolderError.style.display = "flex";
    createNewFolderErrorMessage.textContent = message;
}

function processBadRequest(message) {
    errorMessageElement.textContent = message;
    errorMessageElement.style.display = "flex";
}

function processResult(message) {
    createNewFolderForm.style.display = "none";
    createNewFolderResult.style.display = "flex";
    createNewFolderResultMessage.textContent = message;
}

createNewFolderForm.addEventListener('submit', event => {
    event.preventDefault();
    const formData = new FormData(createNewFolderForm);
    const formDataObj = {};
    formData.forEach((value, key) => (formDataObj[key] = value));
    const data = JSON.stringify(formDataObj);
    const requestOptions = { method: 'PUT', headers: {'Content-Type':'application/json'}, body: data };
    fetch('/folder/add-folder', requestOptions)
        .then(response => response.json())
        .then(
            response => {
                if (response.status >= 400) {
                    processError(response.message);
                }
                if (response.status == 200) {
                    processResult(response.message);
                }
                return response;
            },
            error => {
                processError(error.message);
            }
        );
});

