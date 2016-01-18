package com.qualcomm.ftcrobotcontroller;

public class Keys {
    //Robot parts
    public static final String frontRight = "front_right";
    public static final String frontLeft = "front_left";
    public static final String backRight = "back_right";
    public static final String backLeft = "back_left";
    public static final String liftLeft = "lift_left";
    public static final String liftRight = "lift_right";
    public static final String collector = "collector";
    public static final String swivel= "swivel";
    public static final String score = "score";
    public static final String climber = "climber";
    public static final String dump1 = "dump1";
    public static final String dump2 = "dump2";
    public static final String clamp1 = "clamp1";
    public static final String clamp2 = "clamp2";
    public static final String hang = "hang";
    public static final String advancedSensorModule = "asm";
    public static final String LIMIT_ONE = "ls1";
    public static final String SONAR_ONE = "sona1";

    //Ports
    public static final int NAVX_DIM_I2C_PORT = 0;
    public static final int SONAR_ONE_ANALOG_PORT = 0; //just in case, may not be needed
    public static final int LIMIT_ONE_DIGITAL_PORT = 0; //just in case, may not be needed

    //Telementry
    public static final String telementryLeftKey = "left joystick  y value:";
    public static final String telementryRightKey = "right joystick y value: ";
    public static final String telementryFrontLeftPowerKey = "telementry_front_left_power_key";
    public static final String telementryFrontRightPowerKey = "telementry_front_right_power_key";
    public static final String telementryBackLeftPowerKey = "telementry_back_left_power_key";
    public static final String telementryBackRightPowerKey = "telementry_back_right_power_key";
    public static final String SWIVEL_TELEMETRY = "swivel position: ";

    //Smooth move values
    public static final double MAX_SPEED_SMOOTH_MOVE = .6;
    public static final double MIN_SPEED_SMOOTH_MOVE = .001;

    //Imaging stuff
    public static byte[] IMAGE_DATA;
    public static String imagePath = "/storage/emulated/0/Pictures/Matt Quan's Eyes/";
    final public static String pictureImagePathSharedPrefsKeys = "com.qualcomm.ftcrobotcontroller.pictureImagePathSharedPrefsKeys";

    //Scoring values
    public static final double SCORE_CLOSE = 0;
    public static final double SCORE_SCORING = .53;

    //Climber values
    public static final double CLIMBER_HALFWAY = .65;
    public static final double CLIMBER_DUMP = .1;
    public static final double CLIMBER_INITIAL_STATE = 1;


    //Swivel values
    public static final double SWIVEL_CENTER = .5;
    public static final double SWIVEL_LEFT = .132;
    public static final double SWIVEL_RIGHT = .7663;

    //NavX values
    public static final byte NAVX_DEVICE_UPDATE_RATE_HZ = 50;
    public static final double TOLERANCE_DEGREES = 2.0;

    //Hanging values TODO test values, these are just random numbers for now
    public static final double HANG_INIT = .5;
    public static final double HANG_NOW = .3;
    public static final double HANG_DOWN = .2;

    //Dump values, should be the same for both cause one will be reversed TODO get real values, these are just placeholders
    public static final double DUMP_INIT = .5;
    public static final double DUMP_UP = .6;
    public static final double DUMP_DOWN = .5;

    //Clamp values, same for both, TODO get real values
    public static final double CLAMP_INIT = .5;
    public static final double CLAMP_DOWN = .3;
    public static final double CLAMP_UP = .4;

    //Collector power
    public static final double COLLECTOR = .5;

    //Misc
    public static final double MAX_SPEED = .86;








    //Depreciated
    public static final String touch = "touch" ;
    final public static String testServo = "servo_test";
    public static final double ZIP_LEFT_INITIAL_STATE = .8;
    public static final double ZIP_RIGHT_INITIAL_STATE = .8;
    public static final double ZIP_LEFT_DOWN = .4;
    public static final double ZIP_RIGHT_DOWN = .4;
    public static final String filterLeft = "filter_left";
    public static final String filterRight = "filter_right";
    public static final double FILTER_ACTIVE = 0.07;
    public static final double FILTER_CLOSE = .1;
    public static final double FILTER_UP = .7;

}
