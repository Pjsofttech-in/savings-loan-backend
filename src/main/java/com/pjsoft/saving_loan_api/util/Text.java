package com.pjsoft.saving_loan_api.util;

import java.util.Map;

public final class Text {

    private Text() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /** Trims and cuts a value to the column length so MySQL strict mode never rejects it. */
    public static String clip(String value, int max) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.length() > max ? trimmed.substring(0, max) : trimmed;
    }

    /** First non-blank value among the given parameter names (supports alternative field names). */
    public static String pick(Map<String, ?> params, String... names) {
        if (params == null) {
            return null;
        }
        for (String name : names) {
            Object raw = params.get(name);
            if (raw != null) {
                String value = String.valueOf(raw).trim();
                if (!value.isEmpty() && !"null".equalsIgnoreCase(value) && !"undefined".equalsIgnoreCase(value)) {
                    return value;
                }
            }
        }
        return null;
    }

    public static boolean has(Map<String, ?> params, String... names) {
        return params != null && java.util.Arrays.stream(names).anyMatch(params::containsKey);
    }

    /** Builds "street, taluka, district, state - pincode" from whichever parts are present. */
    public static String composeAddress(Map<String, ?> params) {
        String street = pick(params, "street", "address", "residentialAddress");
        StringBuilder out = new StringBuilder();
        for (String part : new String[]{street, pick(params, "taluka"), pick(params, "district"), pick(params, "state")}) {
            if (!isBlank(part)) {
                if (out.length() > 0) {
                    out.append(", ");
                }
                out.append(part.trim());
            }
        }
        String pincode = pick(params, "pincode", "pinCode");
        if (!isBlank(pincode)) {
            out.append(out.length() > 0 ? " - " : "").append(pincode.trim());
        }
        return out.length() == 0 ? null : out.toString();
    }

    /** "MEM-12" -> 12, "12" -> 12, otherwise null. */
    public static Long parseId(String value) {
        if (value == null) {
            return null;
        }
        String digits = value.replaceAll("[^0-9]", "");
        if (digits.isEmpty() || digits.length() > 18) {
            return null;
        }
        return Long.parseLong(digits);
    }
}
