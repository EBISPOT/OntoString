package uk.ac.ebi.spot.ontotools.curation.util;

import uk.ac.ebi.spot.ontotools.curation.constants.IDPConstants;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

public class HeadersUtil {

    public static String extractJWT(HttpServletRequest httpServletRequest) {
        String authHeader = httpServletRequest.getHeader(IDPConstants.AUTH_HEADER);
        if (authHeader == null) {
            String jwt = httpServletRequest.getHeader(IDPConstants.JWT_TOKEN);
            if (jwt == null && httpServletRequest.getCookies() != null) {
                jwt = Arrays.stream(httpServletRequest.getCookies())
                        .filter(cookie -> IDPConstants.COOKIE_ACCESSTOKEN.equalsIgnoreCase(cookie.getName())
                                || IDPConstants.JWT_TOKEN.equalsIgnoreCase(cookie.getName())).findFirst()
                        .map(Cookie::getValue).orElse(null);
            }
            return jwt;
        }
        String[] parts = authHeader.split(" ");
        if (parts.length == 2 && parts[0].equalsIgnoreCase(IDPConstants.AUTH_BEARER)) {
            if (parts[1] == null || parts[1].equalsIgnoreCase("null")) {
                return null;
            }

            return parts[1];
        }

        return null;
    }
}
