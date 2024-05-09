package ru.numbdev.interviewer.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class SecurityUtil {

    private static final String ANONIM_NAME = "anonymousUser";

    public static String getUserName() {
        var auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return auth instanceof String
                ? (String) auth
                : ((User) auth).getUsername();
    }

    public static boolean isAnonymous() {
        return ANONIM_NAME.equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    public static boolean isHr() {
        var auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return false;
    }
}
