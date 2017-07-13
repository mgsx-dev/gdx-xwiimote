** This is a Work In Progress project **

# How to use

* required linux with hid-wiimote driver (included since 3.1 kernel) : `modprobe hid-wiimote`
* require xwiimote runtime and dev dependencies : `sudo apt-get install xwiimote xwiimote-dev`
* require user added to input group
* require bluetooth hardware
* require some Nintendo hardware (wiimote, ...etc) paired (just press 1+2 buttons when connecting)
* GCC/G++ compiler toolchain installed

to run GUI : `./gradlew runGUI`