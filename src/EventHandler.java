import api.DebugFile;
import api.ModPlayground;
import api.common.GameClient;
import api.common.GameServer;
import api.listener.Listener;
import api.listener.events.entity.SegmentControllerInstantiateEvent;
import api.listener.events.player.PlayerChatEvent;
import api.mod.StarLoader;
import com.bulletphysics.linearmath.Transform;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.rudp.impl.Segment;
import org.luaj.vm2.ast.Str;
import org.newdawn.slick.Game;
import org.schema.game.client.controller.GameClientController;
import org.schema.game.client.data.GameClientState;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.PlayerCharacter;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.world.Sector;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.game.mod.Mod;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.network.RegisteredClientOnServer;
import org.schema.schine.network.client.ClientController;
import org.schema.schine.network.client.ClientControllerInterface;
import org.schema.schine.network.client.ClientState;
import org.schema.schine.sound.pcode.SoundManager;
import org.schema.schine.sound.pcode.SoundPool;

import java.io.File;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 01.10.2020
 * TIME: 16:15
 */
public class EventHandler {
    public EventHandler() {
        //create the pirate attack sound by loading it from the audio resources folder
        String path = "data/audio-resource/Actions/banditAttack01.ogg";
        File f = new File(path);
        if (!f.exists()) {
            ModPlayground.broadcastMessage("file does not exist!");
            return;
        }
        ModPlayground.broadcastMessage("adding sound, then playing");
        Controller.getAudioManager().addSound("outlander",f);
        //!create pirate sound
        StarLoader.registerListener(PlayerChatEvent.class, new Listener<PlayerChatEvent>() {
            @Override
            public void onEvent(PlayerChatEvent e) {
                if (!e.onServer()) {
                    return;
                }
                Sector sector = null;
                PlayerState player = null;
                SimpleTransformableSendableObject stso = null;
                try {
                    sector = GameServer.getServerState().getUniverse().getSector(GameServer.getServerState().getPlayerFromNameIgnoreCaseWOException(e.getMessage().sender).getCurrentSector());
                    player = GameServer.getServerState().getPlayerFromName(e.getMessage().sender);
                    stso =  player.getFirstControlledTransformable();
                } catch (Exception ex) {
                    ModPlayground.broadcastMessage("failed getting sector");
                    DebugFile.log("failed getting sector");
                }

                if (sector == null) {return;};
                ModPlayground.broadcastMessage("sent from " + sector.pos);
                ModPlayground.broadcastMessage("SC type: " + stso.getTypeString());

                if (e.getText().equals("roid")) {
                  //  AsteroidSpawner.spawnRandomAsteroid(sector);
                }
                if (e.getText().equals("list")) {
                    String path = "data/audio-resource/Actions/banditAttack01.ogg";
                    File f = new File(path);
                    if (!f.exists()) {
                        ModPlayground.broadcastMessage("file does not exist!");
                        return;
                    }
                    ModPlayground.broadcastMessage("adding sound, then playing");
                    Controller.getAudioManager().addSound("outlander",f);
                    ModSoundBoard.PlaySound(player,stso,"outlander");
                }
                if (e.getText().equals("bandit")) {
                    ModSoundBoard.PlaySound(player,stso,"banditAttack01");
                }
                if (e.getText().equals("sound")) {
                    ModPlayground.broadcastMessage("trying to play sound");
                    ModSoundBoard.PlaySound(player,stso,"0022_spaceship enemy - hit medium explosion medium enemy ship blow up");
                }
            }
        });
        StarLoader.registerListener(SegmentControllerInstantiateEvent.class, new Listener<SegmentControllerInstantiateEvent>() {
            @Override
            public void onEvent(SegmentControllerInstantiateEvent e) {
                try {
                    SegmentController sc = e.getController();
                    if(!sc.isDocked() && sc.getFactionId() == -1) {
                        ModPlayground.broadcastMessage("pirates!");
                        //its a pirate ship
                        //get player, send him a warning message
                        PlayerState player = GameClient.getClientPlayerState();
                        SimpleTransformableSendableObject stso = player.getFirstControlledTransformable(); //player object/ship
                        ModSoundBoard.PlaySound(player,stso,"outlander");
                    }
                } catch (Exception ex) {
                    ModPlayground.broadcastMessage("pirate sound failed");
                    ModPlayground.broadcastMessage(ex.toString());
                }

            }
        });
    }
}
