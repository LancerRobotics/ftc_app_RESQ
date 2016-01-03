# ftc_app
This is our 2015-2016 FTC app, where all of our opmodes and code will be written, do NOT push to master unless approved

**************************************************************************************

# IMPORTANT UPDATE
To install fully on your own computer:
* Download navX-micro from http://www.pdocs.kauailabs.com/navx-mxp/software/android-library-ftc/ by clicking on _latest build_, it is in purple
MAKE SURE THERE IS A navX-micro FOLDER IN YOUR HOME DIRECTORY (C:\Users\YOURUSERNAME)
* Extract the contents and run through setup.exe
* Open your project in android studio
* Change the directory in the *FTC ROBOT CONTROLLER* build.gradle located in Gradle Scripts
  - When changing go to the repositories flat-dr placce
  - Then change the directory to yours, you should only be changing your username (where it says jakew)


**************************************************************************************

IMPORTANT RESOURCE (ftc forum): http://ftcforum.usfirst.org/forumdisplay.php?156-FTC-Technology

Information and Future Plans

* Autonomous Plans
 - Drive to button
 - Push button
 - Score climbers
 - Back up
 - Climb up mountain, possibly find a way to score climbers on the zipline
 
* TeleOp Updates
 - Treads teleop, movement only
 
* Sensors
 - navX-Micro --> to detect direction and open up the possibilities as to how we can use the raw, pitch, and roll values determined by the sensor. (VALUES: need to be tested)
 - Sonar --> (Maxbotix i2c/PWM/Analog Sonar) to detect distance, can be used in autonomous for more accurate movement (VALUES: need to be tested)
 - Motor Encoders --> to detect "ticks" or movement for the wheels, encoded movement in autonomous, move certain distances (VALUES: need to be tested) (Maybe 0-1400?)
 - ODS --> to detect distance in the Maxbotix's deadzone (VALUES: need to be tested)

Swerve Robotics' Library will possibly be used in the code (Thanks to Swerve Robotics)

**************************************************************************************

FTC Android Studio project to create FTC Robot Controller app.

This is the FTC SDK that can be used to create an FTC Robot Controller app, with custom op modes.
The FTC Robot Controller app is designed to work in conjunction with the FTC Driver Station app.
The FTC Driver Station app is available through Google Play.

To use this SDK, download/clone the entire project to your local computer.
Use Android Studio to import the folder  ("Import project (Eclipse ADT, Gradle, etc.)").

Documentation for the FTC SDK are included with this repository.  There is a subfolder called "doc" which contains several subfolders:

 * The folder "apk" contains the .apk files for the FTC Driver Station and FTC Robot Controller apps.
 * The folder "javadoc" contains the JavaDoc user documentation for the FTC SDK.
 * The folder "tutorial" contains PDF files that help teach the basics of using the FTC SDK.

For technical questions regarding the SDK, please visit the FTC Technology forum:

FTC FORUM: http://ftcforum.usfirst.org/forumdisplay.php?156-FTC-Technology

**************************************************************************************

Livingston Lancer Robotics
www.lancerrobotics.com
ftc3415@gmail.com
