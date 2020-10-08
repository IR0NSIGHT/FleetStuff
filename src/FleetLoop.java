import api.DebugFile;
import api.common.GameServer;
import api.utils.StarRunnable;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.fleet.Fleet;
import org.schema.game.common.data.fleet.FleetMember;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 07.10.2020
 * TIME: 23:13
 */
public class FleetLoop {
    final Fleet fleet;
    public FleetLoop(Fleet fleet) {
        this.fleet = fleet;
        DebugFile.log("FleetLoop created");
        new StarRunnable() {
            @Override
            public void run() {
                Update();
            }
        }.runTimer(25);
    }
    private void Update() {
        CountMembers();
    }

    private HashMap<String, SegmentController> members = new HashMap<String, SegmentController>();
    /**
     * check if any members changed
     */
    private void CountMembers() {
        List<SegmentController> membersNow = new ArrayList<>();
        for (FleetMember m: fleet.getMembers()) {
            //each member now
            if (!members.containsKey(m.UID)) {
                //old list does not contain current member -> member gained
                members.put(m.UID,m.getLoaded());
            }
            membersNow.add(m.getLoaded());
        }
        for (Map.Entry<String,SegmentController> entry: members.entrySet()) {
            //each member in saved list
            if (!membersNow.contains(entry.getValue())) {
                //member was lost since last time
                MemberLost(entry.getValue());
            }
        }
    }
    private void MemberLost(SegmentController member) {

    }
}
