package com.github.dozzatq.phoenix.tasks;

interface OnExtensionListener<PResult> {
        void OnExtension(Task<PResult> pResult);
}