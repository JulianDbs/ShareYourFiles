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
const showFileArticle = document.getElementById("show-file-article");
const showFileForm = document.getElementById("show-file-article-form");
const showFileResult = document.getElementById("show-file-article-result");
const showFileResultMessage = document.getElementById("show-file-article-result-message");
const showFileError = document.getElementById("show-file-article-error");
const showFileErrorMessage = document.getElementById("show-file-article-error-message");

function showFileButtonPressed() {
    showFileResult.style.display = "none";
    showFileResultMessage.innerText = "";
    showFileArticle.classList.toggle("show-article");
}

function showFileContinueButtonPressed() {
    showFileButtonPressed();
    document.getElementById("file-refresh-form").submit();
}

function showFileErrorButtonPressed() {
    showFileError.style.display = "none";
    showFileErrorMessage.textContent = "";
    showFileForm.style.display = "flex";
}

showFileForm.addEventListener('submit', event => {
    event.preventDefault();
    const formData = new FormData(showFileForm);
    const formDataObj = {};
    formData.forEach( (value, key) => (formDataObj[key] = value) );
    const data = JSON.stringify(formDataObj);
    const requestOptions = { method: 'PATCH', headers: {'Content-Type':'application/json'}, body: data};
    fetch('/file/show-file', requestOptions)
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
       showFileResultMessage.textContent = message;
       showFileResult.style.display = "flex";
       showFileForm.style.display = "none";
    }

    function processError(message) {
       showFileErrorMessage.textContent = message;
       showFileError.style.display = "flex";
       showFileForm.style.display = "none";
    }
});
