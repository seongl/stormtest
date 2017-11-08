import java.io.Serializable;

/**
 * A configuration container class that hosts information obtained - from the
 * command line (e.g. location of the yaml file) - from the yaml file - at
 * runtime
 *
 */
public class Configuration implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final int DFLT_INIT_WAIT_TIME_MS = 10000;

    /** configuration file location */
    private String configFileLocation;

    /** life cycle init wait time */
    private int initWaitTimeMs = DFLT_INIT_WAIT_TIME_MS;

    /** root directory for any runtime configuration */
    private String runtimeConfigRoot;

    /** logging configuration */
    private LoggingConfig loggingConfig = new LoggingConfig();

    /** TSDB metric reporter and its tags */
    private MetricsConfig metricsConfig = new MetricsConfig();

    /** schema */
    private SchemaConfig schemaConfig = new SchemaConfig();

    /** change data merge processing configuration */
    private ChangeDataMergeConfig changeDataMergeConfig = new ChangeDataMergeConfig();

    /** Merge - data access information */
    private DataAccessConfig dataAccessConfig = new DataAccessConfig();

    /** Metadata store */
    private MetadataStoreConfig metadataStoreConfig = new MetadataStoreConfig();

    /** rheos state store */
    private StateStoreConfig stateConfig = new StateStoreConfig();


    /** rheos audit config */
    private AuditConfig auditConfig = new AuditConfig();

    /** dropwizard prometheus config **/
    private PromethuesConfig promethuesConfig = new PromethuesConfig();

    /** storm prometheus metrics consumer config **/
    private StormPrometheusConfig stormPrometheusConfig = new StormPrometheusConfig();

    /** getters and setters */
    public String getConfigFileLocation() {
        return configFileLocation;
    }

    public void setConfigFileLocation(String configFileLocation) {
        this.configFileLocation = configFileLocation;
    }

    public long getInitWaitTimeMs() {
        return initWaitTimeMs;
    }

    public void setInitWaitTimeMs(int initWaitTimeMs) {
        this.initWaitTimeMs = initWaitTimeMs;
    }

    public String getRuntimeConfigRoot() {
        return runtimeConfigRoot;
    }

    public void setRuntimeConfigRoot(String runtimeConfigRoot) {
        this.runtimeConfigRoot = runtimeConfigRoot;
    }

    public LoggingConfig getLoggingConfig() {
        return loggingConfig;
    }

    public void setLoggingConfig(LoggingConfig loggingConfig) {
        this.loggingConfig = loggingConfig;
    }

    public MetricsConfig getMetricsConfig() {
        return metricsConfig;
    }

    public void setMetricsConfig(MetricsConfig metricsConfig) {
        this.metricsConfig = metricsConfig;
    }

    public SchemaConfig getSchemaConfig() {
        return schemaConfig;
    }

    public void setSchemaConfig(SchemaConfig schemaConfig) {
        this.schemaConfig = schemaConfig;
    }

    public ChangeDataMergeConfig getChangeDataMergeConfig() {
        return changeDataMergeConfig;
    }

    public void setChangeDataMergeConfig(ChangeDataMergeConfig config) {
        this.changeDataMergeConfig = config;
    }

    public DataAccessConfig getDataAccessConfig() {
        return dataAccessConfig;
    }

    public void setDataAccessConfig(DataAccessConfig dataAccessConfig) {
        this.dataAccessConfig = dataAccessConfig;
    }

    public MetadataStoreConfig getMetadataStoreConfig() {
        return metadataStoreConfig;
    }

    public void setMetadataStoreConfig(MetadataStoreConfig metadataStoreConfig) {
        this.metadataStoreConfig = metadataStoreConfig;
    }

    public StateStoreConfig getStateConfig() {
        return stateConfig;
    }

    public void setStateConfig(StateStoreConfig stateConfig) {
        this.stateConfig = stateConfig;
    }


    public AuditConfig getAuditConfig() {
        return auditConfig;
    }

    public void setAuditConfig(AuditConfig auditConfig) {
        this.auditConfig = auditConfig;
    }

    public PromethuesConfig getPromethuesConfig() {
        return promethuesConfig;
    }

    public void setPromethuesConfig(PromethuesConfig promethuesConfig) {
        this.promethuesConfig = promethuesConfig;
    }

    public StormPrometheusConfig getStormPrometheusConfig() {
        return stormPrometheusConfig;
    }

    public void setStormPrometheusConfig(StormPrometheusConfig stormPrometheusConfig) {
        this.stormPrometheusConfig = stormPrometheusConfig;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(5000);
        sb.append("---------------\nConfiguration\n--------------- \n");
        sb.append(Parameters.CONFIG_FILE_LOCATION).append(": ").append(getConfigFileLocation()).append("\n");
        sb.append("\n");
        sb.append(Parameters.INIT_WAIT_TIME).append(": ").append(getInitWaitTimeMs()).append("\n");
        sb.append("\n");

        sb.append(this.loggingConfig.toString());
        sb.append(this.dataAccessConfig.toString());
        sb.append(this.changeDataMergeConfig.toString());
        sb.append(this.schemaConfig.toString());
        sb.append(this.metricsConfig.toString());
        sb.append(this.stateConfig.toString());
        sb.append(this.auditConfig.toString());
        sb.append(this.promethuesConfig.toString());
        sb.append(this.stormPrometheusConfig.toString());

        return sb.toString();
    }

}
