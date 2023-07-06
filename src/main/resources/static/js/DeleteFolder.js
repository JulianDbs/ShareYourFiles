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
const deleteFolderArticle = document.getElementById("delete-folder-article");
const deleteFolderForm = document.getElementById("delete-folder-article-form");
const deleteFolderResult = document.getElementById("delete-folder-article-result");
const deleteFolderResultMessage = document.getElementById("delete-folder-article-result-message");
const deleteFolderError = document.getElementById("delete-folder-article-error");
const deleteFolderErrorMessage = document.getElementById("delete-folder-article-error-message");

function deleteFolderButtonPressed() {
    deleteFolderResult.style.display = "none";
    deleteFolderResultMessage.innerText = "";
    deleteFolderArticle.classList.toggle("show-article");
}

function deleteFolderContinueButtonPressed() {
    deleteFolderButtonPressed();
    document.getElementById("folder-go-back-form").submit();
}

function deleteFolderErrorButtonPressed() {
    deleteFolderError.style.display = "none";
    deleteFolderErrorMessage.textContent = "";
    deleteFolderForm.style.display = "flex";
}

deleteFolderForm.addEventListener('submit', event => {
    event.preventDefault();
    const formData = new FormData(deleteFolderForm);
    const formDataObj = {};
    formData.forEach((value, key) => (formDataObj[key] = value));
    const data = JSON.stringify(formDataObj);
    const requestOptions = { method: 'PATCH', headers: {'Content-Type':'application/json'}, body: data };
    fetch('/folder/delete', requestOptions)
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
       deleteFolderResultMessage.textContent = message;
       deleteFolderResult.style.display = "flex";
       deleteFolderForm.style.display = "none";
    }

    function processError(message) {
       deleteFolderErrorMessage.textContent = message;
       deleteFolderError.style.display = "flex";
       deleteFolderForm.style.display = "none";
    }
});
