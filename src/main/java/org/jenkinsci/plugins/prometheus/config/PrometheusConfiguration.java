package org.jenkinsci.plugins.prometheus.config;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Robin Müller
 */
@Extension
public class PrometheusConfiguration extends GlobalConfiguration {

    private static final String PROMETHEUS_ENDPOINT = "PROMETHEUS_ENDPOINT";
    private static final String DEFAULT_ENDPOINT = "prometheus";

    private String urlName;
    private String additionalPath;
    private String defaultNamespace = "default";
    private String jobAttributeName = "jenkins_job";
    private boolean useAuthenticatedEndpoint;
    
    private boolean countSuccessfulBuilds = true;
    private boolean countUnstableBuilds = true;
    private boolean countFailedBuilds = true;
    private boolean countNotBuiltBuilds = true;
    private boolean countAbortedBuilds = true;
    private boolean fetchTestResults = true;

    private boolean processingDisabledBuilds = false;

    public PrometheusConfiguration() {
        load();
        if (urlName == null) {
            Map<String, String> env = System.getenv();
            setPath(env.getOrDefault(PROMETHEUS_ENDPOINT, DEFAULT_ENDPOINT));
        }
    }

    public static PrometheusConfiguration get() {
        Descriptor configuration = Jenkins.getInstance().getDescriptor(PrometheusConfiguration.class);
        return (PrometheusConfiguration) configuration;
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        setPath(json.getString("path"));
        useAuthenticatedEndpoint = json.getBoolean("useAuthenticatedEndpoint");
        defaultNamespace = json.getString("defaultNamespace");
        jobAttributeName = json.getString( "jobAttributeName");
        countSuccessfulBuilds = json.getBoolean("countSuccessfulBuilds");
        countUnstableBuilds = json.getBoolean("countUnstableBuilds");
        countFailedBuilds = json.getBoolean("countFailedBuilds");
        countNotBuiltBuilds = json.getBoolean("countNotBuiltBuilds");
        countAbortedBuilds = json.getBoolean("countAbortedBuilds");
        fetchTestResults = json.getBoolean("fetchTestResults");

        processingDisabledBuilds = json.getBoolean("processingDisabledBuilds");

        save();
        return super.configure(req, json);
    }

    public String getPath() {
        return StringUtils.isEmpty(additionalPath) ? urlName : urlName + "/" + additionalPath;
    }

    public void setPath(String path) {
        urlName = path.split("/")[0];
        List<String> pathParts = Arrays.asList(path.split("/"));
        additionalPath = (pathParts.size() > 1 ? "/" : "") + StringUtils.join(pathParts.subList(1, pathParts.size()), "/");
        save();
    }
    public String getJobAttributeName() { return jobAttributeName; }

    public void setJobAttributeName(String jobAttributeName) {
        this.jobAttributeName = jobAttributeName;
        save();
    }

    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    public void setDefaultNamespace(String path) {
        this.defaultNamespace = path;
        save();
    }

    public boolean isUseAuthenticatedEndpoint() {
        return useAuthenticatedEndpoint;
    }

    public void setUseAuthenticatedEndpoint(boolean useAuthenticatedEndpoint) {
        this.useAuthenticatedEndpoint = useAuthenticatedEndpoint;
        save();
    }
    
    public boolean isCountSuccessfulBuilds() {
        return countSuccessfulBuilds;
    }

    public void setCountSuccessfulBuilds(boolean countSuccessfulBuilds) {
        this.countSuccessfulBuilds = countSuccessfulBuilds;
        save();
    }

    public boolean isCountUnstableBuilds() {
        return countUnstableBuilds;
    }

    public void setCountUnstableBuilds(boolean countUnstableBuilds) {
        this.countUnstableBuilds = countUnstableBuilds;
        save();
    }

    public boolean isCountFailedBuilds() {
        return countFailedBuilds;
    }

    public void setCountFailedBuilds(boolean countFailedBuilds) {
        this.countFailedBuilds = countFailedBuilds;
        save();
    }

    public boolean isCountNotBuiltBuilds() {
        return countNotBuiltBuilds;
    }

    public void setCountNotBuiltBuilds(boolean countNotBuiltBuilds) {
        this.countNotBuiltBuilds = countNotBuiltBuilds;
        save();
    }

    public boolean isCountAbortedBuilds() {
        return countAbortedBuilds;
    }

    public void setCountAbortedBuilds(boolean countAbortedBuilds) {
        this.countAbortedBuilds = countAbortedBuilds;
        save();
    }

    public boolean isFetchTestResults() {
        return fetchTestResults;
    }

    public void setFetchTestResults(boolean fetchTestResults) {
        this.fetchTestResults = fetchTestResults;
        save();
    }

    public boolean isProcessingDisabledBuilds() {
        return processingDisabledBuilds;
    }

    public void setProcessingDisabledBuilds(boolean processingDisabledBuilds) {
        this.processingDisabledBuilds = processingDisabledBuilds;
        save();
    }

    public String getUrlName() {
        return urlName;
    }

    public String getAdditionalPath() {
        return additionalPath;
    }

    public FormValidation doCheckPath(@QueryParameter String value) {
        if (StringUtils.isEmpty(value)) {
            return FormValidation.error(Messages.path_required());
        } else if (System.getenv().containsKey(PROMETHEUS_ENDPOINT)) {
            return FormValidation.warning(Messages.path_environment_override(PROMETHEUS_ENDPOINT, System.getenv(PROMETHEUS_ENDPOINT)));
        } else {
            return FormValidation.ok();
        }
    }
}
