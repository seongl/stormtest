/**
 * This defines the change data capture stream bootstrap parameters that are
 * required for starting storm supervisor process and running the storm
 * topology.
 *
 */
public interface Parameters {
    /** Location of rheos configuration file */
    static final String CONFIG_FILE_LOCATION = "config.file.location";

    static final String RUNTIME_CONTEXT_CONFIG_ROOT = "runtime.context.config.root";

    /** Launching the storm supervisor */
    static final String TOPO_NAME = "topology.name";
    static final String TOPO_SPOUT_INSTANCES = "topology.spout.instances";
    static final String TOPO_KAFKA_BOLT_INSTANCES = "topology.kafka.bolt.instances";
    static final String TOPO_WORKER_INSTANCES = "topology.worker.instances";
    static final String TOPO_MESSAGE_TIMEOUT_SEC = "topology.message.timeout.sec";

    static final String TOPO_KAFKA_BOLT_PARALLELISM = "topology.kafka.bolt.parallelism";
    static final String TOPO_SPOUT_PARALLELISM = "topology.spout.parallelism";
    static final String TOPO_MAX_SPOUT_PENDING = "topology.max.spout.pending";
    static final String TOPO_NUM_ACKERS = "topology.num.ackers";

    /**
     * Initialization - a global wait time for each life cycle to complete its
     * initialization
     */
    static final String INIT_WAIT_TIME = "init.wait.time.ms";

    /** Logging - logback logger and central application logger information */
    static final String LOGGER_LIFECYCLE_CLASS = "logger.lifecycle.class";
    static final String LOGGING_CAL_ENV = "logging.cal.environment";
    static final String LOGGING_CAL_POOL = "logging.cal.pool";
    static final String LOGGING_CAL_REMOTE_ADDRESS = "logging.cal.remote.address";
    static final String LOGGING_CAL_SERVICE_TYPE = "logging.cal.service.type";
    static final String LOGGING_CAL_PERFMON_ENABLE = "logging.cal.perfmon.enable";

    /** Merge - JDBC connection information */
    static final String MERGE_ENABLED = "merge.enabled";

    /** Merge - data access information */
    static final String MERGE_DB_ENV = "merge.db.environment";
    static final String MERGE_DB_ROLE = "merge.db.role";
    static final String MERGE_DB_SID = "merge.db.sid";
    static final String MERGE_DAL_LIFECYCLE_CLASS = "merge.dal.lifecycle.class";

    /** State */
    static final String STATE_DB_FAMILY = "state.db.family";
    static final String STATE_DB_SCHEMA = "state.db.schema";

    /** LCR Kafka information */
    static final String LCR_TOPIC_LIST = "lcr.topic.list";

    /** Metric Reporter information */
    static final String METRICS_CLIENT_LIFECYCLE_CLASS = "metrics.client.lifecycle.class";
    static final String METRIC_RPT_URI = "metric.reporter.uri";
    static final String METRIC_RPT_PROFILE = "metric.reporter.profile";
    static final String METRIC_TAG_APPNAME = "metric.tag.appname";
    static final String METRIC_TAG_DATACENTER = "metric.tag.datacenter";
    static final String METRIC_REPORT_INTEVAL_SEC = "metric.reporter.interval.sec";
    static final String METRIC_WHITE_LIST_PATTERN = "metric.reporter.filter.pattern";
    static final String METRIC_CLIENT_ID = "metric.reporter.client.id";

    /** Metadata store configuration */
    static final String METADATA_STORE_HOST = "metadata.store.host";
    static final String METADATA_STORE_LIST = "metadata.store.list";

    /** State store configuration */
    static final String STATE_STORE_CONNECT_STRING = "state.store.connect.string";
    static final String STATE_STORE_DATA_CONNECT_STRING = "state.store.data.connect.string";
    static final String STATE_STORE_SESSION_TIMEOUT = "state.store.session.timeout";
    static final String STATE_STORE_CONNECTION_TIMEOUT = "state.store.connection.timeout";
    static final String STATE_STORE_RETRY_ATTEMPTS = "state.store.retry.attempts";
    static final String STATE_STORE_RETRY_INTERVAL = "state.store.retry.interval";
    static final String STATE_STORE_NAMESPACE = "state.store.namespace";
    static final String STATE_STORE_REPORTING_INTERVAL_SEC = "state.store.reporting.sec";
    static final String STATE_STORE_DATA_DATACENTER = "state.store.data.datacenter";
    static final String STATE_STORE_ENABLED = "state.store.enabled";

    /** kakfa, storm properties configuration url **/
    static final String RHEOS_META_DATA_REGISTRY_URLS = "rheos.services.urls";
    static final String LCR_CONSUMER_GROUP_ID = "lcr.consumer.group.id";
    static final String FCR_PRODUCER_GROUP_ID = "fcr.producer.group.id";
    static final String REGION = "region";

    static final String TOPOLOGY_WORKER_CHILDOPTS = "topology.worker.childopts";
    static final String WORKER_CHILDOPTS = "worker.childopts";

    /** jaas configuration */

    /** audit configuration */
    static final String AUDIT_ENABLE = "audit.enable";
    static final String AUDIT_LIFECYCLE_CLASS = "audit.lifecycle.class";
    static final String AUDIT_REST_URL = "audit.rest.url";
    static final String AUDIT_API_URL = "audit.api.url";
    static final String AUDIT_BATCH_API_URL = "audit.batch.api.url";
    static final String AUDIT_DATACENTER = "audit.datacenter";
    static final String AUDIT_TIER = "audit.tier";
    static final String AUDIT_ENABLE_BATCH = "audit.enable.batch";
    static final String AUDIT_BATCH_SIZE = "audit.batch.size";
    static final String AUDIT_BUFFER_SIZE = "audit.buffer.size";
    static final String AUDIT_WORKING_QUEUE_SIZE = "audit.working.queue.size";
    static final String AUDIT_WORKER_THREAD_SIZE = "audit.worker.thread.size";
    static final String AUDIT_HTTP_POOL_SIZE = "audit.http.pool.size";
    static final String AUDIT_FLUSH_INTERVAL = "audit.flush.interval";

}