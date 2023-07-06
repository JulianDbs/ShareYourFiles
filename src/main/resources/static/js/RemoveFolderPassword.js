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
const removeFolderPasswordArticle = document.getElementById("remove-folder-password-article");
const removeFolderPasswordForm = document.getElementById("remove-folder-password-article-form");
const removeFolderPasswordResult = document.getElementById("remove-folder-password-article-result");
const removeFolderPasswordResultMessage = document.getElementById("remove-folder-password-article-result-message");
const removeFolderPasswordError = document.getElementById("remove-folder-password-article-error");
const removeFolderPasswordErrorMessage = document.getElementById("remove-folder-password-article-error-message");

function removeFolderPasswordButtonPressed() {
    removeFolderPasswordResult.style.display = "none";
    removeFolderPasswordResultMessage.innerText = "";
    removeFolderPasswordArticle.classList.toggle("show-article");
}

function removeFolderPasswordContinueButtonPressed() {
    removeFolderPasswordButtonPressed();
    document.getElementById("folder-go-back-form").submit();
}

function removeFolderPasswordErrorButtonPressed() {
    removeFolderPasswordError.style.display = "none";
    removeFolderPasswordErrorMessage.textContent = "";
    removeFolderPasswordForm.style.display = "flex";
}

removeFolderPasswordForm.addEventListener('submit', event => {
    event.preventDefault();
    const formData = new FormData(removeFolderPasswordForm);
    const formDataObj = {};
    formData.forEach((value, key) => (formDataObj[key] = value));
    const data = JSON.stringify(formDataObj);
    const requestOptions = { method: 'PATCH', headers: {'Content-Type':'application/json'}, body: data };
    fetch('/folder/remove-password', requestOptions)
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
       removeFolderPasswordResultMessage.textContent = message;
       removeFolderPasswordResult.style.display = "flex";
       removeFolderPasswordForm.style.display = "none";
    }

    function processError(message) {
       removeFolderPasswordErrorMessage.textContent = message;
       removeFolderPasswordError.style.display = "flex";
       removeFolderPasswordForm.style.display = "none";
    }
});
