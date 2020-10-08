import api.DebugFile;
import api.ModPlayground;
import api.common.GameServer;
import api.utils.StarRunnable;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.fleet.Fleet;
import org.schema.game.common.data.fleet.FleetMember;
import org.schema.game.mod.Mod;
import org.schema.game.server.controller.GameServerController;
import org.schema.game.server.controller.SectorSwitch;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 08.10.2020
 * TIME: 13:36
 */
public class FleetJump {
    public static void JumpTo(Fleet fleet, Vector3i sector, boolean together) {
        //create wrappers for each member with important info of jump
        //list member with timeToRechargeFTL; lastTimeJumped; UID; Fleetmember
        //wait until each member is able to perform a jump
        DebugFile.log("Fleet " + fleet.getName() + "received order to jump to " + sector.toString());
        //get flagship jump info
        int i = 0;
        for (FleetMember m: fleet.getMembers()) {
            i += 4000;
            FleetShip s = new FleetShip(m);
            if (s.getInternalEntity() != null) { //ship is not unloaded, use standard FTL
                final SegmentController sc = s.getInternalEntity();

                final SectorSwitch sw = GameServer.getServerState().getController().queueSectorSwitch(s.getInternalEntity(), sector, 0, true);
                if (sw != null) {
                    sw.delay = System.currentTimeMillis() + i;
                    long delaySeconds = i/1000 - 1;
                    new StarRunnable() {
                        @Override
                        public void run() {
                            sc.getNetworkObject().graphicsEffectModifier.add((byte) 1);
                            ModPlayground.broadcastMessage("playing effect");
                        }
                    }.runLater(25*delaySeconds); //run at same time of jump, 1 second prior
                    sw.executionGraphicsEffect = (byte) 2;
                    sw.keepJumpBasisWithJumpPos = true;
                }
                ModPlayground.broadcastMessage("init. jump for " + s.getInternalEntity().getName());
                ModPlayground.broadcastMessage("jump delay set to" + sw.delay + " should be: " + i);
            } else {
                ModPlayground.broadcastMessage("fleetship is unloaded: " + m.name);
            }

        }
    }
    private static void delayJump() {

    }
}

