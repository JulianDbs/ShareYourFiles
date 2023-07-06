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
const removeFilePasswordArticle = document.getElementById("remove-file-password-article");
const removeFilePasswordForm = document.getElementById("remove-file-password-article-form");
const removeFilePasswordResult = document.getElementById("remove-file-password-article-result");
const removeFilePasswordResultMessage = document.getElementById("remove-file-password-article-result-message");
const removeFilePasswordError = document.getElementById("remove-file-password-article-error");
const removeFilePasswordErrorMessage = document.getElementById("remove-file-password-article-error-message");

function removeFilePasswordButtonPressed() {
    removeFilePasswordResult.style.display = "none";
    removeFilePasswordResultMessage.innerText = "";
    removeFilePasswordArticle.classList.toggle("show-article");
}

function removeFilePasswordContinueButtonPressed() {
    removeFilePasswordButtonPressed();
    document.getElementById("file-go-back-form").submit();
}

function removeFilePasswordErrorButtonPressed() {
    removeFilePasswordError.style.display = "none";
    removeFilePasswordErrorMessage.textContent = "";
    removeFilePasswordForm.style.display = "flex";
}

removeFilePasswordForm.addEventListener('submit', event => {
    event.preventDefault();
    const formData = new FormData(removeFilePasswordForm);
    const formDataObj = {};
    formData.forEach((value, key) => (formDataObj[key] = value));
    const data = JSON.stringify(formDataObj);
    const requestOptions = { method: 'PATCH', headers: {'Content-Type':'application/json'}, body: data };
    fetch('/file/remove-password', requestOptions)
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
       removeFilePasswordResultMessage.textContent = message;
       removeFilePasswordResult.style.display = "flex";
       removeFilePasswordForm.style.display = "none";
    }

    function processError(message) {
       removeFilePasswordErrorMessage.textContent = message;
       removeFilePasswordError.style.display = "flex";
       removeFilePasswordForm.style.display = "none";
    }
});
