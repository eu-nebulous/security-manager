/*
 * Copyright (c) 2025     Ubitech LTD.
 */

package eu.nebulouscloud.securitymanager.model.opa;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ConstraintType {
    ALLOWED_REPOS("allowed-repos"),
    DISALLOWED_REPOS("disallowed-repos"),
    DISALLOWED_TAGS("disallowed-tags"),
    REPLICA_LIMITS("replica-limits");

    private final String suffix;

    ConstraintType(String suffix) {
        this.suffix = suffix;
    }

    @JsonValue
    public String getSuffix() {
        return suffix;
    }

    @JsonCreator
    public static ConstraintType fromValue(String value) {
        for (ConstraintType type : values()) {
            if (type.name().equalsIgnoreCase(value) || type.getSuffix().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown constraint type: " + value);
    }
}
