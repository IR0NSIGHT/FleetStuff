/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 08.10.2020
 * TIME: 14:59
 */

import api.DebugFile;
import api.ModPlayground;
import api.listener.Listener;
import api.listener.events.entity.ShipJumpEngageEvent;
import api.mod.StarLoader;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.data.fleet.FleetMember;
import proguard.evaluation.Stack;

/**
 * a wrapper that logs info about the fleet members jump drive to allow cached and unchached ftl jumps
 */
public class FleetShip {
    public SegmentController getInternalEntity() {
        return internalEntity;
    }

    private SegmentController internalEntity;
    private String internalUID;
    private float rechargeTimeFTL = 0;
    private int rangeFTL = 0;
    private long lastTimeJumped = 0;
    /**
     * create a wrapper that logs all jump related info to use it when the ship is cached.
     * @param ship the fleet ship to create the wrapper for
     */
    public FleetShip(FleetMember ship) {
        try {
            internalEntity = ship.getLoaded();
            internalUID = ship.UID;
            UpdateStats();
            AddJumpListener();

        } catch (Exception e) {
            ModPlayground.broadcastMessage(e.toString());
        }

    }
    public void UpdateStats() {
        if (internalEntity != null) {
            try { //cast segmentcontroller into ship
                Ship s = (Ship) internalEntity;
                rangeFTL = s.getManagerContainer().getJumpAddOn().getDistance();
                rechargeTimeFTL = s.getManagerContainer().getJumpAddOn().getChargeRateFull(); //returns time to fully recharge in seconds
                DebugFile.log("fleetship was logged. " + internalEntity.getName() + " has ftl range " + rangeFTL + ", recharge rate " + rechargeTimeFTL);
            } catch (Exception e) {
                e.printStackTrace();
                DebugFile.log(e.toString());
            }
        } else {
            DebugFile.log("fleet ship unloaded");
        }
    }
    private void AddJumpListener() {
        DebugFile.log("adding jump listener to ship: " + internalEntity.getName());
        ModPlayground.broadcastMessage("adding jump listener to ship: " + internalEntity.getName());
        final FleetShip f = this;
        StarLoader.registerListener(ShipJumpEngageEvent.class, new Listener<ShipJumpEngageEvent>() {
            @Override
            public void onEvent(ShipJumpEngageEvent e) {
                ModPlayground.broadcastMessage("shipjumpevent: " + e.getController().getName());
                if (!e.server) {
                    return;
                }
                if (e.getController().getUniqueIdentifier().equals(f.internalUID)) {
                    f.lastTimeJumped = System.currentTimeMillis();
                    ModPlayground.broadcastMessage("fleetship jumped: " + e.getController().getName());
                    f.UpdateStats();
                }
            }
        });
    }
}
