package org.saturnclient.config.property;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;

import org.saturnclient.common.provider.Providers;

public abstract class Property {

    public enum PropertyType {
        BOOLEAN,
        INTEGER,
        FLOAT,
        STRING,
        HEX,
        NAMESPACE,
        SELECT,
        KEYBINDING,
    }

    public boolean isReset = false;

    // ---------- Factory Methods ----------

    public static NamespaceProperty namespace(Map<String, Property> value) {
        return new NamespaceProperty(value);
    }

    public static BoolProperty bool(boolean value) {
        return new BoolProperty(value);
    }

    public static IntProperty integer(int value) {
        return new IntProperty(value);
    }

    public static FloatProperty floatProp(float value) {
        return new FloatProperty(value);
    }

    public static StringProperty string(String value) {
        return new StringProperty(value);
    }

    public static ColorProperty color(int value) {
        return new ColorProperty(value);
    }

    public static KeybindingProperty keybinding(int value) {
        return new KeybindingProperty(value);
    }

    public static SelectProperty font(int value) {
        return select(value, "Default", "Inter", "Inter bold");
    }

    public static SelectProperty select(Integer value, String... availableValues) {
        return new SelectProperty(value, availableValues);
    }

    // ---------- Utility ----------

    public NamedProperty named(String name) {
        return new NamedProperty(name, this);
    }

    public void reset() {
        isReset = true;
    }

    // ---------- JSON Serialization ----------

    public JsonNode toJson() {

        switch (type) {

            case BOOLEAN:
                return JsonNodeFactory.instance.booleanNode((Boolean) value);

            case INTEGER:
                return JsonNodeFactory.instance.numberNode((Integer) value);

            case FLOAT:
                return JsonNodeFactory.instance.numberNode((Float) value);

            case STRING:
                return JsonNodeFactory.instance.textNode((String) value);

            case HEX:
                return JsonNodeFactory.instance.textNode(
                        String.format("#%08X", (Integer) value));

            case KEYBINDING:
            case SELECT:
                return JsonNodeFactory.instance.numberNode((Integer) value);

            case NAMESPACE:

                ObjectNode nested = JsonNodeFactory.instance.objectNode();

                getNamespaceValue().forEach((k, v) -> {
                    nested.set(k, v.toJson());
                });

                return nested;

            default:
                return JsonNodeFactory.instance.textNode(String.valueOf(value));
        }
    }

    public void loadFromJson(JsonNode element) {

        if (element == null)
            return;

        switch (type) {

            case BOOLEAN:
                if (element.isBoolean())
                    setValue(element.booleanValue());
                break;

            case INTEGER:
                if (element.isInt())
                    setValue(element.intValue());
                else if (element.isTextual())
                    setValue(parseHexToInt(element.textValue()));
                break;

            case FLOAT:
                if (element.isNumber())
                    setValue(element.floatValue());
                break;

            case STRING:
                if (element.isTextual())
                    setValue(element.textValue());
                break;

            case HEX:
                if (element.isTextual())
                    setValue(parseHexToInt(element.textValue()));
                break;

            case NAMESPACE:

                if (element.isObject()) {

                    ObjectNode obj = (ObjectNode) element;

                    getNamespaceValue().forEach((k, v) -> {
                        v.loadFromJson(obj.get(k));
                    });

                }

                break;

            case SELECT:
                if (element.isInt())
                    setSelection(element.intValue());
                break;

            case KEYBINDING:
                if (element.isInt())
                    setValue(element.intValue());
                break;
        }
    }
}