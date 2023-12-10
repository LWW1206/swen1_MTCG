package at.technikum.server.http;

// THOUGHT: Add new relevant status (https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)
public enum HttpStatus {
    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    CREATED(201, "Created"),
    NO_CONTENT(204, "No Content"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    CONFLICT(409, "Conflict"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    METHOD_NOT_ALLOWED(405, "Method not allowed");

    private final int code; // status Code
    private final String message; // status Message

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


}
