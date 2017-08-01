package com.github.dozzatq.phoenix.json;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Iterator;

/**
 * Created by Rodion Bartoshyk on 05.07.2017.
 */

class JSONArrayIterator implements Iterator<DeltaSnapshot> {

    private JSONArray jsonArray;
    private int length;
    private int position;

    JSONArrayIterator(JSONArray jsonArray, int length) {
        this.jsonArray = jsonArray;
        this.length = length;
        position=0;
    }

    @Override
    public boolean hasNext() {
        return position<length;
    }

    @Override
    public DeltaSnapshot next() {
        try {
            return new DeltaSnapshot(String.valueOf(position),jsonArray.get(position));
        } catch (JSONException e) {
            return new DeltaSnapshot("", "");
        }
    }
}
