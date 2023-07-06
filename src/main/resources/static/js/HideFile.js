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
const hideFileArticle = document.getElementById("hide-file-article");
const hideFileForm = document.getElementById("hide-file-article-form");
const hideFileResult = document.getElementById("hide-file-article-result");
const hideFileResultMessage = document.getElementById("hide-file-article-result-message");
const hideFileError = document.getElementById("hide-file-article-error");
const hideFileErrorMessage = document.getElementById("hide-file-article-error-message");

function hideFileButtonPressed() {
    hideFileResult.style.display = "none";
    hideFileResultMessage.innerText = "";
    hideFileArticle.classList.toggle("show-article");
}

function hideFileContinueButtonPressed() {
    hideFileButtonPressed();
    document.getElementById("file-refresh-form").submit();
}

function hideFileErrorButtonPressed() {
    hideFileError.style.display = "none";
    hideFileErrorMessage.textContent = "";
    hideFileForm.style.display = "flex";
}

hideFileForm.addEventListener('submit', event => {
    event.preventDefault();
    const formData = new FormData(hideFileForm);
    const formDataObj = {};
    formData.forEach( (value, key) => (formDataObj[key] = value) );
    const data = JSON.stringify(formDataObj);
    const requestOptions = { method: 'PATCH', headers: {'Content-Type':'application/json'}, body: data};
    fetch('/file/hide-file', requestOptions)
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
       hideFileResultMessage.textContent = message;
       hideFileResult.style.display = "flex";
       hideFileForm.style.display = "none";
    }

    function processError(message) {
       hideFileErrorMessage.textContent = message;
       hideFileError.style.display = "flex";
       hideFileForm.style.display = "none";
    }
});
