package technology.positivehome.ihome.server.service.core.controller;

import com.google.common.base.Strings;

/**
 * Created by maxim on 6/30/19.
 **/
public class ControllerEventInfo {

    private long sourceId;
    private int port;
    private Integer mode;
    private Integer count;
    private Integer click;

    private ControllerEventInfo(Builder bld) {
        sourceId = bld.sourceId;
        port = bld.port;
        mode = bld.mode;
        count = bld.count;
        click = bld.click;
    }

    public long getSourceId() {
        return sourceId;
    }

    public int getPort() {
        return port;
    }

    public Integer getMode() {
        return mode;
    }

    public Integer getCount() {
        return count;
    }

    public Integer getClick() {
        return click;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private long sourceId;
        private int port;
        private Integer mode;
        private Integer count;
        private Integer click;

        public Builder sourceId(long srcId) {
            this.sourceId = srcId;
            return this;
        }

        public Builder port(String portName) {
            this.port = Integer.parseInt(portName);
            return this;
        }

        public Builder mode(String mode) {
            if (!Strings.isNullOrEmpty(mode)) {
                this.mode = Integer.parseInt(mode);
            }
            return this;
        }

        public Builder count(String count) {
            if (!Strings.isNullOrEmpty(count)) {
                this.count = Integer.parseInt(count);
            }
            return this;
        }

        public ControllerEventInfo build() {
            return new ControllerEventInfo(this);
        }

        public Builder click(String click) {
            if (!Strings.isNullOrEmpty(click)) {
                this.click = Integer.parseInt(click);
            }
            return this;
        }
    }
}
