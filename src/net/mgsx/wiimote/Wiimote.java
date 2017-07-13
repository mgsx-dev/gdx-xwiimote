package net.mgsx.wiimote;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.utils.Array;

// see https://github.com/dvdhrm/xwiimote/blob/master/tools/xwiishow.c
// to know how to access device !
public class Wiimote {
	
	public static enum Keys {
		XWII_KEY_LEFT,
		XWII_KEY_RIGHT,
		XWII_KEY_UP,
		XWII_KEY_DOWN,
		XWII_KEY_A,
		XWII_KEY_B,
		XWII_KEY_PLUS,
		XWII_KEY_MINUS,
		XWII_KEY_HOME,
		XWII_KEY_ONE,
		XWII_KEY_TWO,
		XWII_KEY_X,
		XWII_KEY_Y,
		XWII_KEY_TL,
		XWII_KEY_TR,
		XWII_KEY_ZL,
		XWII_KEY_ZR,

		/**
		 * Left thumb button
		 *
		 * This is reported if the left analog stick is pressed. Not all analog
		 * sticks support this. The Wii-U Pro Controller is one of few devices
		 * that report this event.
		 */
		XWII_KEY_THUMBL,

		/**
		 * Right thumb button
		 *
		 * This is reported if the right analog stick is pressed. Not all analog
		 * sticks support this. The Wii-U Pro Controller is one of few devices
		 * that report this event.
		 */
		XWII_KEY_THUMBR,

		/**
		 * Extra C button
		 *
		 * This button is not part of the standard action pad but reported by
		 * extension controllers like the Nunchuk. It is supposed to extend the
		 * standard A and B buttons.
		 */
		XWII_KEY_C,

		/**
		 * Extra Z button
		 *
		 * This button is not part of the standard action pad but reported by
		 * extension controllers like the Nunchuk. It is supposed to extend the
		 * standard X and Y buttons.
		 */
		XWII_KEY_Z,

		/**
		 * Guitar Strum-bar-up event
		 *
		 * Emitted by guitars if the strum-bar is moved up.
		 */
		XWII_KEY_STRUM_BAR_UP,

		/**
		 * Guitar Strum-bar-down event
		 *
		 * Emitted by guitars if the strum-bar is moved down.
		 */
		XWII_KEY_STRUM_BAR_DOWN,

		/**
		 * Guitar Fret-Far-Up event
		 *
		 * Emitted by guitars if the upper-most fret-bar is pressed.
		 */
		XWII_KEY_FRET_FAR_UP,

		/**
		 * Guitar Fret-Up event
		 *
		 * Emitted by guitars if the second-upper fret-bar is pressed.
		 */
		XWII_KEY_FRET_UP,

		/**
		 * Guitar Fret-Mid event
		 *
		 * Emitted by guitars if the mid fret-bar is pressed.
		 */
		XWII_KEY_FRET_MID,

		/**
		 * Guitar Fret-Low event
		 *
		 * Emitted by guitars if the second-lowest fret-bar is pressed.
		 */
		XWII_KEY_FRET_LOW,

		/**
		 * Guitar Fret-Far-Low event
		 *
		 * Emitted by guitars if the lower-most fret-bar is pressed.
		 */
		XWII_KEY_FRET_FAR_LOW,

		/**
		 * Number of key identifiers
		 *
		 * This defines the number of available key-identifiers. It is not
		 * guaranteed to stay constant and may change when new identifiers are
		 * added. However, it will never shrink.
		 */
		XWII_KEY_NUM
	}
	
	public static class WiimoteDevice {
		public String path;
		public long ptr;
		public String type;
		public String extension;
		public int caps;
		public double ax;
		
		public int [] keys = new int[Keys.XWII_KEY_NUM.ordinal()];

		public String getName() {
			return type + " - " + extension;
		}
		
		public void start(WiimoteDevice device){
			ptr = createDevice(path);
			Wiimote.start(ptr);
		}
		public void stop(WiimoteDevice device){
			Wiimote.stop(ptr);
		}

		public boolean isKeyPressed(Keys key) {
			return isKeyPressed(key.ordinal());
		}
		public boolean isKeyPressed(int key) {
			return keys[key] != 0;
		}
		public void toggleMotor(boolean on){
			Wiimote.iface_rumble(ptr, on);
		}
		public void toggleLed(int led, boolean on){
			Wiimote.iface_set_led(ptr, led+1, on);
		}
		public boolean getLed(int led){
			return Wiimote.iface_get_led(ptr, led+1);
		}
	}
	
	
	
	
	public static void init(Application app){
		init();
		app.addLifecycleListener(new LifecycleListener() {
			
			@Override
			public void resume() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void pause() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void dispose() {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public static void update(){
		for(WiimoteDevice device : getConnectedDevices()) {
			
			device.ax = getAccelX();
			for(int i=0 ; i<Keys.XWII_KEY_NUM.ordinal() ; i++){
				device.keys[i] = getKey(i);
			}
			
		}
	}
	
	private static Array<WiimoteDevice> devices;
	
	public static Array<WiimoteDevice> getConnectedDevices(){
		
		if(devices == null) {
			devices = new Array<WiimoteDevice>();
			
			long m = createMonitor();
			for(;;){
				String path = nextDevice(m);
				if(path == null){
					break;
				}
				WiimoteDevice device = new WiimoteDevice();
				device.path = path;
				device.ptr = createDevice(path);
				device.type = getDevType(device.ptr);
				device.extension = getExtension(device.ptr);
				device.caps = available(device.ptr);
				
				releaseDevice(device.ptr);
				device.ptr = 0;
				
				// device.caps = available(ptr);
				devices.add(device);
			}
			releaseMonitor(m);
		}
		
		return devices;
	}
	
	public static interface Listener{
		public void onKey(int key);
	}
	
	static{
		// new SharedLibraryLoader().load("gdx-controllers-desktop");
	}
	
	/*JNI
	#include <stdio.h>
	#include <string.h>
	#include <xwiimote.h>
	#include <errno.h>
	#include <poll.h>
	
	#define print_error printf
	
	#define MAX_DEVICE 4
	
	
	static jclass eventCallbackClass;
	static jmethodID eventCallbackMethodID; 
	
	static volatile bool should_poll;
	
	static double accel_x = 0;
	
	struct device_cache {
		int keys[XWII_KEY_NUM];
	};
	
	static device_cache  * device_caches;
	
	*/
	
	static native void iface_rumble(long ptr, boolean on); /*
		xwii_iface * iface = (xwii_iface *)ptr;
		xwii_iface_rumble(iface, on);
	*/
	
	static native void iface_set_led(long ptr, int led, boolean on); /*
		xwii_iface * iface = (xwii_iface *)ptr;
		int err = xwii_iface_set_led(iface, XWII_LED(led), on);
		if(err != 0) {
			print_error("cannot change led\n");
		}
	*/

	static native boolean iface_get_led(long ptr, int led); /*
		xwii_iface * iface = (xwii_iface *)ptr;
		bool on = false;
		int err = xwii_iface_get_led(iface, XWII_LED(led), &on);
		if(err != 0) {
			print_error("cannot read led\n");
		}
		return on;
	*/

	public static native double getAccelX(); /*
		return accel_x;
	*/
	
	public static native String getDevType(long ptr); /*
		xwii_iface * iface = (xwii_iface *)ptr;
		char * devtype;
		xwii_iface_get_devtype(iface, &devtype);
		return env->NewStringUTF(devtype);
	*/
	
	public static native String getExtension(long ptr); /*
		xwii_iface * iface = (xwii_iface *)ptr;
		char * extension;
		xwii_iface_get_extension(iface, &extension);
		return env->NewStringUTF(extension);
	*/
	
	public static void onKey(int key, int state){
		System.out.println("CALLBACK KEY : " + key + " | " + state);
	}
	
	public static void start(final long ptr) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				pollDevice(ptr);
			}
		}).start();
	}
	
	public static native int getKey(int code); /*
		return device_caches->keys[code];
	*/
	
	public static native void stop(long ptr); /*
		should_poll = false;
	*/
	public static native void pollDevice(long ptr); /*
		xwii_iface * device = (xwii_iface *)ptr;
		int ret = 0;
		
		should_poll = true;
		
		ret = xwii_iface_open(device,
					      xwii_iface_available(device) | XWII_IFACE_WRITABLE);
					      
		if (ret)
		print_error("Error: Cannot open ifaces");
					      
		struct xwii_event event;
		
		int fds_num;
		struct pollfd fds[2];

printf("prepare\n");

		memset(fds, 0, sizeof(fds));
		fds[0].fd = 0;
		fds[0].events = POLLIN;
		fds[1].fd = xwii_iface_get_fd(device);
		fds[1].events = POLLIN;
		fds_num = 2;
		
		
		
		printf("watch\n");
		ret = xwii_iface_watch(device, true);
		if (ret)
		print_error("Error: Cannot initialize hotplug watch descriptor");
		
		
		while (should_poll) {
		
		//printf("poll\n");
		
			ret = poll(fds, fds_num, -1);
			
			if (ret < 0) {
				if (errno != EINTR) {
					ret = -errno;
					print_error("Error: Cannot poll fds");
					break;
				}
			}
			
			//printf("read queue\n");
			
			ret = xwii_iface_dispatch(device, &event, sizeof(event));
			if (ret) {
				if (ret != -EAGAIN) {
					print_error("Error: dispatch");
					break;
				}
			} else {
				// printf("dispatch\n");
				
				if(event.type == XWII_EVENT_ACCEL){
					// TODO ... send accel
					accel_x = event.v.abs[0].x;
				}else if(event.type == XWII_EVENT_KEY){
					device_caches[0].keys[event.v.key.code] = event.v.key.state;
					env->CallStaticVoidMethod(eventCallbackClass, eventCallbackMethodID, event.v.key.code, event.v.key.state);
				}
			}
			
			// break;
		}
		printf("closing\n");
		xwii_iface_close(device, XWII_IFACE_ALL);
					      
		
	*/
	
	public static native long createDevice(String name); /*
		int err;
		xwii_iface * device;
		err = xwii_iface_new(&device, name);
		return (long)device;
	*/
	
	public static native void releaseDevice(long ptr); /*
	xwii_iface * device = (xwii_iface *)ptr;
	xwii_iface_unref(device);
*/
	
	public static native int available(long ptr); /*
		xwii_iface * device = (xwii_iface *)ptr;
		return xwii_iface_available(device);
	*/
	
	public static native int battery(long ptr); /*
		int err;
		uint8_t value;
		xwii_iface * device = (xwii_iface *)ptr;
		err = xwii_iface_get_battery(device, &value);
		return (int)value;
	*/
	
	
	public static native void init (); /*
	
	device_caches = (device_cache*)malloc(MAX_DEVICE * sizeof(struct device_cache));
	memset(device_caches, 0, MAX_DEVICE * sizeof(struct device_cache));
	
	
	eventCallbackClass = env->FindClass("net/mgsx/wiimote/Wiimote");
	// if (!eventCallbackClass) throw exception("Java class not found"); 
	eventCallbackMethodID = env->GetStaticMethodID(eventCallbackClass, "onKey", "(II)V"); 
    // if (!eventCallbackMethodID) throw exception("Java method with appropriate signature not found");
	
//   	printf("hello xwiimote !\n");
//   
//   	struct xwii_monitor *mon;
//	char *ent;
//	int num = 0;
//
//	mon = xwii_monitor_new(false, false);
//	if (!mon) {
//		printf("Cannot create monitor\n");
//	}
//
//	while ((ent = xwii_monitor_poll(mon))) {
//		printf("  Found device #%d: %s\n", ++num, ent);
//		free(ent);
//	}
//
//	xwii_monitor_unref(mon);
//   
//   	printf("bye xwiimote !\n");
*/
	
	public static native int countDevices (); /*
	   	printf("hello xwiimote !\n");
	   
	   	struct xwii_monitor *mon;
		char *ent;
		int num = 0;
	
		mon = xwii_monitor_new(false, false);
		if (!mon) {
			printf("Cannot create monitor\n");
		}
	
		while ((ent = xwii_monitor_poll(mon))) {
			printf("  Found device #%d: %s\n", ++num, ent);
			free(ent);
		}
	
		xwii_monitor_unref(mon);
	   
	   	return num;
	*/
	
	public static native String nextDevice(long ptr); /*
		struct xwii_monitor *mon = (xwii_monitor *)ptr;
		char *ent = xwii_monitor_poll(mon);
		return env->NewStringUTF(ent);
	*/
	
	public static native long createMonitor(); /*
		struct xwii_monitor *mon;
		mon = xwii_monitor_new(false, false);
		return (jlong)mon;
	*/
	public static native void releaseMonitor(long ptr); /*
		struct xwii_monitor *mon = (xwii_monitor *)ptr;
		xwii_monitor_unref(mon);
	*/
	
	
	public static native void enumerate(long ptr); /*
		struct xwii_monitor *mon = (xwii_monitor *)ptr;
		char *ent;
		int num = 0;
	
		mon = xwii_monitor_new(false, false);
		if (!mon) {
			printf("Cannot create monitor\n");
		}
	
		while ((ent = xwii_monitor_poll(mon))) {
			printf("  Found device #%d: %s\n", ++num, ent);
			free(ent);
		}
	*/
	
}
