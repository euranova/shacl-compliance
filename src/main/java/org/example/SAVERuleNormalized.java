package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Normalized version of the SAVE rule
 */
public class SAVERuleNormalized extends SAVERule {

    /**
     * List of prefixed attribute names in the same order as they go in values
     */
    private List<String> attributes;

    /**
     * List of attribute values in the same order as attribute names
     */
    private List<List<String>> values;

    /**
     * List of combinations of attributes and values (basically, light version of atomic sub-rules)
     */
    private List<List<String>> combinations;

    public List<List<String>> getValues() {
        return values;
    }

    public void setValues(List<List<String>> values) {
        this.values = values;
    }

    public void addValues(List<String> values) {
        this.values.add(values);
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(String attribute) {
        attributes.add(attribute);
    }

    public List<List<String>> getCombinations() {
        return combinations;
    }

    public void setCombinations(List<List<String>> combinations) {
        this.combinations = combinations;
    }

    /**
     * Creates the normalized instance based on original rule
     * @param parentRule original rule
     */
    public SAVERuleNormalized(SAVERule parentRule) {
        super(parentRule);
        attributes = new ArrayList<>();
        values = new ArrayList<>();
    }
}
