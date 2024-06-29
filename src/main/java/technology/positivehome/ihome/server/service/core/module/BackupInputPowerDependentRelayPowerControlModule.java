package technology.positivehome.ihome.server.service.core.module;

import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.server.model.command.IHomeCommandFactory;
import technology.positivehome.ihome.server.service.core.SystemManager;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static technology.positivehome.ihome.domain.constant.PreferredPowerSupplyMode.ONLY_LED;

public class BackupInputPowerDependentRelayPowerControlModule extends AbstractRelayBasedIHomeModule implements IHomeModule {

    private static final long POWER_CHECK_INTERVAL = TimeUnit.SECONDS.toMillis(30);
    private static final long MAX_POWER_ABSENT_DELAY = TimeUnit.SECONDS.toMillis(60);
    private static final long POWER_CHECKING_DELAY = TimeUnit.MINUTES.toMillis(5);

    public static final int POWER_SENSOR_PORT_ID = 29;

    public static final int POWER_METER_PORT_ID = 85;
    public static final int INT_POWER_METER_PORT_ID = 86;
    public static final int INT_BCK_POWER_METER_PORT_ID = 88;

    private final CronModuleJob[] moduleJobs;

    private final AtomicLong lastPowerOkTs = new AtomicLong(System.currentTimeMillis());
    private final AtomicLong lastPowerFailTs = new AtomicLong(System.currentTimeMillis());

    public BackupInputPowerDependentRelayPowerControlModule(SystemManager mgr, ModuleConfigEntry configEntry) {
        super(mgr, configEntry);
        moduleJobs = new CronModuleJob[]{
                new CronModuleJob(POWER_CHECK_INTERVAL) {
                    @Override
                    protected void execute() throws Exception {
                        switch (getMode()) {
                            case AUTO:
                                OutputPortStatus status = getOutputPortStatus();
                                BinaryPortStatus state = getMgr().runCommand(IHomeCommandFactory.cmdGetBinarySensorReading(POWER_SENSOR_PORT_ID));
                                double voltage;
                                if (BinaryPortStatus.ENABLED.equals(state)) {
                                    voltage = getMgr().runCommand(IHomeCommandFactory.cmdGetDds238Reading(POWER_METER_PORT_ID)).voltage();
                                } else {
                                    voltage = 0;
                                }
                                boolean powerSupplyOk = voltage > 170 && voltage < 245;
                                long now = System.currentTimeMillis();
                                if (powerSupplyOk || mgr.getInputPowerSupplySourceCalc().getPreferredPowerSupplyMode().equals(ONLY_LED)) {
                                    lastPowerOkTs.set(System.currentTimeMillis());
                                    if (status.isDisabled() && now - POWER_CHECKING_DELAY > lastPowerFailTs.get()) {
                                        setOutputStatus(OutputPortStatus.enabled());
                                    }
                                } else {
                                    lastPowerFailTs.set(System.currentTimeMillis());
                                    if (status.isEnabled() && now - MAX_POWER_ABSENT_DELAY > lastPowerOkTs.get()) {
                                        setOutputStatus(OutputPortStatus.disabled());
                                    }
                                }
                                break;
                        }
                    }
                }};
    }

    @Override
    protected CronModuleJob[] getJobList() {
        return moduleJobs;
    }
}
