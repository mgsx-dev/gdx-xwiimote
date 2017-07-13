package net.mgsx.wiimote;

// see https://github.com/dvdhrm/xwiimote/blob/master/tools/xwiishow.c
// to know how to access device !
public class Wiimote {
	
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
	
	
	static jclass eventCallbackClass;
	static jmethodID eventCallbackMethodID; 
	
	static volatile bool should_poll;
	
	static double accel_x = 0;
	
	*/
	
	public static native double getAccelX(); /*
		return accel_x;
	*/
	
	public static void onKey(int key, int state){
		System.out.println("CALLBACK KEY : " + key + " | " + state);
	}
	public static native void stop(long ptr); /*
		should_poll = false;
	*/
	public static native void pollDevice(long ptr); /*
		xwii_iface * device = (xwii_iface *)ptr;
		int ret = 0;
		
		should_poll = true;
		
		ret = xwii_iface_open(device,
					      xwii_iface_available(device));// | XWII_IFACE_WRITABLE);
					      
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
