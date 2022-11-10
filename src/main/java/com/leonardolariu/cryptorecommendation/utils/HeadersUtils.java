package com.leonardolariu.cryptorecommendation.utils;

import com.leonardolariu.cryptorecommendation.config.ApiConfig;
import com.leonardolariu.cryptorecommendation.exception.ErrorCode;
import com.leonardolariu.cryptorecommendation.exception.RequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.leonardolariu.cryptorecommendation.utils.Constants.X_FORWARDED_FOR;

@Component
public final class HeadersUtils {

    private static List<String> blacklistedIps;

    @Autowired
    public HeadersUtils(ApiConfig apiConfig) {
        blacklistedIps = apiConfig.getBlacklistedIps();
    }

    public static void validate(Map<String, String> headers) {
        if (!headers.containsKey(X_FORWARDED_FOR)) {
            throw new RequestException("The x-forwarded-for header is missing", ErrorCode.MISSING_REQUEST_HEADER_ERROR);
        }

        if (blacklistedIps.contains(headers.get(X_FORWARDED_FOR))) {
            throw new RequestException("The Client IP is blacklisted", ErrorCode.INVALID_REQUEST_HEADER_ERROR);
        }
    }
}
