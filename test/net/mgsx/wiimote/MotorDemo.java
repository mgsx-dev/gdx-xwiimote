package net.mgsx.wiimote;

import java.io.File;

import com.badlogic.gdx.math.MathUtils;

import net.mgsx.wiimote.Wiimote.WiimoteDevice;

public class MotorDemo {
	public static void main(String[] args) throws InterruptedException {
		System.load(new File("libs/linux64/libgdx-xwiimote64.so").getAbsolutePath());
		Wiimote.init();
		
		
		long mon = Wiimote.createMonitor();
		String s;
		String lastDevice = null;
		while(null != (s = Wiimote.nextDevice(mon))){
			System.out.println("from java : " + String.valueOf(s));
			lastDevice = s;
		}
			
		// Wiimote.enumerate(mon);
		Wiimote.releaseMonitor(mon);
		
		if(lastDevice != null){
			
			WiimoteDevice device = Wiimote.getConnectedDevices().peek();
			device.start();
			
			Thread.sleep(500); // required in order to call motors
			
			float phase = 0.5f; // 0 - 1
			float duration = 25f; // secs
			float frequency = 0.6f; // Hz : full period per second
			
			int delayOn = MathUtils.round(phase * 1000 / (2 * frequency));
			int delayOff = MathUtils.round((1 - phase) * 1000 / (2 * frequency));

			int count = MathUtils.round(1000 * duration / (delayOn + delayOff));
			
			float time = 0;
			for(int i=0 ; i<count ; i++){
				time += (delayOn + delayOff )/ 1000f;
				frequency = MathUtils.lerp(0.1f, 1, MathUtils.sinDeg(time * 360 / 2) * 0.5f + 0.5f);
				
				delayOn = MathUtils.round(phase * 1000 / (2 * frequency));
				delayOff = MathUtils.round((1 - phase) * 1000 / (2 * frequency));

				device.toggleMotor(true);
				Thread.sleep(delayOn);
				device.toggleMotor(false);
				Thread.sleep(delayOff);
			}
			
			device.stop();
		}
	}
}
