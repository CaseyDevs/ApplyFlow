package com.casey.applyflow.utils;

import org.springframework.stereotype.Component;

@Component
public class EmailValidationProvider {
    
    public boolean validateEmail(String email) {
        if (email == null || email.isBlank()) { 
            return false;
        }

        if (!email.equals(email.trim())) { // check for white space at beginning and end of email
            return false;
        }

        // keep track of @'s
        int atCount = 0;
        for (int i = 0; i < email.length(); i++) { 
            char currChar = email.charAt(i);

            if (Character.isWhitespace(currChar)) {
                return false;
            }

            if (currChar == '@') {
                atCount++;
            }
        }

        if (atCount != 1) {
            return false;
        }

        int atIndex = email.indexOf('@');
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex + 1);

        if (localPart.isEmpty() || domainPart.isEmpty()) {
            return false;
        }

        if (localPart.startsWith(".") || localPart.endsWith(".") || localPart.contains("..")) {
            return false;
        }

        if (domainPart.startsWith(".") || domainPart.endsWith(".") || domainPart.contains("..")) {
            return false;
        }

        if (!domainPart.contains(".")) {
            return false;
        }

        // check for illegal chracters in first half of email
        for (int i = 0; i < localPart.length(); i++) {
            char c = localPart.charAt(i);
            boolean isAllowedSpecial = "!#$%&'*+/=?^_`{|}~.-".indexOf(c) >= 0;
            if (!Character.isLetterOrDigit(c) && !isAllowedSpecial) {
                return false;
            }
        }

        // validate label O(n * n)  => TODO: Improve efficiency later ?
        String[] labels = domainPart.split("\\.");
        for (String label : labels) {
            if (label.isEmpty() || label.startsWith("-") || label.endsWith("-")) {
                return false;
            }

            for (int i = 0; i < label.length(); i++) {
                char c = label.charAt(i);
                if (!Character.isLetterOrDigit(c) && c != '-') {
                    return false;
                }
            }
        }

        return true;
    }
}
