package com.conaxgames.libraries.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;

public class ValidationUtil {

    public static boolean isAlphanumeric(String string) {
        return StringUtils.isAlphanumeric(string);
    }

}
