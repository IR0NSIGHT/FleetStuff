import api.DebugFile;
import api.ModPlayground;
import org.schema.game.common.controller.FloatingRock;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.world.Sector;
import org.schema.game.server.data.GameServerState;
import org.schema.game.server.data.ServerConfig;

import java.util.Random;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 01.10.2020
 * TIME: 16:17
 */
public class AsteroidSpawner {
    private static int maxSize = 64;
    private static int i = 0;
    public static void spawnRandomAsteroid(Sector sector) {
        ModPlayground.broadcastMessage("spawning random rock");
        //get sector
        GameServerState state = GameServerState.instance;

        Random randomAsteroidLocal = new Random(sector.getSeed());
        long seed = randomAsteroidLocal.nextLong(); //get seed for roid from random
        int maxSize = (Integer) ServerConfig.ASTEROID_RADIUS_MAX.getCurrentState(); //get maximum size from serverconfig
        maxSize = AsteroidSpawner.maxSize;
        AsteroidSpawner.maxSize *= 2;
        int sizeX = maxSize;
        int sizeY = maxSize;
        int sizeZ = maxSize;
        DebugFile.log("maxSize: " + maxSize + " " +
                "size : " + sizeX +"" + sizeY +"" + sizeZ + " " +
                "seed: " + seed);
        try {
      //      sector.addRandomRock(state, seed, sizeX,sizeY,sizeZ,randomAsteroidLocal,AsteroidSpawner.in);
            AsteroidSpawner.i ++;
        } catch (Exception e) {
            e.printStackTrace();
            ModPlayground.broadcastMessage("failed: " + e.toString());
            DebugFile.log(e.toString());
        }

    }
}
