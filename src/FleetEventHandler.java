import api.DebugFile;
import api.ModPlayground;
import api.common.GameServer;
import api.listener.Listener;
import api.listener.events.controller.fleet.FleetEvent;
import api.listener.events.entity.SegmentControllerOverheatEvent;
import api.listener.events.fleet.FleetAttackedEvent;
import api.listener.events.player.PlayerChatEvent;
import api.mod.StarLoader;
import api.utils.StarRunnable;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.newdawn.slick.Game;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.fleet.Fleet;
import org.schema.game.common.data.fleet.FleetMember;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.faction.Faction;
import org.schema.game.common.data.player.faction.FactionPermission;
import org.schema.game.common.data.player.faction.FactionRelation;
import org.schema.game.common.data.world.Sector;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.game.server.data.GameServerState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 07.10.2020
 * TIME: 18:29
 */
public class FleetEventHandler {
    public FleetEventHandler() {
        DebugFile.log("fleet EH was created");
        CreateListeners();
    }
    private void CreateListeners() {
        DebugFile.log("fleet EH has created its listeners");
        FleetAttacked();
        FleetOverheat();
        DebugCommand();
        //FleetLoop fl = new FleetLoop();
      // for (Fleet fleet : GameServer.getServerState().getFleetManager().)
    }

    private void FleetOverheat() {
        DebugFile.log("created fleet overheat event");
        StarLoader.registerListener(SegmentControllerOverheatEvent.class, new Listener<SegmentControllerOverheatEvent>() {
            @Override
            public void onEvent(SegmentControllerOverheatEvent e) {
                Fleet f = e.getEntity().getFleet();
                if (f == null) {
                    ModPlayground.broadcastMessage("fleet is null");
                    return;
                }
                if(f.isNPCFleet()) {
                    ModPlayground.broadcastMessage("npc fleet");
                    return;
                }
                ModPlayground.broadcastMessage("fleet of destroyed ship: " + f.getName());
                int factionID = 0;
                for (FleetMember fm: f.getMembers()) {

                    Faction faction = GameServer.getServerState().getFactionManager().getFaction(fm.getFactionId());
                    if (faction != null) {
                        factionID = fm.getFactionId();
                        break;
                    };
                }
                if (factionID == 0) {
                    ModPlayground.broadcastMessage("could not find valid faction for any member");
                    return;
                }
                FleetEventHandler.SendFactionMembers(factionID,"[Fleet]" + f.getName(),"OWN SHIP DESTROYED","the fleet has lost the ship " + e.getEntity().getName() + " at " + e.getEntity().getSector(new Vector3i()).toString() + " to attacker " + e.getLastDamager().getShootingEntity().getName() + " of " + e.getLastDamager().getShootingEntity().getFaction().getName());
            }
        });
    }
    private void FleetAttacked() {
        StarLoader.registerListener(FleetAttackedEvent.class, new Listener<FleetAttackedEvent>() {
            @Override
            public void onEvent(FleetAttackedEvent e) {
                DebugFile.log("fleet EH has detected fleetattack event");
                //get faction whos fleet was attacked
                int factionID = e.getFleet().getFlagShip().getFactionId();
                ModPlayground.broadcastMessage("faction " + factionID + " was attacked");
                //send a f4 mail to all faction members with the details of the attack
                //get all members
                List<Faction> factionsPresent = new ArrayList<Faction>();
                String enemiesNearby = "";

                Vector3i sector = e.getMemberSC().getSector(new Vector3i());
                DebugFile.log("sector is " + sector.toString());
                //get enemy ships in sector
                try {
                    Sector s = GameServer.getServerState().getUniverse().getSector(sector);
                    //collect all factions that have a ship/entity in the sector

                    for(SimpleTransformableSendableObject ship: s.getEntities()) {
                        DebugFile.log("ship in sector " + ship.getName());
                        if (!factionsPresent.contains(ship.getFaction())) {
                            factionsPresent.add(ship.getFaction());
                        }
                    }
                    for (Faction f: factionsPresent) {
                        FactionRelation.RType r = f.getRelationshipWithFactionOrPlayer(factionID);
                        if (r == FactionRelation.RType.ENEMY) {
                            ModPlayground.broadcastMessage("enemy faction near fleet: " + f.getName());
                            enemiesNearby += "-" + f.getName();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    DebugFile.log(ex.toString());
                }
                //! get enemy factions in sector
                //send message to all faction members
                SendFactionMembers(factionID, "[Fleet] " + e.getFleet().getName(),"ATTACK","fleet is under attack at: " + sector.toString()+ ", enemies nearby: " + enemiesNearby);
                DebugFile.log("sent fleetattacked f4-mail to faction " + GameServer.getServerState().getFactionManager().getFaction(factionID).getName());
            }
        });
    }
    private void DebugCommand() {
        DebugFile.log("chatcommands registered");
        StarLoader.registerListener(PlayerChatEvent.class, new Listener<PlayerChatEvent>() {
            @Override
            public void onEvent(PlayerChatEvent e) {
                if (!e.onServer()) {
                    return;
                }
                try {
                    //get player
                    PlayerState player = GameServer.getServerState().getPlayerFromNameIgnoreCaseWOException(e.getMessage().sender);
                    //get ship play is in
                    SimpleTransformableSendableObject ship = player.getFirstControlledTransformableWOExc(); //can also be astronaut!
                    //get faction of player
                    Faction faction = GameServer.getServerState().getFactionManager().getFaction(player.getFactionId());

                    if (e.getText().equals("fleetjump")) {
                        DebugFile.log("fleetjump command registered for player " + player.getName());
                        //get the fleet
                        //make them jump to their current move destination
                        //Object2ObjectOpenHashMap<String, LongOpenHashSet> fleetsByOwnerLowerCase = GameServer.getServerState().getFleetManager().fleetsByOwnerLowerCase;
                        //LongOpenHashSet fleets = fleetsByOwnerLowerCase.get(player.getName()); //fleets owned by this player, set of dbID of each fleet
                        Long2ObjectOpenHashMap<Fleet> fleetCache = GameServer.getServerState().getFleetManager().fleetCache;
                        ModPlayground.broadcastMessage("fleetjump ordered for player " + player.getName());
                        List<Fleet> playerFleets = new ArrayList<>();
                        for (Fleet fleet: fleetCache.values()) {
                            if (fleet.getOwner().equals(player.getName())) {
                                playerFleets.add(fleet);
                                DebugFile.log("ordering " + fleet.getName() + " to jump.");
                                FleetJump.JumpTo(fleet,new Vector3i(2,2,2),true);
                            }
                        }

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    ModPlayground.broadcastMessage(ex.toString());
                }

            }
        });
    }
    public static void SendFactionMembers(int factionID, String from, String topic, String message) {
        try {
            Faction faction = GameServer.getServerState().getFactionManager().getFaction(factionID);
            if (faction == null) {
                ModPlayground.broadcastMessage("faction is null");
                return;
            }
            if (faction.isNPC()) {
                ModPlayground.broadcastMessage("is npc faction " + faction.getName());
                return;
            }
            if (faction.getMembersUID().entrySet().size() == 0) {
                ModPlayground.broadcastMessage("no players in faction " + factionID);
                return;
            }
            for (Map.Entry<String, FactionPermission> playerEntry: faction.getMembersUID().entrySet())
            {
                DebugFile.log("member is " + playerEntry.getKey() + " - " + playerEntry.getValue());
                String player = playerEntry.getKey();
                GameServer.getServerState().getServerPlayerMessager().send(from,player,topic,message);
                DebugFile.log("sent f4 mail to player " + player + ": " + message);
            };
        } catch (Exception e) {
            e.printStackTrace();
            DebugFile.log(e.toString());
        }
    }
}
