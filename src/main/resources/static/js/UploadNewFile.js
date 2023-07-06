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
const uploadFileArticle = document.getElementById("upload-file-article");
const uploadFileForm = document.getElementById("upload-file-article-form");
const progressBarText = document.getElementById("upload-file-article-form-progress-bar-p");
const uploadFileProcess = document.getElementById("upload-file-article-form-progress-bar");
const uploadFileProcessMessage = document.getElementById("upload-file-article-progress-message");
const progressBarPercent = document.getElementById("upload-file-article-form-progress-bar-p");
const progressBarSpan = document.getElementById("upload-file-article-form-progress-span");
const progressBarSize = document.getElementById("upload-file-article-form-size-p");
const progressBarTime = document.getElementById("upload-file-article-form-time-p");
const uploadFileResult = document.getElementById("upload-file-article-result");
const uploadFileResultMessage = document.getElementById("upload-file-article-result");
const uploadFileError = document.getElementById("upload-file-article-error");
const uploadFileErrorMessage = document.getElementById("upload-file-article-error-message");
const uploadFileArticleContinueButton = document.getElementById("upload-file-article-continue-button");

function uploadFileButtonPressed() {
    uploadFileArticle.classList.toggle("show-article");
    uploadFileArticleContinueButton.style.display = "none";
}

function uploadFileErrorButtonPressed() {
    uploadFileError.style.display = "none";
    uploadFileForm.style.display = "flex";
}

function uploadFileProgressContinueButtonPressed() {
    let refreshForm = document.getElementById("folder-refresh-form");
    if (refreshForm == null) {
        refreshForm = document.getElementById("folder-go-back-form");
    }
    refreshForm.submit();
}

function dufHavePasswordCheckBoxPressed() {
    document.getElementById("upload-file-article-form-password-div").classList.toggle("show-upload-password-div");
    document.getElementById("upload-file-article-form-match-password-div").classList.toggle("show-upload-password-div");
}

function clearTextView() {
        progressBarSize.innerText= ( ' : 0 / 0' );
        progressBarTime.innerText= ( ' : 00:00:00 MS');
        progressBarPercent.innerText = (' : %0' );
}

function formatBytes(bytes, dm = 2) {
  if (bytes == 0) return '0 Bytes';
  var k = 1024,
    sizes = [`${bytes <= 1 ? "Byte" : "Bytes"}`, 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
    i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
}

function printUploadProgress(progressEvent, startTime) {
    var progress = Math.round( (progressEvent.loaded / progressEvent.total) * 100 );
    if (progressEvent.lengthComputable) {
        progressBarSpan.style.width = (progress + '%');
        const currentTime = new Date().getTime();
        var time = new Date( (currentTime - startTime) ).toISOString().slice(11,19);
        progressBarSize.innerText= ( ' : ' + formatBytes(progressEvent.loaded));
        progressBarTime.innerText= ( ' : ' + time + ' MS');
        progressBarPercent.innerText = (' : %' + progress);
    }
}

uploadFileForm.addEventListener('submit', event => {
    event.preventDefault();
    const startTime = new Date().getTime();
    const formData = new FormData(uploadFileForm);
    uploadFileForm.style.display = "none";
    uploadFileProcess.style.display = "flex";
    var xhr = new XMLHttpRequest();
    xhr.open("PUT", "/file/add-file");
    xhr.onload = event => {
        if (xhr.status === 200) {
            var jsonResponse = JSON.parse(xhr.responseText);
            processResult(jsonResponse.message);
        }
        if (xhr.status >= 400) {
            var jsonResponse = JSON.parse(xhr.responseText);
            processError(jsonResponse.message);
        }
    };
    xhr.onerror = errorEvent => {
        processError(errorEvent.message)
    };
    xhr.upload.onprogress = progressEvent => {
        onProgress(progressEvent);
    };
    xhr.send(formData);

    const onProgress = (progressEvent) => {
        printUploadProgress(progressEvent, startTime);
    }

    function processError(message) {
        uploadFileProcess.style.display = "none";
        uploadFileError.style.display = "flex";
        uploadFileErrorMessage.textContent = message;
    }

    function processResult(message) {
        uploadFileProcessMessage.innerText = message;
        uploadFileArticleContinueButton.style.display = "flex";
    }
});
