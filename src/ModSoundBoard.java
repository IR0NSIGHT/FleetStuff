import api.ModPlayground;
import api.common.GameClient;
import api.common.GameServer;
import com.bulletphysics.linearmath.Transform;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.schema.game.client.data.GameClientState;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.PlayerCharacter;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.schine.network.RegisteredClientOnServer;
import org.schema.schine.network.client.ClientControllerInterface;
import org.schema.schine.network.client.ClientState;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 03.10.2020
 * TIME: 15:41
 */
public class ModSoundBoard {
    /**
     * Will play (queue) sound for player at position of ship.
     * @param player Target client which will hear the sound
     * @param soundSource SimpleTransformableSendableObject that will be the source of the sound
     * @param soundName soundName
     * @return true if successfull, false if not
     */
    public static boolean PlaySound(PlayerState player, SimpleTransformableSendableObject soundSource, String soundName) {
        ModPlayground.broadcastMessage("playing sound");
        try {
            com.bulletphysics.linearmath.Transform t; //define transform which will be filled with the entities transform

            if (soundSource.getType() == SimpleTransformableSendableObject.EntityType.ASTRONAUT) {
                t = ((PlayerCharacter) soundSource).getHeadWorldTransform(); //returns the astronauts transform
                ModPlayground.broadcastMessage(t.origin.toString());
            } else {
                t =new com.bulletphysics.linearmath.Transform(); //create new empty transform
                t.setIdentity(); //dont know what that does
                ((SegmentController) soundSource).getPhysicsObject().getWorldTransform(t); //assign transform to be the ships transform
            }


            ModPlayground.broadcastMessage("transform origin: " + t.origin.toString());
            //TODO get the client of the given player instead of playing on all clients
            //Int2ObjectMap<RegisteredClientOnServer> clientList = GameServer.getServerState().getClients();
            //RegisteredClientOnServer client = clientList.get( player.getClientId());
            //get ships state, cast to clientstate
            GameClientState clientState = GameClient.getClientState();
            if (clientState == null ) {
                ModPlayground.broadcastMessage("no game client found");
                return false;
            }
            ModPlayground.broadcastMessage("clientstate of player " + clientState.getPlayer().getName());
            ClientControllerInterface controller = ((ClientState)clientState).getController();
            ModPlayground.broadcastMessage("gotten controller");
            controller.queueTransformableAudio(soundName, t, 2, 100);
            ModPlayground.broadcastMessage("queued audio");
        } catch (Exception ex) {
            ModPlayground.broadcastMessage("failed playing sound" + ex.toString());
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
