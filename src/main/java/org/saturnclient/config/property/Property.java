package org.saturnclient.config.property;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;

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

        if (this instanceof BoolProperty p) {
            return JsonNodeFactory.instance.booleanNode(p.value);
        }

        if (this instanceof IntProperty p) {
            return JsonNodeFactory.instance.numberNode(p.value);
        }

        if (this instanceof FloatProperty p) {
            return JsonNodeFactory.instance.numberNode(p.value);
        }

        if (this instanceof StringProperty p) {
            return JsonNodeFactory.instance.textNode(p.value);
        }

        if (this instanceof ColorProperty p) {
            return JsonNodeFactory.instance.textNode(
                    String.format("#%08X", p.value));
        }

        if (this instanceof KeybindingProperty p) {
            return JsonNodeFactory.instance.numberNode(p.value);
        }

        if (this instanceof SelectProperty p) {
            return JsonNodeFactory.instance.numberNode(p.value);
        }

        if (this instanceof NamespaceProperty p) {
            ObjectNode nested = JsonNodeFactory.instance.objectNode();

            p.value.forEach((k, v) -> {
                nested.set(k, v.toJson());
            });

            return nested;
        }

        return null;
    }

    public void loadFromJson(JsonNode element) {

        if (element == null)
            return;

        if (this instanceof BoolProperty p) {
            if (element.isBoolean())
                p.value = element.booleanValue();
            return;
        }

        if (this instanceof IntProperty p) {
            if (element.isInt())
                p.value = element.intValue();
            else if (element.isTextual())
                p.value = ColorProperty.parseHexToInt(element.textValue());
            return;
        }

        if (this instanceof FloatProperty p) {
            if (element.isNumber())
                p.value = element.floatValue();
            return;
        }

        if (this instanceof StringProperty p) {
            if (element.isTextual())
                p.value = element.textValue();
            return;
        }

        if (this instanceof ColorProperty p) {
            if (element.isTextual())
                p.value = ColorProperty.parseHexToInt(element.textValue());
            return;
        }

        if (this instanceof NamespaceProperty p) {
            if (element.isObject()) {
                ObjectNode obj = (ObjectNode) element;

                p.value.forEach((k, v) -> {
                    v.loadFromJson(obj.get(k));
                });
            }
            return;
        }

        if (this instanceof SelectProperty p) {
            if (element.isInt())
                p.setSelection(element.intValue());
            return;
        }

        if (this instanceof KeybindingProperty p) {
            if (element.isInt())
                p.value = element.intValue();
        }
    }
}