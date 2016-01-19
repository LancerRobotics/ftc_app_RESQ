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
    public static final int LIMIT_ONE_ANALOG_PORT = 0; //just in case, may not be needed

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

    //Climber values PORT 3, SERVO 2
    //public static final double CLIMBER_HALFWAY = .65;
    public static final double CLIMBER_DUMP = .863;
    public static final double CLIMBER_INITIAL_STATE = 0;


    //Swivel values PORT 3 SERVO 5 TODO FIX VALUES ONCE NEW SERVOS ARRIVE
    public static final double SWIVEL_CENTER = .431;
    public static final double SWIVEL_LEFT = 0;
    public static final double SWIVEL_RIGHT = 1;

    //Right Trigger Values PORT 3 SERVO 1
    public static final double RT_INIT = 0;
    public static final double RT_TRIGGER = 0.47;

    //NavX values
    public static final byte NAVX_DEVICE_UPDATE_RATE_HZ = 50;
    public static final double TOLERANCE_DEGREES = 2.0;

    //Hanging values PORT 3, SERVO 3
    public static final double HANG_INIT = 1; //HANG UP AS WELL
    public static final double HANG_NOW = .196;

    //Dump values, PORT 3 SERVO 4;
    public static final double DUMP_INIT = 1;
    public static final double DUMP_DOWN = .392;

    //Clamp Left values, PORT 6, SERVO 4, TODO get real values
    public static final double CL_INIT = .706;
    public static final double CL_DOWN = 1;

    //Clamp Right values, PORT 6, SERVO 4
    public static final double CR_INIT = .502;
    public static final double CR_DOWN = 0;

    //Hopper Servos (SAME FOR BOTH) TODO confirm with real servos PORT 3 SERVO 1 and
    public static final double HOPPER_STORE = .863;
    public static final double HOPPER_DUMP = 0.784;

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
