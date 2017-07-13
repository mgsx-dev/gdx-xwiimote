package net.mgsx.wiimote;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;

import net.mgsx.wiimote.Wiimote.Keys;
import net.mgsx.wiimote.Wiimote.WiimoteDevice;

public class WiimoteDeviceGUI extends Table
{
	private WiimoteDevice device;
	private Label accelLabel;
	private IntMap<Label> keyLabels = new IntMap<Label>();
	
	public WiimoteDeviceGUI(Skin skin, final WiimoteDevice device) {
		super(skin);
		this.device = device;
		add("Device"); add(device.getName()).row();
		add("Accel"); add(accelLabel = new Label("", skin)).row();
		
		Wiimote.Keys keys[] = Keys.values();
		for(Wiimote.Keys key : keys)
			if(key != Wiimote.Keys.XWII_KEY_NUM)
				createKeyLabel(key);
		
		final TextButton bt = new TextButton("", getSkin(), "toggle");
		add("Rumble"); add(bt).row();;
		
		
		bt.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				device.toggleMotor(bt.isChecked());
			}
		});
		
		add("Leds");
		for(int i=0 ; i<4 ; i++){
			final int ledIndex = i;
			final TextButton btled = new TextButton("", getSkin(), "toggle");
			btled.setChecked(device.getLed(ledIndex));
			add(btled);
			btled.addListener(new ChangeListener() {
				
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					device.toggleLed(ledIndex, btled.isChecked());
				}
			});
		}
		row();
	}
	
	private void createKeyLabel(Wiimote.Keys key) {
		Label label = new Label("", getSkin());
		keyLabels.put(key.ordinal(), label);
		add(key.name()); add(label).row();
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		accelLabel.setText(String.valueOf(device.ax));
		
		for(Entry<Label> entry : keyLabels){
			
			entry.value.setText(device.isKeyPressed(entry.key) ? "X" : "-");
		}
		
		
	}
	
	
	
}
