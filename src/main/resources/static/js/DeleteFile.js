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
const deleteFileArticle = document.getElementById("delete-file-article");
const deleteFileForm = document.getElementById("delete-file-article-form");
const deleteFileResult = document.getElementById("delete-file-article-result");
const deleteFileResultMessage = document.getElementById("delete-file-article-result-message");
const deleteFileError = document.getElementById("delete-file-article-error");
const deleteFileErrorMessage = document.getElementById("delete-file-article-error-message");

function deleteFileButtonPressed() {
    deleteFileResult.style.display = "none";
    deleteFileResultMessage.innerText = "";
    deleteFileArticle.classList.toggle("show-article");
}

function deleteFileContinueButtonPressed() {
    deleteFileButtonPressed();
    document.getElementById("file-go-back-form").submit();
}

function deleteFileErrorButtonPressed() {
    deleteFileError.style.display = "none";
    deleteFileErrorMessage.textContent = "";
    deleteFileForm.style.display = "flex";
}

deleteFileForm.addEventListener('submit', event => {
    event.preventDefault();
    const formData = new FormData(deleteFileForm);
    const formDataObj = {};
    formData.forEach((value, key) => (formDataObj[key] = value));
    const data = JSON.stringify(formDataObj);
    const requestOptions = { method: 'PATCH', headers: {'Content-Type':'application/json'}, body: data };
    fetch('/file/delete', requestOptions)
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
       deleteFileResultMessage.textContent = message;
       deleteFileResult.style.display = "flex";
       deleteFileForm.style.display = "none";
    }

    function processError(message) {
       deleteFileErrorMessage.textContent = message;
       deleteFileError.style.display = "flex";
       deleteFileForm.style.display = "none";
    }
});
