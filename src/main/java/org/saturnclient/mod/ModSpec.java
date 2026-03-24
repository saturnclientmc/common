package org.saturnclient.mod;

import java.util.function.Supplier;

public class ModSpec {
    public String name;
    public String namespace;
    public String description;
    public String version;
    public String[] tags = {};
    public Supplier<?>[] requiresFeature = {};

    public ModSpec(String name, String namespace) {
        this.name = name;
        this.namespace = namespace;
    }

    public ModSpec name(String name) {
        this.name = name;
        return this;
    }

    public ModSpec namespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public ModSpec description(String description) {
        this.description = description;
        return this;
    }

    public ModSpec version(String version) {
        this.version = version;
        return this;
    }

    public ModSpec tags(String... tags) {
        this.tags = tags;
        return this;
    }

    public ModSpec requires(Supplier<?>... suppliers) {
        this.requiresFeature = suppliers;
        return this;
    }
}
