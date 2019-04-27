package com.anthracitelabs.game;

import com.anthracitelabs.game.data.GameData;
import com.anthracitelabs.game.screen.GamePlayScreen;
import com.anthracitelabs.game.screen.LevelScreen;
import com.anthracitelabs.game.screen.MainMenuScreen;
import com.anthracitelabs.game.screen.UIScreen;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class MyGdxGame extends Game {

    private UIScreen[] mScreens;
    private GameData mGameData;

    public TextureAtlas mTextureAtlas;

	public void initialize() {

		Skin skin = new Skin(Gdx.files.internal(Constants.SKIN_FILE)) {
			//Override json loader to process FreeType fonts from skin JSON
			@Override
			protected Json getJsonLoader(final FileHandle skinFile) {
				Json json = super.getJsonLoader(skinFile);
				final Skin skin = this;

				json.setSerializer(FreeTypeFontGenerator.class, new Json.ReadOnlySerializer<FreeTypeFontGenerator>() {
					@Override
					public FreeTypeFontGenerator read(Json json,
													  JsonValue jsonData, Class type) {
						String path = json.readValue("font", String.class, jsonData);
						jsonData.remove("font");

						FreeTypeFontGenerator.Hinting hinting = FreeTypeFontGenerator.Hinting.valueOf(json.readValue("hinting",
								String.class, "AutoMedium", jsonData));
						jsonData.remove("hinting");

						Texture.TextureFilter minFilter = Texture.TextureFilter.valueOf(
								json.readValue("minFilter", String.class, "Nearest", jsonData));
						jsonData.remove("minFilter");

						Texture.TextureFilter magFilter = Texture.TextureFilter.valueOf(
								json.readValue("magFilter", String.class, "Nearest", jsonData));
						jsonData.remove("magFilter");

						FreeTypeFontGenerator.FreeTypeFontParameter parameter = json.readValue(FreeTypeFontGenerator.FreeTypeFontParameter.class, jsonData);
						parameter.hinting = hinting;
						parameter.minFilter = minFilter;
						parameter.magFilter = magFilter;
						FreeTypeFontGenerator generator = new FreeTypeFontGenerator(skinFile.parent().child(path));
						BitmapFont font = generator.generateFont(parameter);
						skin.add(jsonData.name, font);
						if (parameter.incremental) {
							generator.dispose();
							return null;
						} else {
							return generator;
						}
					}
				});

				return json;
			}
		};

		mGameData = new GameData();

		AssetManager mAssetManager = new AssetManager();
		mAssetManager.load("images/characters.pack.atlas", TextureAtlas.class);
		mAssetManager.finishLoading();
		mTextureAtlas = mAssetManager.get("images/characters.pack.atlas");

		mScreens = new UIScreen[]{
		        new MainMenuScreen(this, skin),
				new GamePlayScreen(this, skin),
				new LevelScreen(this, skin)
        };
	}

	@Override
	public void create () {
		initialize();
	    setScreen(Constants.MAIN_MENU_SCREEN);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {

	}

	public void setScreen(int screenIndex) {
		setScreen(mScreens[screenIndex]);
	}

	public GameData getGameData() {
	    return mGameData;
    }
}
