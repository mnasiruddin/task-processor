package com.coolplanet.task.domain.exception;

import org.springframework.http.HttpStatus;

public class TaskNotFoundException extends TrackerException {

        public TaskNotFoundException(String message) {
            super(message, ErrorCodes.RECORD_NOT_FOUND_ERROR.getCode(), HttpStatus.NOT_FOUND.value());
        }
}
