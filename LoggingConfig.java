import java.io.Serializable;

/**
 * Logging configuration POJO.  This also includes Centralize Application Logging (CAL) configuration.
 *
 */
public class LoggingConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String DFLT_CAL_SERVICE_TYPE = "NULL";
    private static final int DFLT_CAL_CONNECTIONS = 1;
    private static final boolean DFLT_CAL_PERFMON_ENABLE = true;

    /** logger class and centralize application logger (cal) parameters */
    private String loggerLifeCycleClass;
    private String calRemoteAddress;
    private String calEnvironment;
    private String calPoolName;
    private String calServiceType = DFLT_CAL_SERVICE_TYPE;
    private int calNumConnections = DFLT_CAL_CONNECTIONS;
    private boolean isPerfMonEnabled = DFLT_CAL_PERFMON_ENABLE;

    public String getLoggerLifeCycleClass() {
        return loggerLifeCycleClass;
    }

    public void setLoggerLifeCycleClass(String loggerLifeCycleClass) {
        this.loggerLifeCycleClass = loggerLifeCycleClass;
    }

    public String getCalRemoteAddress() {
        return calRemoteAddress;
    }

    public void setCalRemoteAddress(String calRemoteAddress) {
        this.calRemoteAddress = calRemoteAddress;
    }

    public String getCalEnvironment() {
        return calEnvironment;
    }

    public void setCalEnvironment(String calEnvironment) {
        this.calEnvironment = calEnvironment;
    }

    public String getCalPoolName() {
        return calPoolName;
    }

    public void setCalPoolName(String calPoolName) {
        this.calPoolName = calPoolName;
    }

    public String getCalServiceType() {
        return calServiceType;
    }

    public void setCalServiceType(String calServiceType) {
        this.calServiceType = calServiceType;
    }

    public int getCalNumConnections() {
        return calNumConnections;
    }

    public void setCalNumConnections(int calNumConnections) {
        this.calNumConnections = calNumConnections;
    }

    public boolean isPerfMonEnabled() {
        return isPerfMonEnabled;
    }

    public void setPerfMonEnabled(boolean isEnabled) {
        this.isPerfMonEnabled = isEnabled;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(500);

        sb.append("\n");
        sb.append(Parameters.LOGGER_LIFECYCLE_CLASS).append(": ").append(getLoggerLifeCycleClass()).append("\n");
        sb.append(Parameters.LOGGING_CAL_ENV).append(": ").append(getCalEnvironment()).append("\n");
        sb.append(Parameters.LOGGING_CAL_POOL).append(": ").append(getCalPoolName()).append("\n");
        sb.append(Parameters.LOGGING_CAL_REMOTE_ADDRESS).append(": ").append(getCalRemoteAddress()).append("\n");
        sb.append(Parameters.LOGGING_CAL_SERVICE_TYPE).append(": ").append(getCalServiceType()).append("\n");
        sb.append(Parameters.LOGGING_CAL_PERFMON_ENABLE).append(": ").append(isPerfMonEnabled()).append("\n");
        sb.append("\n");

        return sb.toString();
    }

}
