package com.interviewprep.modules.roadmap.exception;

/**
 * Thrown for roadmap-specific business rule violations (e.g. an invalid selected duration).
 * Handled by the global exception handler.
 */
public class RoadmapException extends RuntimeException {

    public RoadmapException(String message) {
        super(message);
    }
}
