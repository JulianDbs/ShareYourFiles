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
const showFolderArticle = document.getElementById("show-folder-article");
const showFolderForm = document.getElementById("show-folder-article-form");
const showFolderResult = document.getElementById("show-folder-article-result");
const showFolderResultMessage = document.getElementById("show-folder-article-result-message");
const showFolderError = document.getElementById("show-folder-article-error");
const showFolderErrorMessage = document.getElementById("show-folder-article-error-message");

function showFolderButtonPressed() {
    showFolderResult.style.display = "none";
    showFolderResultMessage.innerText = "";
    showFolderArticle.classList.toggle("show-article");
}

function showFolderContinueButtonPressed() {
    showFolderButtonPressed();
    let refreshForm = document.getElementById("folder-refresh-form");
    if (refreshForm == null) {
        refreshForm = document.getElementById("folder-go-back-form");
    }
    refreshForm.submit();
}

function showFolderErrorButtonPressed() {
    showFolderError.style.display = "none";
    showFolderErrorMessage.textContent = "";
    showFolderForm.style.display = "flex";
}

showFolderForm.addEventListener('submit', event => {
    event.preventDefault();
    const formData = new FormData(showFolderForm);
    const formDataObj = {};
    formData.forEach( (value, key) => (formDataObj[key] = value) );
    const data = JSON.stringify(formDataObj);
    const requestOptions = { method: 'PATCH', headers: {'Content-Type':'application/json'}, body: data};
    fetch('/folder/show-folder', requestOptions)
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
       showFolderResultMessage.textContent = message;
       showFolderResult.style.display = "flex";
       showFolderForm.style.display = "none";
    }

    function processError(message) {
       showFolderErrorMessage.textContent = message;
       showFolderError.style.display = "flex";
       showFolderForm.style.display = "none";
    }
});
