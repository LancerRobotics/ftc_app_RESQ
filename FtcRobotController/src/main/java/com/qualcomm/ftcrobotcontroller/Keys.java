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
    public static final String climber = "climber";
    public static final String hopperLeft = "hopper_left";
    public static final String hopperRight = "hopper_right";
    public static final String clampLeft = "clamp_left";
    public static final String clampRight = "clamp_right";
    public static final String buttonPusher = "button_pusher";
    public static final String hang = "hang";
    public static final String advancedSensorModule = "asm";
    public static final String LIMIT_LEFT = "lsl";
    public static final String LIMIT_RIGHT = "lsr";
    public static final String SONAR_ABOVE_PHONE = "sonar_above_phone";
    public static final String SONAR_FOOT = "sonar_foot";
    public static final String COLOR_FRONT_RIGHT = "colorFR";
    public static final String COLOR_FRONT_LEFT = "colorFL";
    public static final String dump = "dump";
    public static final String triggerLeft = "trigger_left";
    public static final String triggerRight = "trigger_right";
    public static final String winch = "winch";
    public static final String jam = "jam";

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


    //Climber values
    public static final double CLIMBER_DUMP = .3415;
    public static final double CLIMBER_INITIAL_STATE = .85;

    public static final double JAM_INIT = .1;
    public static final double JAM_JAM = .2;

    //Swivel values
    public static final double SWIVEL_CENTER = .5;
    public static final double SWIVEL_LEFT = .20;
    public static final double SWIVEL_RIGHT = .80;

    //button pusher todo REMOVE ALL REFERENCES OF THIS
    public static final double BUTTON_PUSHER_INIT=.95;
    public static final double BUTTON_PUSHER_PUSH = .25;

    //Right Trigger Values
    public static final double RIGHT_TRIGGER_INIT = .20;
    public static final double RIGHT_TRIGGER_TRIGGER = 0.80;

    //Left Trigger Values
    public static final double LEFT_TRIGGER_INIT = .80;
    public static final double LEFT_TRIGGER_TRIGGER = 0.20;

    //NavX values
    public static final byte NAVX_DEVICE_UPDATE_RATE_HZ = 50;
    public static final double PITCH_OF_MOUNTAIN = 27.5;
    public static final double TOLERANCE_DEGREES = 2.0;
    public static final double TOLERANCE_LEVEL_1 = 11;
    public static final double TOLERANCE_LEVEL_2 = 6;
    public static final double TOLERANCE_LEVEL_3 = .5;
    public static final double SONAR_TOLERANCE = .5;

    //Hanging values
    public static final double HANG_INIT = .95;
    public static final double HANG_NOW = 0;
    public static final double HANG_HALFWAY = .196;

    //Dump values,
    public static final double DUMP_INIT = .95;
    public static final double DUMP_DOWN = .35;

    //Clamp Left values, TODO get real values
    public static final double CLAMP_LEFT_INIT = 1;
    public static final double CLAMP_LEFT_DOWN = .45;

    //Clamp Right values,
    public static final double CLAMP_RIGHT_INIT = .25;
    public static final double CLAMP_RIGHT_DOWN = .80;

    //Hopper Left TODO confirm with real servos
    public static final double HL_STORE = 1;
    public static final double HL_DUMP = .85;

    //Hopper Right TODO confirm with real servos
    public static final double HR_STORE = 1- HL_STORE; //TO BE FIXED;
    public static final double HR_DUMP = 1 - HL_DUMP;

    //Collector power
    public static final double COLLECTOR = .5;

    //Color Sensor Thresholds
    public static final int COLOR_RED_RED = 100;
    public static final int COLOR_BLUE_RED = 50;
    public static final int COLOR_GREEN_RED = 50;
    public static final int COLOR_RED_BLUE = 50;
    public static final int COLOR_GREEN_BLUE = 50;
    public static final int COLOR_BLUE_BLUE = 100;
    public static final int COLOR_RED_WHITE = 250;
    public static final int COLOR_BLUE_WHITE = 250;
    public static final int COLOR_GREEN_WHITE = 250;

    //Misc
    public static final double MAX_SPEED = .86;
    public static final double WHEEL_DIAMETER = 6.5;








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
    public static final String score = "score";
    public static final double SCORE_SCORING = .5;
    public static final double SCORE_CLOSE = .5;
    public static final double CLIMBER_HALFWAY = .5;

}
