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
const downloadFileForm = document.getElementById("download-file-article-form");
const progressBarContainer = document.getElementById("download-file-article-form-progress-bar-div");
const progressBarPercent = document.getElementById("download-file-article-form-percent-p");
const progressBarSize = document.getElementById("download-file-article-form-size-p");
const progressBarTime = document.getElementById("download-file-article-form-time-p");
const progressBarSpan = document.getElementById("download-file-article-form-progress-span");
const continueButton = document.getElementById("download-file-article-button");
const progressStepA = document.getElementById("download-file-article-form-step-a-div");
const progressStepB = document.getElementById("download-file-article-form-step-b-div");

function downloadFileButtonPressed() {
    document.getElementById("download-file-wp-article").classList.toggle("show-article");
    downloadFileForm.style.display = "flex";
    progressBarContainer.style.display = "none";
    continueButton.style.display = 'none';
    showStepA();
    clearTextView();
}

function showStepA() {
    progressBarSpan.classList.add("indeterminate-progress-bar");
    progressStepA.style.display = "flex"
    progressStepB.style.display = "none"
}

function showStepB() {
    progressBarSpan.classList.remove("indeterminate-progress-bar");
    progressStepA.style.display = "none"
    progressStepB.style.display = "flex"
}

function closeDownloadFileWindow() {
    document.getElementById("download-file-wp-article").classList.toggle("show-article");
    downloadFileForm.style.display = "flex";
    progressBarContainer.style.display = "none";
    showStepA();
    continueButton.style.display = 'none';
    clearTextView();
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

function printDownloadProgress(progressEvent, startTime) {
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

downloadFileForm.addEventListener('submit', event => {
    event.preventDefault();
    const startTime = new Date().getTime();
    const formData = new FormData(downloadFileForm);
    downloadFileForm.style.display = "none";
    progressBarContainer.style.display = "flex";
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/file/get-file");
    xhr.responseType = 'arraybuffer';
    xhr.onload = event => {
        const currentTime = new Date().getTime();
        var time = new Date( (currentTime - startTime) ).toISOString().slice(11,19);
        console.log( '-> ' + time + " | on load - start");

        if (xhr.readyState == 4 && xhr.status === 200) {
            var contentDisposition = xhr.getResponseHeader('Content-Disposition');
            var contentType = xhr.getResponseHeader('Content-Type');
            var contentLength = xhr.getResponseHeader('Content-Length');
            onStatus200(event, xhr.response, contentDisposition, contentType. contentLength);
        }
        if (xhr.status >= 400) { onStatus4xx(event); }
    };
    xhr.onerror = errorEvent => {
        onError(errorEvent);
    };
    xhr.onprogress = progressEvent => {
        const currentTime = new Date().getTime();
        var time = new Date( (currentTime - startTime) ).toISOString().slice(11,19);
        console.log("-> " + time );

        onProgress(progressEvent);
    };

    xhr.onreadystatechange = event => {
        if (xhr.readyState == XMLHttpRequest.HEADERS_RECEIVED) {
            const currentTime = new Date().getTime();
            const time = new Date( (currentTime - startTime) ).toISOString().slice(11,19);
            showStepB();
        }
    };

    xhr.onloadstart = progressEvent => {
        const currentTime = new Date().getTime();
        var time = new Date( (currentTime - startTime) ).toISOString().slice(11,19);
    };

    xhr.onloadend = progressEvent => {
        const currentTime = new Date().getTime();
        var time = new Date( (currentTime - startTime) ).toISOString().slice(11,19);
    };

    xhr.send(formData);

    const onStatus200 = (event, response, contentDisposition, contentType, contentLength) => {
        continueButton.style.display = 'flex';
        var filename = "";
        if (contentDisposition && contentDisposition.indexOf('attachment') !== -1) {
            var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
            var matches = filenameRegex.exec(contentDisposition);
            if (matches != null && matches[1]) filename = matches[1].replace(/['"]/g, '');
        }
        var contentLength = xhr.getResponseHeader('Content-Length');
        const currentTime = new Date().getTime();
        var blob = new Blob([response], { type: contentType });
        const time = new Date( (currentTime - startTime) ).toISOString().slice(11,19);
        if (typeof window.navigator.msSaveBlob !== 'undefined') {
            window.navigator.msSaveBlob(blob, filename);
        } else {
            var URL = window.URL || window.webkitURL;
            var downloadUrl = URL.createObjectURL(blob);
            if (filename) {
                var a = document.createElement("a");
                a.style.display = "none";
                if (typeof a.download === 'undefined') {
                    window.location = downloadUrl;
                } else {
                    a.href = downloadUrl;
                    a.download = filename;
                    document.body.appendChild(a);
                    a.click();
                    a.remove();
                }
            } else {
                window.location = downloadUrl;
            }
            setTimeout(function () { URL.revokeObjectURL(downloadUrl); }, 100); // cleanup

        }
    }
    const onStatus4xx = (progressEvent) => {
        console.log("http client error");
        console.log("status : " + xhr.status);
        console.log("status text : " + xhr.statusText);
    }

    const onProgress = (progressEvent) => {
        printDownloadProgress(progressEvent, startTime);
    }

    const onerror = errorEvent => {
        const errorMessageElement = document.getElementById("download-file-article-form-error-message");
        errorMessageElement.textContent = errorEvent.message;
        errorMessageElement.style.display = "flex";
        downloadFileForm.style.display = "flex";
        progressBarContainer.style.display = "none";
    }
});
