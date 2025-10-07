package com.example.securityapp.validation;

public class ValidationConstants {

    // User validation patterns
    public static final String NAME_PATTERN = "^[a-zA-Z0-9À-ž\\s\\-'@]+$";
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String ORGANIZATION_PATTERN = "^[a-zA-Z0-9\\s\\-\\.&]+$";

    // Certificate X500 name patterns
    public static final String CN_PATTERN = "^[a-zA-Z0-9\\s\\.\\-_]+$";
    public static final String O_PATTERN = "^[a-zA-Z0-9\\s\\.\\-_&]+$";
    public static final String OU_PATTERN = "^[a-zA-Z0-9\\s\\.\\-_]+$";
    public static final String COUNTRY_PATTERN = "^[A-Z]{2}$";

    // File extensions
    public static final String[] CSR_EXTENSIONS = {".csr", ".pem"};
    public static final String[] KEY_EXTENSIONS = {".pem", ".key"};

    // Size limits
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 50;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 128;
    public static final int MIN_ORGANIZATION_LENGTH = 2;
    public static final int MAX_ORGANIZATION_LENGTH = 100;

    public static final int MIN_CN_LENGTH = 2;
    public static final int MAX_CN_LENGTH = 64;
    public static final int MIN_O_LENGTH = 2;
    public static final int MAX_O_LENGTH = 64;
    public static final int MIN_OU_LENGTH = 2;
    public static final int MAX_OU_LENGTH = 64;
    public static final int COUNTRY_LENGTH = 2;

    public static final int MIN_DURATION_DAYS = 1;
    public static final int MAX_ROOT_DURATION_DAYS = 7300; // 20 years
    public static final int MAX_DURATION_DAYS = 3650; // 10 years default

    public static final long MAX_FILE_SIZE = 1024 * 1024; // 1MB

    // Error messages
    public static final String NAME_INVALID_MSG = "Name must contain only letters, numbers, spaces, hyphens, apostrophes and @";
    public static final String EMAIL_INVALID_MSG = "Invalid email format";
    public static final String ORGANIZATION_INVALID_MSG = "Organization must contain only letters, numbers, spaces, hyphens, dots and &";
    public static final String PASSWORD_WEAK_MSG = "Password must contain uppercase, lowercase, number and special character (@$!%*?&)";

    public static final String CN_INVALID_MSG = "CN must contain only letters, numbers, spaces, dots, hyphens and underscores";
    public static final String O_INVALID_MSG = "Organization must contain only letters, numbers, spaces, dots, hyphens, underscores and &";
    public static final String OU_INVALID_MSG = "OU must contain only letters, numbers, spaces, dots, hyphens and underscores";
    public static final String COUNTRY_INVALID_MSG = "Country must be 2 uppercase letters (e.g., RS, US)";

    public static final String DURATION_INVALID_MSG = "Duration must be between {min} and {max} days";
    public static final String FILE_SIZE_EXCEEDED_MSG = "File size must not exceed 1MB";
    public static final String FILE_EXTENSION_INVALID_MSG = "Invalid file extension";

    private ValidationConstants() {
        // Prevent instantiation
    }
}