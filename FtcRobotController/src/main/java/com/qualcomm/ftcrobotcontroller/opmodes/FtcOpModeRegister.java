/* Copyright (c) 2014, 2015 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.ftcrobotcontroller.opmodes.deprecated.CameraTestOp;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousBlueCameraCodesFromClosePos;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousBlueCameraCodesFromFarPos;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousBlueClimbersFromClosePos;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousBlueClimbersFromFarPos;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousBlueClimbersWithDelayFromClosePos;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousBlueClimbersWithDelayFromFarPos;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousBlueParkingZoneOnlyFromClosePos;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousBlueParkingZoneOnlyFromClosePosWithDelay;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousBlueParkingZoneOnlyFromFarPos;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousBlueParkingZoneOnlyFromFarPosWithDelay;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousRedCameraCodesFromClosePos;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousRedCameraCodeFromFarPos;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousRedClimbersFromClosePos;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousRedClimbersFromFarPos;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousRedClimbersWithDelayFromClosePos;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousRedClimbersWithDelayFromFarPos;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousRedParkingZoneOnlyFromClosePos;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousRedParkingZoneOnlyFromClosePosWithDelay;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousRedParkingZoneOnlyFromFarPos;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonomousRedParkingZoneOnlyFromFarPosWithDelay;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.AutonDefense;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.CameraTestOpWorlds;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.JudgesWorlds;
import com.qualcomm.ftcrobotcontroller.opmodes.worlds.Teleop;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegister;

/**
 * Register Op Modes
 */
public class FtcOpModeRegister implements OpModeRegister {

  /**
   * The Op Mode Manager will call this method when it wants a list of all
   * available op modes. Add your op mode to the list to enable it.
   *
   * @param manager op mode manager
   */
  public void register(OpModeManager manager) {

    /*
     * register your op modes here.
     * The first parameter is the name of the op mode
     * The second parameter is the op mode class property
     *
     * If two or more op modes are registered with the same name, the app will display an error.
     */
    manager.register("Teleop", Teleop.class);
    manager.register("Autonomous Blue Main (BOTH VERSIONS) Far Pos", AutonomousBlueCameraCodesFromFarPos.class);
    manager.register("Autonomous Blue Main (BOTH VERSIONS) Close Pos", AutonomousBlueCameraCodesFromClosePos.class);
    manager.register("Autonomous Red Main (BOTH VERSIONS) Far Pos", AutonomousRedCameraCodeFromFarPos.class);
    manager.register("Autonomous Red Main (BOTH VERSIONS) Close Pos", AutonomousRedCameraCodesFromClosePos.class);
    manager.register("Autonomous Blue Climbers Only WITHOUT Delay Far Pos", AutonomousBlueClimbersFromFarPos.class);
    manager.register("Autonomous Blue Climbers Only WITHOUT Delay Close Pos", AutonomousBlueClimbersFromClosePos.class);
    manager.register("Autonomous Red Climbers Only WITHOUT Delay Far Pos", AutonomousRedClimbersFromFarPos.class);
    manager.register("Autonomous Red Climbers Only WITHOUT Delay Close Pos", AutonomousRedClimbersFromClosePos.class);
    manager.register("Autonomous Blue Climbers Only WITH Delay Far Pos", AutonomousBlueClimbersWithDelayFromFarPos.class);
    manager.register("Autonomous Blue Climbers Only WITH Delay Close Pos", AutonomousBlueClimbersWithDelayFromClosePos.class);
    manager.register("Autonomous Red Climbers Only WITH Delay Far Pos", AutonomousRedClimbersWithDelayFromFarPos.class);
    manager.register("Autonomous Red Climbers Only WITH Delay Close Pos", AutonomousRedClimbersWithDelayFromClosePos.class);
    manager.register("Autonomous Blue Parking Zone Only WITH Delay Far Pos", AutonomousBlueParkingZoneOnlyFromFarPosWithDelay.class);
    manager.register("Autonomous Blue Parking Zone Only WITH Delay Close Pos", AutonomousBlueParkingZoneOnlyFromClosePosWithDelay.class);
    manager.register("Autonomous Red Parking Zone Only WITH Delay Far Pos", AutonomousRedParkingZoneOnlyFromFarPosWithDelay.class);
    manager.register("Autonomous Red Parking Zone Only WITH Delay Close Pos", AutonomousRedParkingZoneOnlyFromClosePosWithDelay.class);
    manager.register("Autonomous Blue Parking Zone Only WITHOUT Delay Far Pos", AutonomousBlueParkingZoneOnlyFromFarPos.class);
    manager.register("Autonomous Blue Parking Zone Only WITHOUT Delay Close Pos", AutonomousBlueParkingZoneOnlyFromClosePos.class);
    manager.register("Autonomous Red Parking Zone Only WITHOUT Delay Far Pos", AutonomousRedParkingZoneOnlyFromFarPos.class);
    manager.register("Autonomous Red Parking Zone Only WITHOUT Delay Close Pos", AutonomousRedParkingZoneOnlyFromClosePos.class);
    manager.register("Worlds CameraTestOp", CameraTestOpWorlds.class);
    manager.register("reg camera test", CameraTestOp.class);
    manager.register("Judges Code", JudgesWorlds.class);
    manager.register("Defense",AutonDefense.class);
  }
}
