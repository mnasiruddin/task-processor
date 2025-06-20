package com.coolplanet.task.domain.exception;

import org.springframework.http.HttpStatus;

public class ProcessingException extends TrackerException {

        public ProcessingException(String message) {
            super(message, ErrorCodes.PROCESSING_ERROR.getCode(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

}
