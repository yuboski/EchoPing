package com.factory.echoping;

import java.util.Collection;

/**
 * Created by yuboski on 12/07/17.
 */
public class EchoInfo {
    private String key;
    private Collection<String> values;

    public EchoInfo() {

    }
    public EchoInfo(String key, Collection<String> values) {
        this.key = key;
        this.values = values;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Collection<String> getValues() {
        return values;
    }

    public void setValue(Collection<String> values) {
        this.values = values;
    }
}
