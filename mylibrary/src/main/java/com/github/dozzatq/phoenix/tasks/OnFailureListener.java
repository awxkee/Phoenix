package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

public interface OnFailureListener {
        void OnFailure(@NonNull Exception exception);
    }