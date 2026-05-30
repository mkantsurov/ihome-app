package technology.positivehome.ihome.domain.runtime.sensor;

public record Dds238PowerMeterData(Double voltage, Double current, Double freq, Double total) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        Double voltage = .0;
        Double current = .0;
        Double freq = .0;
        Double total = .0;

        public Builder voltage(Double voltage) {
            this.voltage = voltage;
            return this;
        }

        public Builder current(Double current) {
            this.current = current;
            return this;
        }

        public Builder freq(Double frequency) {
            this.freq = frequency;
            return this;
        }

        public Builder total(Double total) {
            this.total = total;
            return this;
        }

        public Dds238PowerMeterData build() {
            return new Dds238PowerMeterData(voltage, current, freq, total);
        }
    }
}
