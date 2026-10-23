package com.pjsoft.saving_loan_api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/** Always answers with JSON {success:false, message:...} so the UI can show a real reason. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> tooLarge(MaxUploadSizeExceededException ex) {
        return body(HttpStatus.PAYLOAD_TOO_LARGE,
                "Uploaded file is too large. Each document must be 10 MB or smaller (30 MB in total).");
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Map<String, Object>> badMultipart(MultipartException ex) {
        log.warn("Multipart request failed", ex);
        return body(HttpStatus.BAD_REQUEST, "The upload could not be read. Please re-select the files and try again.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation", ex);
        return body(HttpStatus.CONFLICT, "Some of the submitted data is invalid or already exists (for example a duplicate mobile number).");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> unexpected(Exception ex) {
        // Framework / ResponseStatusException errors keep their own status (404, 405, 415, 503 ...)
        if (ex instanceof ErrorResponse) {
            ErrorResponse errorResponse = (ErrorResponse) ex;
            String detail = errorResponse.getBody().getDetail();
            if (ex instanceof ResponseStatusException && ((ResponseStatusException) ex).getReason() != null) {
                detail = ((ResponseStatusException) ex).getReason();
            }
            return ResponseEntity.status(errorResponse.getStatusCode())
                    .body(payload(detail != null ? detail : "Request failed."));
        }
        log.error("Unhandled error", ex);
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error. Please check the server log.");
    }

    private static ResponseEntity<Map<String, Object>> body(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(payload(message));
    }

    private static Map<String, Object> payload(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", message);
        return body;
    }
}
