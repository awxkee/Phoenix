package com.github.dozzatq.phoenix.json;

import com.github.dozzatq.phoenix.tasks.Task;
import com.github.dozzatq.phoenix.tasks.TaskSource;
import com.github.dozzatq.phoenix.tasks.Tasks;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Type;
import java.util.Iterator;

/**
 * Created by Rodion Bartoshyk on 02.06.2017.
 */

public class DeltaSnapshot {

    private String key;
    private Object value;
    private final Object mLock = new Object();

    private JSONTokener jsonTokener=null;

    public DeltaSnapshot(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public long getChildrenCount()
    {
        synchronized (mLock)
        {
            if (value instanceof String) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject((String) value);
                    return jsonObject.length();
                } catch (JSONException e) {
                    try {
                        JSONArray jsonArray = new JSONArray((String) value);
                        return jsonArray.length();
                    } catch (JSONException e5) {
                    }
                }

            }
            return 0;
        }
    }

    public Iterator<DeltaSnapshot> getIterator()
    {
        synchronized (mLock)
        {
            if (value instanceof String) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject((String) value);
                    final Iterator<String> keyIterator = jsonObject.keys();
                    Iterator<DeltaSnapshot> iterator = new Iterator<DeltaSnapshot>() {
                        @Override
                        public boolean hasNext() {
                            return keyIterator.hasNext();
                        }

                        @Override
                        public DeltaSnapshot next() {
                            return child(keyIterator.next());
                        }
                    };
                    return iterator;
                } catch (JSONException e) {
                    try {
                        JSONArray jsonArray = new JSONArray((String) value);
                        return new JSONArrayIterator(jsonArray, jsonArray.length());
                    } catch (JSONException e5) {
                    }
                }

            }
            return null;
        }
    }

    public DeltaSnapshot child(String childKey)
    {
        synchronized (mLock) {
            if (childKey == null)
                return new DeltaSnapshot(null, null);

            if (value instanceof String) {
                try {
                    if (jsonTokener==null)
                        jsonTokener = new JSONTokener((String) value);
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
            else if (value instanceof JSONObject)
            {
                if (((JSONObject) value).isNull(childKey))
                    return new DeltaSnapshot(childKey, null);

                Object valueObject = null;
                try {
                    valueObject = ((JSONObject) value).get(childKey);
                } catch (JSONException e) {
                    return null;
                }
                return alphaSnapshot(childKey, valueObject);
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
        synchronized (mLock) {
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

    public <X> X getValue(Type xClass)
    {
        if (value==null)
            return null;
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("MM/dd/yy HH:mm:ss");
        if (value instanceof String)
            return builder.create().fromJson((String) value, xClass);
        return null;
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
