package net.mgsx.wiimote;

import java.io.IOException;

public class Test {
	private static boolean shouldRun = true;
	public static void main(String[] args) {
		System.load("/home/germain/git/gdx-xwiimote/libs/linux64/libgdx-xwiimote64.so");
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
			final long dev = Wiimote.createDevice(lastDevice);
			int caps = Wiimote.available(dev);
			System.out.println("caps : " + caps);
			int bat = Wiimote.battery(dev);
			System.out.println("bats : " + bat);
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Wiimote.pollDevice(dev); 
					Wiimote.releaseDevice(dev);
				}}).start();
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while(shouldRun){
						System.out.println(Wiimote.getAccelX());
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}}).start();
			
			// TODO open last device and get some status
		
			try {
				System.in.read();
				Wiimote.stop(dev);
				shouldRun = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
	}
}
