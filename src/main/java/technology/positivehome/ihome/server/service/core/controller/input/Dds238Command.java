package technology.positivehome.ihome.server.service.core.controller.input;

public enum Dds238Command {
    READ_TOTAL_ENERGY(0, 2, 9),
    READ_VOLTAGE(0x0c, 1, 7),
    READ_CURRENT(0x0d, 1, 7),
    READ_FREQUENCY(0x11, 1, 7);

    int register;
    int data;
    int expectedLen;
    Dds238Command(int register, int data, int expectedLen) {
        this.register = register;
        this.data = data;
        this.expectedLen = expectedLen;
    }

    public int getRegister() {
        return register;
    }

    public int getData() {
        return data;
    }

    public int getExpectedLen() {
        return expectedLen;
    }
}
