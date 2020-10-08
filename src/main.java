import api.DebugFile;
import api.common.GameClient;
import api.common.GameServer;
import api.mod.StarMod;
import api.utils.StarRunnable;
import org.schema.schine.network.server.ServerState;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 01.10.2020
 * TIME: 15:47
 */
public class main extends StarMod {
    public static void main(String[] args) {

    }

    @Override
    public void onGameStart() {
        this.setModDescription("control asteroid generation");
        this.setModAuthor("IR0NSIGHT");
        this.setModName("roidControl");
        this.setModVersion("0.1");
        super.onGameStart();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        DebugFile.log("starting serverwait.",this);
        waitForServer();
    }

    /**
     * wait until a serverstate instance was created -- the server is done loading on this machine.
     * means that the init code will only be run on the server, bc it never fires for client.
     */
    private void waitForServer() {
        final StarMod m = this;
        new StarRunnable() {
            boolean log = true;
            @Override
            public void run() {
                if (log) {
                    log = false;
                    DebugFile.log("Starting loop to check for server.",m);
                }
                if (GameClient.getClientState() != null) {
                    init();
                    FleetEventHandler feh = new FleetEventHandler();
                    DebugFile.log("Server detected, intitializing mod.",m);
                    cancel();
                }
                if (ServerState.isShutdown() || ServerState.isFlagShutdown()) {
                    cancel();
                }
            }
        }.runTimer(25);
    }
    private void init() {
        new EventHandler();
    }
}
