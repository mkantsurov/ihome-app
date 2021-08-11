package technology.positivehome.ihome.domain.constant;

/**
 * Created by maxim on 6/27/19.
 **/
public enum MegadPortType {
    UNDEFINED,                          //0
    RELAY_OUTPUT,                       //1
    BINARY_INPUT,                       //2
    DHT21_TEMPERATURE_HUMIDITY_SENSOR,  //3
    DS1820_TEMPERATURE_SENSOR,          //4
    DIMMER_OUTPUT,                      //5
    BME280_TEMP_HUMIDITY_PRESS_SENSOR,  //6
    TSL2591_LUMINOSITY_SENSOR,          //7
    ADC                                 //8
}
