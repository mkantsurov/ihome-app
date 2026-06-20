package technology.positivehome.ihome.model.constant;

public enum ErrorEventType {
    UNDEFINED,
    /* System events */
    APPLICATION_STARTUP,
    SYSTEM_STAT_COLLECTION,
    LUMINOSITY_READING,
    MODULE_INIT_DEFAULT_STATE,
    MODULE_CRON_TASK_EXECUTION,
    MODULE_JOB_TASK_EXECUTION,
    /* Module operations */
    MODULE_STATE_REQUEST,
    MODULE_LIGHT_TOGGLE,
    MODULE_LIGHT_ENABLE,
    MODULE_LIGHT_DISABLE,
    MODULE_POWER_SUPPLY_CONTROL,
    /* Security */
    INVALID_JWT_TOKEN,
}
