package com.qualcomm.ftcrobotcontroller;

/**
 * Created by AJ on 10/26/2015.
 */
public class Keys {
    public static final String frontRight = "front_right";
    public static final String frontLeft = "front_left";
    public static final String backRight = "back_right";
    public static final String backLeft = "back_left";
    public static final String liftLeft = "lift_left";
    public static final String liftRight = "lift_right";
    public static final String collector = "collector";
    //public static final String arm = "arm";
    public static final String swivel= "swivel";
    public static final String filterLeft = "filter_left";
    public static final String filterRight = "filter_right";
    public static final String score = "score";
    public static final String climber = "climber";
    public static final String touch = "touch" ;
    public static final String advancedSensorModule = "asm";
    public static final String LIMIT_ONE = "ls1";
    public static final String SONAR_ONE = "sona1";

    public static final int NAVX_DIM_I2C_PORT = 0;
    public static final int SONAR_ONE_ANALOG_PORT = 0; //just in case, may not be needed
    public static final int LIMIT_ONE_DIGITAL_PORT = 0; //just in case, may not be needed

    public static final String telementryLeftKey = "left joystick  y value:";
    public static final String telementryRightKey = "right joystick y value: ";
    public static final String telementryFrontLeftPowerKey = "telementry_front_left_power_key";
    public static final String telementryFrontRightPowerKey = "telementry_front_right_power_key";
    public static final String telementryBackLeftPowerKey = "telementry_back_left_power_key";
    public static final String telementryBackRightPowerKey = "telementry_back_right_power_key";

    public static final double CLIMBER_INITIAL_STATE = 1;
    public static final double FILTER_ACTIVE = 0.07;
    public static final double FILTER_CLOSE = .1;
    public static final double FILTER_UP = .7;
    public static final double MAX_SPEED_SMOOTH_MOVE = .6;
    public static final double MIN_SPEED_SMOOTH_MOVE = .001;

    public static byte[] IMAGE_DATA;
    public static String imagePath = "/storage/emulated/0/Pictures/Matt Quan's Eyes/";

    public static final double SCORE_CLOSE = 0;
    public static final double SCORE_SCORING = .53

            ;
    public static final double CLIMBER_HALFWAY = .65;
    public static final double CLIMBER_DUMP = .1;
    //TODO test Zip initial state pos values
    public static final double ZIP_LEFT_INITIAL_STATE = .8;
    public static final double ZIP_RIGHT_INITIAL_STATE = .8;
    public static final double ZIP_LEFT_DOWN = .4;
    public static final double ZIP_RIGHT_DOWN = .4;

    //swivel
    public static final double SWIVEL_CENTER = .5;
    public static final double SWIVEL_LEFT = .132;
    public static final double SWIVEL_RIGHT = .7663;


    public static final String SWIVEL_TELEMETRY = "swivel position: ";

    public static final double MAX_SPEED = .86;

    final public static String pictureImagePathSharedPrefsKeys = "com.qualcomm.ftcrobotcontroller.pictureImagePathSharedPrefsKeys";

    final public static String testServo = "servo_test";

    public static final byte NAVX_DEVICE_UPDATE_RATE_HZ = 50;
    public static final double TOLERANCE_DEGREES = 2.0;

}
