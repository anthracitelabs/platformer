package com.anthracitelabs.game.map;

import com.anthracitelabs.game.Constants;
import com.anthracitelabs.game.objects.GameObject;
import com.anthracitelabs.game.objects.MovingPlatform;
import com.anthracitelabs.game.objects.NPC;
import com.anthracitelabs.game.objects.Player;
import com.anthracitelabs.game.objects.Projectile;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import com.anthracitelabs.game.world.GameWorld;


public class TiledMapManager {

    final static public int EMPTY_TILE = 0x00;
    final static public int BLOCK_TILE = 0x01;
    final static public int ONE_WAY_TILE = 0x02;
    final static public int INVALID_TILE = 0xff;

    private TmxMapLoader mMapLoader;
    private TiledMap mCurrentTiledMap;

    private int mMapWidthPixels, mMapHeightPixels, mMapWidthTiles, mMapHeightTiles;
    private int[] mTilesCollision;

    private Array<GameObject> mActiveGameObjects;
    private Array<Projectile> mActiveProjectiles;
    private TextureAtlas mTextureAtlas;
    private GameWorld mGameWorld;

    public TiledMapManager(Array<GameObject> activeGameObjects, Array<Projectile> activeProjectiles, TextureAtlas textureAtlas, GameWorld world) {
        mActiveGameObjects = activeGameObjects;
        mActiveProjectiles = activeProjectiles;
        mTextureAtlas = textureAtlas;
        mGameWorld = world;

        mMapLoader = new TmxMapLoader();
    }

    public void init(String mapFileName) {
        mCurrentTiledMap = mMapLoader.load(mapFileName);

        mMapWidthPixels = ((TiledMapTileLayer)mCurrentTiledMap.getLayers().get(0)).getWidth() * Constants.TILE_SIZE;
        mMapHeightPixels = ((TiledMapTileLayer)mCurrentTiledMap.getLayers().get(0)).getHeight() * Constants.TILE_SIZE;
        mMapWidthTiles = ((TiledMapTileLayer)mCurrentTiledMap.getLayers().get(0)).getWidth();
        mMapHeightTiles = ((TiledMapTileLayer)mCurrentTiledMap.getLayers().get(0)).getHeight();

        mTilesCollision = new int[mMapWidthTiles*mMapHeightTiles];

        prepareMapArray();
    }

    private void prepareMapArray()
    {
        TiledMapTileLayer baseLayer = (TiledMapTileLayer)mCurrentTiledMap.getLayers().get("static");
        TiledMapTileLayer objectsLayer = (TiledMapTileLayer)mCurrentTiledMap.getLayers().get("objects");

        TiledMapTileLayer.Cell cell;

        for (int i = 0; i < mMapHeightTiles; i++)
        {
            for (int j = 0; j < mMapWidthTiles; j++)
            {
                int x = j;
                int y = mMapHeightTiles - 1 - i;

                cell = baseLayer.getCell(x, y);
                if (cell != null) {
                    Object property = cell.getTile().getProperties().get("tile");
                    if (property != null) {
                        if (property.equals("block")) {
                            mTilesCollision[i * mMapWidthTiles + j] = BLOCK_TILE;
                        }
                        else if (property.equals("one_way")) {
                            mTilesCollision[i * mMapWidthTiles + j] = ONE_WAY_TILE;
                        }
                        else {
                            mTilesCollision[i * mMapWidthTiles + j] = EMPTY_TILE;
                        }
                    }
                }
                else {
                    mTilesCollision[i * mMapWidthTiles + j] = EMPTY_TILE;
                }

                cell = objectsLayer.getCell(x, y);
                if (cell != null) {
                    Object property = cell.getTile().getProperties().get("object");
                    if (property != null) {

                        Vector2 position = new Vector2(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE);
                        Vector2 halfSize = new Vector2(Integer.parseInt(cell.getTile().getProperties().get("size_x").toString()), Integer.parseInt(cell.getTile().getProperties().get("size_y").toString()));

                        if (property.toString().compareTo("player") == 0) {
                            Player player = new Player(mTextureAtlas, this, mGameWorld);
                            player.init(position, halfSize);

                            mActiveGameObjects.add(player);
                        }
                        else if (property.toString().compareTo("npc") == 0) {
                            NPC npc = new NPC();
                            npc.init(position, halfSize);
                            mActiveGameObjects.add(npc);
                        }
                        else if (property.toString().compareTo("moving_platform") == 0) {
                            MovingPlatform movingPlatform = new MovingPlatform(mTextureAtlas,this, mGameWorld);
                            movingPlatform.init(position, halfSize);
                            mActiveGameObjects.add(movingPlatform);
                        }
                    }
                }
            }
        }
    }

    public TiledMap getCurrentTiledMap() {
        return mCurrentTiledMap;
    }

    public int GetMapTileYAtPoint(float y)
    {
        return (int)(y / (float)(Constants.TILE_SIZE));
    }

    public int GetMapTileXAtPoint(float x)
    {
        if (x < 0)
            return -1;

        return (int)(x / (float)(Constants.TILE_SIZE));
    }
    /*
    public int GetMapTileAtPointX(float x)
    {
        return (int)(((x < 0 && x < Constants.TILE_SIZE)? -1 * Constants.TILE_SIZE : x) / (float)(Constants.TILE_SIZE));
    }
    public int GetMapTileAtPointY(float y)
    {
        return (int)(y / (float)(Constants.TILE_SIZE));
    }*/

    public boolean IsObstacle(int x, int y)
    {
        if (x < 0 || x >= mMapWidthTiles
                || y < 0 || y >= mMapHeightTiles)
            return true;

        return (GetTileSafe(x, y) == BLOCK_TILE);
    }

    public int GetTileSafe(int i, int j )
    {
        if ( i >= 0 && i < mMapWidthTiles && j >= 0 && j < mMapHeightTiles )
        {
            return mTilesCollision[(mMapWidthTiles * (mMapHeightTiles - 1)) - (j * mMapWidthTiles) + i];
        }
        else
        {
            return INVALID_TILE;
        }
    }

    public boolean IsOneWayPlatform(int x, int y)
    {
        if (x < 0 || x >= mMapWidthTiles
                || y < 0 || y >= mMapHeightTiles)
            return false;

        return (GetTileSafe(x, y) == ONE_WAY_TILE);
    }

    public boolean IsGround(int x, int y)
    {
        if (x < 0 || x >= mMapWidthTiles
                || y < 0 || y >= mMapHeightTiles)
            return false;

        return (GetTileSafe(x, y) == ONE_WAY_TILE || GetTileSafe(x, y) == BLOCK_TILE);
    }

    public boolean IsEmpty(int x, int y)
    {
        if (x < 0 || x >= mMapWidthTiles
                || y < 0 || y >= mMapHeightTiles)
            return false;

        return (GetTileSafe(x, y) == EMPTY_TILE);
    }
}
