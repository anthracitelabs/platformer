package com.anthracitelabs.game.render;

import com.anthracitelabs.game.Constants;
import com.anthracitelabs.game.objects.GameObject;
import com.anthracitelabs.game.objects.Player;
import com.anthracitelabs.game.objects.Projectile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;


public class GameRenderer {
    private SpriteBatch mBatch;
    private OrthogonalTiledMapRenderer mMapRenderer;
    private OrthographicCamera mCamera;
    private TiledMap mTiledMap;

    private final float mScreenWidthTileCount = 17.78f;
    private float mScreenHeightTileCount;
    private float mUnitPixels;

    private Array<GameObject> mActiveGameObjects;
    private Array<Projectile> mActiveProjectiles;

    private Player mPlayer;

    public GameRenderer(SpriteBatch batch, Array<GameObject> activeGameObjects, Array<Projectile> activeProjectiles) {
        mBatch = batch;
        mCamera = new OrthographicCamera();
        mActiveGameObjects = activeGameObjects;
        mActiveProjectiles = activeProjectiles;

        float tileSize = Gdx.graphics.getWidth() / mScreenWidthTileCount;
        mScreenHeightTileCount = Gdx.graphics.getHeight() / tileSize;
        mUnitPixels = 1 / 16f;

        System.out.println(mScreenWidthTileCount + "x" + mScreenHeightTileCount);
    }

    public void init(TiledMap map) {
        mTiledMap = map;
        mMapRenderer = new OrthogonalTiledMapRenderer(mTiledMap, mUnitPixels, mBatch);

        for (GameObject o : mActiveGameObjects) {
            if (o instanceof Player) {
                mPlayer = (Player)o;
                break;
            }
        }

        mCamera.setToOrtho(false, mScreenWidthTileCount, mScreenHeightTileCount);
        mCamera.update();
    }

    public void render(float delta) {
        cameraUpdate();

        mMapRenderer.setView(mCamera);

        mBatch.begin();

        for (MapLayer layer : mTiledMap.getLayers()) {
            if (layer.isVisible()) {
                if (layer.getName().equals("objects")) {
                    for(GameObject o : mActiveGameObjects) {
                        o.render(mBatch, delta);
                    }
                }
                else {
                    mMapRenderer.renderTileLayer((TiledMapTileLayer) layer);
                }
            }
        }

        mBatch.end();
    }

    public void cameraUpdate() {

        Vector3 position = mCamera.position;

        // first try placing the player in the center
        position.x = mCamera.position.x + (mPlayer.mPosition.x / Constants.TILE_SIZE - mCamera.position.x);
        position.y = mCamera.position.y + (mPlayer.mPosition.y / Constants.TILE_SIZE - mCamera.position.y);

        // check map bounds, adjust camera not to escape map boundaries
        if (mCamera.position.x - mMapRenderer.getViewBounds().getWidth()/2 < 0) {
            mCamera.position.x = mMapRenderer.getViewBounds().getWidth()/2;
        }
        else if (mCamera.position.x + mMapRenderer.getViewBounds().getWidth()/2 > ((TiledMapTileLayer)mTiledMap.getLayers().get(0)).getWidth() * Constants.TILE_SIZE) {
            mCamera.position.x = ((TiledMapTileLayer)mTiledMap.getLayers().get(0)).getWidth() * Constants.TILE_SIZE - mMapRenderer.getViewBounds().getWidth()/2;
        }
        if (mCamera.position.y - mMapRenderer.getViewBounds().getHeight()/2 < 0) {
            mCamera.position.y = mMapRenderer.getViewBounds().getHeight()/2;
        }
        else if (mCamera.position.y + mMapRenderer.getViewBounds().getHeight()/2 > ((TiledMapTileLayer)mTiledMap.getLayers().get(0)).getHeight() * Constants.TILE_SIZE) {
            mCamera.position.y = ((TiledMapTileLayer)mTiledMap.getLayers().get(0)).getHeight() * Constants.TILE_SIZE - mMapRenderer.getViewBounds().getHeight()/2;
        }

        //mCamera.position.set(position);
        mCamera.position.x = position.x;
        mCamera.position.y = position.y;
        mCamera.update();
    }
}
