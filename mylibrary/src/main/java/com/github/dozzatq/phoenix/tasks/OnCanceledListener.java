package com.github.dozzatq.phoenix.tasks;

/**
 * Created by Rodion Bartoshik on 10.06.2017.
 */

public interface OnCanceledListener<PProgress> {
    public void OnCancel(PProgress pProgress);
}
