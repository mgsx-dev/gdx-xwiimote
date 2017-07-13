package net.mgsx.wiimote;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;

import net.mgsx.wiimote.Wiimote.Keys;
import net.mgsx.wiimote.Wiimote.WiimoteDevice;

public class WiimoteDeviceGUI extends Table
{
	private WiimoteDevice device;
	private Label accelLabel;
	private IntMap<Label> keyLabels = new IntMap<Label>();
	
	public WiimoteDeviceGUI(Skin skin, WiimoteDevice device) {
		super(skin);
		this.device = device;
		add("Device"); add(device.getName()).row();
		add("Accel"); add(accelLabel = new Label("", skin)).row();
		
		Wiimote.Keys keys[] = Keys.values(); /*{
				Keys.XWII_KEY_UP,
				Keys.XWII_KEY_LEFT,
				Keys.XWII_KEY_RIGHT};*/
		for(Wiimote.Keys key : keys)
			if(key != Wiimote.Keys.XWII_KEY_NUM)
				createKeyLabel(key);
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
