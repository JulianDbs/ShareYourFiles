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
const hideFolderArticle = document.getElementById("hide-folder-article");
const hideFolderForm = document.getElementById("hide-folder-article-form");
const hideFolderResult = document.getElementById("hide-folder-article-result");
const hideFolderResultMessage = document.getElementById("hide-folder-article-result-message");
const hideFolderError = document.getElementById("hide-folder-article-error");
const hideFolderErrorMessage = document.getElementById("hide-folder-article-error-message");

function hideFolderButtonPressed() {
    hideFolderResult.style.display = "none";
    hideFolderResultMessage.innerText = "";
    hideFolderArticle.classList.toggle("show-article");
}

function hideFolderContinueButtonPressed() {
    hideFolderButtonPressed();
    let refreshForm = document.getElementById("folder-refresh-form");
    if (refreshForm == null) {
        refreshForm = document.getElementById("folder-go-back-form");
    }
    refreshForm.submit();
}

function hideFolderErrorButtonPressed() {
    hideFolderError.style.display = "none";
    hideFolderErrorMessage.textContent = "";
    hideFolderForm.style.display = "flex";
}

hideFolderForm.addEventListener('submit', event => {
    event.preventDefault();
    const formData = new FormData(hideFolderForm);
    const formDataObj = {};
    formData.forEach( (value, key) => (formDataObj[key] = value) );
    const data = JSON.stringify(formDataObj);
    const requestOptions = { method: 'PATCH', headers: {'Content-Type':'application/json'}, body: data};
    fetch('/folder/hide-folder', requestOptions)
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
       hideFolderResultMessage.textContent = message;
       hideFolderResult.style.display = "flex";
       hideFolderForm.style.display = "none";
    }

    function processError(message) {
       hideFolderErrorMessage.textContent = message;
       hideFolderError.style.display = "flex";
       hideFolderForm.style.display = "none";
    }
});
