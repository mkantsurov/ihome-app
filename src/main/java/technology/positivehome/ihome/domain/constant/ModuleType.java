package technology.positivehome.ihome.domain.constant;

/**
 * Created by maxim on 6/27/19.
 **/
public enum ModuleType {
    UNDEFINED,                                                  //0
    GENERIC_RELAY_POWER_CONTROL_MODULE,                         //1
    GENERIC_DIMMER_POWER_CONTROL_MODULE,                        //2
    GARAGE_LIGHT__POWER_CONTROL_MODULE,                         //3
    GARAGE_VENTILATION_POWER_CONTROL_MODULE,                    //4
    HEATING_SYSTEM_PUMP_POWER_CONTROL_MODULE,                   //5
    HOME_LIGHT_RELAY_POWER_CONTROL_MODULE,                      //6
    HOME_LIGHT_DIMMABLE_POWER_CONTROL_MODULE,                   //7
    GENERIC_INPUT_POWER_DEPENDENT_RELAY_POWER_CONTROL_MODULE,   //8
    HOME_LIGHT_MOVENMENT_SENSOR_RELAY_BASED_CONTROL_MODULE,     //9
    HOME_VENTILATION_MOVENMENT_HUMIDITY_SENSOR_RELAY_BASED_CONTROL_MODULE,  //10
    DIRECT_INPUT_POWER_SUPPLY_CONTROL_MODULE,                   //11
    CONVERTER_INPUT_POWER_SUPPLY_CONTROL_MODULE,                //12
    HOME_LIGHT_DAYLIGHT_DEPENDENT_MOVENMENT_SENSOR_RELAY_BASED_CONTROL_MODULE,     //13
    HEAT_WATER_RECIRQULATION_POWER_CONTROL_MODULE,              //14
    SECURITY_MODE_DEPENDENT_RELAY_BASED_IHOME_MODULE,           //15
    HOME_LIGHT_RELAY_LIGHT_DEPENDENT_POWER_CONTROL_MODULE,      //16
    GARAGE_INVERTER_COOLING_CONTROL_MODULE,                     //17,
    RECUPERATOR_POWER_SUPPLY_CONTROL_MODULE,                    //18
    SOLAR_SYSTEM_PUMP_POWER_CONTROL_MODULE,                     //19
    RESERVED20,
    RESERVED21,
    RESERVED22,
    RESERVED23,
    RESERVED24,
    RESERVED25,
    RESERVED26,
    RESERVED27,
    RESERVED28,
    RESERVED29,
    RESERVED30,
    SUMMARY_INFO_MODULE;                                         //31

    public boolean isDimmable() {
        switch (this) {
            case GENERIC_DIMMER_POWER_CONTROL_MODULE:
            case HOME_LIGHT_DIMMABLE_POWER_CONTROL_MODULE:
                return true;
        }
        return false;
    }
}
