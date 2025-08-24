package com.anthracitelabs.game.data;

public class Level {
    private String mName, mMapFileName;
    private boolean mLocked;

    public Level(String name, String mapFileName, boolean locked) {
        this.mName = name;
        this.mMapFileName = mapFileName;
        this.mLocked = locked;
    }

    public String getName() {
        return mName;
    }

    public String getMapFileName() {
        return mMapFileName;
    }

    public boolean getLocked() {
        return mLocked;
    }

    public void setName(String val) {
        mName = val;
    }

    public void setMapFileName(String val) {
        mMapFileName = val;
    }

    public void setName(boolean val) {
        mLocked = val;
    }
}
