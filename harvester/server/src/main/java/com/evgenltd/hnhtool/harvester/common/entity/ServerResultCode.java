package com.evgenltd.hnhtool.harvester.common.entity;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 01-04-2019 23:47</p>
 */
public class ServerResultCode {

    public static final String AGENT_NOT_AUTHENTICATED = "AGENT_NOT_AUTHENTICATED";

    public static final String AGENT_ACTIVATED = "AGENT_ALREADY_ACTIVATED";
    public static final String AGENT_DEACTIVATED = "AGENT_ALREADY_DEACTIVATED";

    public static final String AGENT_REJECT_WORK_OFFER = "AGENT_REJECT_WORK_OFFER";

    public static final String AGENT_ALL_BUSY = "AGENT_ALL_BUSY";
    public static final String AGENT_NO_MATCH_REQUIREMENTS = "AGENT_NO_MATCH_REQUIREMENTS";

    public static final String NO_MATCHED_WORLD_OBJECT_FOUND = "NO_MATCHED_WORLD_OBJECT_FOUND";
    public static final String NO_MATCHED_KNOWN_OBJECT_FOUND = "NO_MATCHED_KNOWN_OBJECT_FOUND";

    public static final String EXCEPTION_DURING_TASK_PERFORMING = "EXCEPTION_DURING_TASK_PERFORMING";

    // knowledge matching service

    public static final String KMS_WORLD_OBJECTS_NOT_PRESENTED = "KMS_WORLD_OBJECTS_NOT_PRESENTED";
    public static final String KMS_WORLD_OBJECTS_ALL_FILTERED = "KMS_WORLD_OBJECTS_ALL_FILTERED";
    public static final String KMS_WORLD_OBJECTS_LOW_COUNT = "KMS_WORLD_OBJECTS_LOW_COUNT";
    public static final String KMS_REFERENCE_POINT_TRYING_LIMIT_EXCEEDED = "KMS_REFERENCE_POINT_TRYING_LIMIT_EXCEEDED";
    public static final String KMS_REFERENCE_POINT_NO_SUITABLE_OBJECT = "KMS_REFERENCE_POINT_NO_SUITABLE";
    public static final String KMS_NO_SPACE_CANDIDATES = "KMS_NO_SPACE_CANDIDATES";
    public static final String KMS_TOO_MANY_SPACE_CANDIDATES = "KMS_TOO_MANY_SPACE_CANDIDATES";
}