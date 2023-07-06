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
const deleteAccountArticle = document.getElementById("delete-account-article");
const deleteAccountForm = document.getElementById("delete-account-article-form");
const deleteAccountResult = document.getElementById("delete-account-article-result");
const deleteAccountResultMessage = document.getElementById("delete-account-article-result-message");
const deleteAccountError = document.getElementById("delete-account-article-error");
const deleteAccountErrorMessage = document.getElementById("delete-account-article-error-message");

function deleteAccountButtonPressed() {
    deleteAccountResult.style.display = "none";
    deleteAccountResultMessage.innerText = "";
    deleteAccountArticle.classList.toggle("show-article");
}

function deleteAccountContinueButtonPressed() {
    deleteAccountButtonPressed();
    document.getElementById("account-refresh-button").click();
}

function deleteAccountErrorButtonPressed() {
    deleteAccountError.style.display = "none";
    deleteAccountErrorMessage.textContext = "";
    deleteAccountForm.style.display = "flex";
}

deleteAccountForm.addEventListener('submit', event => {
    event.preventDefault();
    const formData = new FormData(deleteAccountForm);
    const formDataObj = {};
    formData.forEach((value, key) => (formDataObj[key] = value));
    const data = JSON.stringify(formDataObj);
    const requestOptions = { method: 'PATCH', headers: {'Content-Type':'application/json'}, body: data };
    fetch('/user/delete-account', requestOptions)
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
           deleteAccountResultMessage.textContent = message;
           deleteAccountResult.style.display = "flex";
           deleteAccountForm.style.display = "none";
        }
    
        function processError(message) {
           deleteAccountErrorMessage.textContent = message;
           deleteAccountError.style.display = "flex";
           deleteAccountForm.style.display = "none";
        }
});
