package org.tiagop.lagit.util;

import org.apache.commons.validator.routines.UrlValidator;

public final class Validation {
    private static final UrlValidator urlValidator = new UrlValidator();

    private Validation() {}

    public static boolean isValidUrl(final String url) {
        return urlValidator.isValid(url);
    }
}
