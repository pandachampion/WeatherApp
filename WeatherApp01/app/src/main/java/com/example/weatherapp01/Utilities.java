package com.example.weatherapp01;

// Helper class to hold utilities and constants
public class Utilities {
    public static boolean validateInput(String state, String zipCode) {
        int zip = Integer.parseInt(zipCode);
        if (((zip >= 1001 && zip <= 2791) || (zip >= 5501 && zip <= 5544)) && state != "MA") {
            return false;
        } else if (zip >= 2801 && zip <= 2940 && state != "RI") {
            return false;
        } else if (zip >= 3031 && zip <= 3897 && state != "NH") {
            return false;
        } else if (zip >= 3901 && zip <= 4992 && state != "ME") {
            return false;
        } else if (((zip >= 5001 && zip <= 5495) || (zip >= 5601 && zip <= 5907))
                && state != "VT") {
            return false;
        } else if (((zip >= 6001 && zip <= 6389) || (zip >= 6401 && zip <= 6928))
                && state != "CT") {
            return false;
        } else if (((zip >= 6390 && zip <= 6390) || (zip >= 10001 && zip <= 14975))
                && state != "NY") {
            return false;
        } else if (zip >= 7001 && zip <= 8989 && state != "NJ") {
            return false;
        } else if (zip >= 15001 && zip <= 19640 && state != "PA") {
            return false;
        } else if (zip >= 19701 && zip <= 19980 && state != "DE") {
            return false;
        } else if (((zip >= 20001 && zip <= 20039) || (zip >= 20042 && zip <= 20599)
                || (zip >= 20799 && zip <= 20799) || (zip >= 56901 && zip <= 56999))
                && state != "DC") {
            return false;
        } else if (((zip >= 20040 && zip <= 20167) || (zip >= 22001 && zip <= 24658)
                && state != "VA")) {
            return false;
        } else if (((zip >= 20335 && zip <= 20797) || (zip >= 20810 && zip <= 21930)
                || (zip >= 20331 && zip <= 20331)) && state != "MD") {
            return false;
        } else if (zip >= 24701 && zip <= 26886 && state != "WV") {
            return false;
        } else if (zip >= 27006 && zip <= 28909 && state != "NC") {
            return false;
        } else if (zip >= 29001 && zip <= 29948 && state != "SC") {
            return false;
        } else if (((zip >= 30001 && zip <= 31999) || (zip >= 39813 && zip <= 39897))
                && state != "GA") {
            return false;
        } else if (zip >= 32000 && zip <= 34997 && state != "FL") {
            return false;
        } else if (zip >= 35004 && zip <= 36925 && state != "AL") {
            return false;
        } else if (zip >= 37010 && zip <= 38589 && state != "TN") {
            return false;
        } else if (((zip >= 38601 && zip <= 39776) || (zip >= 71233 && zip <= 71233))
                && state != "MS") {
            return false;
        } else if (zip >= 40003 && zip <= 42788 && state != "KY") {
            return false;
        } else if (zip >= 43001 && zip <= 45999 && state != "OH") {
            return false;
        } else if (zip >= 46001 && zip <= 47997 && state != "IN") {
            return false;
        } else if (zip >= 48001 && zip <= 49971 && state != "MI") {
            return false;
        } else if (((zip >= 50001 && zip <= 52809) ||
                (zip >= 68119 && zip <= 68120))
                && state != "IA") {
            return false;
        } else if (zip >= 53001 && zip <= 54990 && state != "WI") {
            return false;
        } else if (zip >= 55001 && zip <= 56763 && state != "MN") {
            return false;
        } else if (zip >= 57001 && zip <= 57799 && state != "SD") {
            return false;
        } else if (zip >= 58001 && zip <= 58856 && state != "ND") {
            return false;
        } else if (zip >= 59001 && zip <= 59937 && state != "MT") {
            return false;
        } else if (zip >= 60001 && zip <= 62999 && state != "IL") {
            return false;
        } else if (zip >= 63001 && zip <= 65899 && state != "MO") {
            return false;
        } else if (zip >= 66002 && zip <= 67954 && state != "KS") {
            return false;
        } else if (((zip >= 68001 && zip <= 68118) || (zip >= 68122 && zip <= 69367))
                && state != "NE") {
            return false;
        } else if (zip >= 68122 && zip <= 69367 && state != "NE") {
            return false;
        } else if (((zip >= 70001 && zip <= 71232) || (zip >= 71234 && zip <= 71497))
                && state != "LA") {
            return false;
        } else if (((zip >= 71601 && zip <= 72959) || (zip >= 75502 && zip <= 75502))
                && state != "AR") {
            return false;
        } else if (((zip >= 73001 && zip <= 73199) || (zip >= 73401 && zip <= 74966))
                && state != "OK") {
            return false;
        } else if (((zip >= 73301 && zip <= 73399) || (zip >= 88510 && zip <= 88595)
                || (zip >= 75001 && zip <= 75501) || (zip >= 75503 && zip <= 79999))
                && state != "TX") {
            return false;
        } else if (zip >= 80001 && zip <= 81658 && state != "CO") {
            return false;
        } else if (zip >= 82001 && zip <= 83128 && state != "WY") {
            return false;
        } else if (zip >= 83201 && zip <= 83876 && state != "ID") {
            return false;
        } else if (zip >= 84001 && zip <= 84790 && state != "UT") {
            return false;
        } else if (zip >= 85001 && zip <= 86556 && state != "AZ") {
            return false;
        } else if (zip >= 87001 && zip <= 88441 && state != "NM") {
            return false;
        } else if (zip >= 88901 && zip <= 89883 && state != "NV") {
            return false;
        } else if (zip >= 90001 && zip <= 96162 && state != "CA") {
            return false;
        } else if (zip >= 96701 && zip <= 96898 && state != "HI") {
            return false;
        } else if (zip >= 97001 && zip <= 97920 && state != "OR") {
            return false;
        } else if (zip >= 98001 && zip <= 99403 && state != "WA") {
            return false;
        } else if (zip >= 99501 && zip <= 99950 && state != "AK") {
            return false;
        } else {
            return true;
        }
    }
}
