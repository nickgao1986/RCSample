package com.example.nickgao.contacts.adapters.contactsprovider;

/**
 * Created by nick.gao on 1/28/17.
 */

public class ContactsSorting {

    public static int sorting(String str1, String str2) {
        if(str1 == null || str1.isEmpty()) {
            return -1;
        }

        if(str2 == null || str2.isEmpty()) {
            return 1;
        }

        if(str1.equals(str2)) {
            return 0;
        }

        return compareStrings(str1, str2, 0, false);
    }

    public static int sorting(String str1, String str2, boolean toLowerCase) {
        if(str1 == null || str1.isEmpty()) {
            return -1;
        }

        if(str2 == null || str2.isEmpty()) {
            return 1;
        }

        return compareStrings(str1, str2, 0, toLowerCase);
    }

    private static boolean isLatinLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    private static int compareStrings(String str1, String str2, int ind, boolean toLowerCase){
        try {
            char char1 = str1.charAt(ind);
            char char2 = str2.charAt(ind);
            if (toLowerCase) {
                char1 = Character.toLowerCase(char1);
                char2 = Character.toLowerCase(char2);
            }

            if (char1 == char2) {
                ind++;
                //AB-19734 The searching list order is incorrect in Text composing screen when the contacts' name are the same but case sensitive.
                if (str1.length() == str2.length()
                        && ind == str1.length()) {
                    return 0;
                }

                if (str1.length() == ind) {
                    return -1;
                }
                if (str2.length() == ind) {
                    return 1;
                }
                return compareStrings(str1, str2, ind, toLowerCase);
            }

            if (' ' == char1) {
                return -1;
            }

            if (' ' == char2) {
                return 1;
            }

            boolean thisFirstCharIsLatin = isLatinLetter(char1);
            boolean contactFirstCharIsLatin = isLatinLetter(char2);

            if (thisFirstCharIsLatin && !contactFirstCharIsLatin) {
                return -1;
            }
            if (!thisFirstCharIsLatin && contactFirstCharIsLatin) {
                return 1;
            }

            boolean thisFirstCharIsLetter = Character.isLetter(char1);
            boolean contactFirstCharIsLetter = Character.isLetter(char2);

            if (thisFirstCharIsLetter && !contactFirstCharIsLetter) {
                return 1;
            }
            if (!thisFirstCharIsLetter && contactFirstCharIsLetter) {
                return -1;
            }

            boolean thisFirstCharIsNumber = Character.isDigit(char1);
            boolean contactFirstCharIsNumber = Character.isDigit(char2);

            if (thisFirstCharIsNumber && !contactFirstCharIsNumber) {
                return 1;
            }

            if (!thisFirstCharIsNumber && contactFirstCharIsNumber) {
                return -1;
            }

            return (char1 - char2);
        }catch (Throwable th) {
            return 0;
        }
    }
}
