package uk.ac.ebi.spot.ontostring.util;

import uk.ac.ebi.spot.ontostring.constants.IDPConstants;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class HeadersUtil {

    public static String extractJWT(HttpServletRequest httpServletRequest) {
        String authHeader = httpServletRequest.getHeader(IDPConstants.AUTH_HEADER);
        if (authHeader == null) {
            String jwt = httpServletRequest.getHeader(IDPConstants.JWT_TOKEN);
            if (jwt == null && httpServletRequest.getCookies() != null) {
                for (Cookie cookie : httpServletRequest.getCookies()) {
                    if (cookie.getName().equalsIgnoreCase(IDPConstants.COOKIE_ACCESSTOKEN)) {
                        jwt = cookie.getValue();
                        break;
                    }
                    if (cookie.getName().equalsIgnoreCase(IDPConstants.JWT_TOKEN)) {
                        jwt = cookie.getValue();
                        break;
                    }
                }
            }
            return jwt;
        } else {
            String[] parts = authHeader.split(" ");
            if (parts.length == 2) {
                if (parts[0].equalsIgnoreCase(IDPConstants.AUTH_BEARER)) {
                    if ((parts[1] == null) || parts[1].equalsIgnoreCase("null")) {
                        return null;
                    }

                    return parts[1];
                }
            }
        }

        return null;
    }
}
