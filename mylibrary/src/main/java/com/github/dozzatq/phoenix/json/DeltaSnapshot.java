package com.github.dozzatq.phoenix.json;

import com.github.dozzatq.phoenix.Tasks.Task;
import com.github.dozzatq.phoenix.Tasks.TaskSource;
import com.github.dozzatq.phoenix.Tasks.Tasks;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by dxfb on 02.06.2017.
 */

public class DeltaSnapshot {

    private String key;
    private Object value;
    private final Object waitObject = new Object();

    public DeltaSnapshot(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public DeltaSnapshot child(String childKey)
    {
        synchronized (waitObject) {
            if (childKey == null)
                return new DeltaSnapshot(null, null);

            if (value instanceof String) {
                JSONTokener jsonTokener;
                jsonTokener = new JSONTokener((String) value);
                try {
                    Object objectChild = jsonTokener.nextValue();
                    if (objectChild instanceof JSONArray) {
                        Object object = ((JSONArray) objectChild).get(Integer.parseInt(childKey));
                        return alphaSnapshot(childKey, object);

                    } else if (objectChild instanceof JSONObject) {
                        if (((JSONObject) objectChild).isNull(childKey))
                            return new DeltaSnapshot(childKey, null);

                        Object valueObject = ((JSONObject) objectChild).get(childKey);
                        return alphaSnapshot(childKey, valueObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return new DeltaSnapshot(childKey, null);
        }
    }

    public Task<DeltaSnapshot> awayChild(final String childKey)
    {
        TaskSource<DeltaSnapshot> proceedChildTask = new TaskSource<DeltaSnapshot>() {

            private String childedKey = childKey;

            @Override
            public DeltaSnapshot call() throws Exception {
                return child(childedKey);
            }
        };
        return Tasks.execute(proceedChildTask);
    }

    private DeltaSnapshot alphaSnapshot(String childKey, Object valueObject)
    {
        if (valueObject instanceof Boolean) {
            return new DeltaSnapshot(childKey,valueObject);
        } else if (valueObject instanceof String) {
            return new DeltaSnapshot(childKey, valueObject);
        } else if (valueObject instanceof Long) {
            return new DeltaSnapshot(childKey, valueObject);
        } else if (valueObject instanceof Double) {
            return new DeltaSnapshot(childKey, valueObject);
        } else if (valueObject instanceof Integer){
            return new DeltaSnapshot(childKey, valueObject);
        } else if (valueObject instanceof JSONObject){
            return new DeltaSnapshot(childKey, ((JSONObject)valueObject).toString());
        } else if (valueObject instanceof JSONArray){
            return new DeltaSnapshot(childKey, ((JSONArray)valueObject).toString());
        } else
            return new DeltaSnapshot(childKey, valueObject);
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        synchronized (waitObject) {
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else if (value instanceof String) {
                return (String) value;
            } else if (value instanceof Long) {
                return (Long) value;
            } else if (value instanceof Double) {
                return (Double) value;
            } else if (value instanceof Integer) {
                return (Integer) value;
            } else
                return value;
        }
    }

    public <X> X getValue(Class<X> xClass)
    {
        if (value==null)
            return null;
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("MM/dd/yy HH:mm:ss");
      //  Type collectionType = new TypeToken <X>(){}.getType();
        if (value instanceof String)
            return builder.create().fromJson((String) value, xClass);
        else
            return tryCastValue(xClass);
    }


    public String toString() {
        return (new StringBuilder(33 + String.valueOf(getKey()).length() + String.valueOf(getValue()).length())).append("DeltaSnapshot { key = ").append(getKey())
                .append(", value = ").append(getValue()).append(" }").toString();
    }

    private <X> X tryCastValue(Class<X> xClass)
    {
        try {
            return (X) value;
        }
        catch (ClassCastException e)
        {
            return null;
        }
    }
}
