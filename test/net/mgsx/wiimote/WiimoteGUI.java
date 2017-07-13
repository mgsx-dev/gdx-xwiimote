package net.mgsx.wiimote;

import java.io.File;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.mgsx.wiimote.Wiimote.WiimoteDevice;

public class WiimoteGUI extends ApplicationAdapter
{
	public static void main(String[] args) {
		// TODO change this !
		System.load(new File("libs/linux64/libgdx-xwiimote64.so").getAbsolutePath());
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 1000;
		LwjglApplicationConfiguration.disableAudio = true;
		ApplicationListener app = new WiimoteGUI();
		Application application = new LwjglApplication(app, config);
		Wiimote.init(application);
	}
	
	private Skin skin;
	private Stage stage;
	
	@Override
	public void create() {
		skin = new Skin(Gdx.files.classpath("skins/uiskin.json"));
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		
		Array<WiimoteDevice> devices = Wiimote.getConnectedDevices();
		Table t = new Table(skin);
		for(WiimoteDevice device : devices) {
			device.start(device);
			t.add(new WiimoteDeviceGUI(skin, device));
		}
		t.setFillParent(true);
		stage.addActor(t);
	}
	
	@Override
	public void render() {
		Wiimote.update();
		stage.act();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
	}
	

}
