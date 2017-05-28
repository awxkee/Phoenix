package com.github.dozzatq.phoenix.Tasks;

interface OnExtensionListener<PResult> {
        void OnExtension(Task<PResult> pResult);
}