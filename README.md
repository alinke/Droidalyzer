Droidalyzer
===========

Droidalyzer

Follow these steps to compile the Droidalyzer code in Eclipse. 

1. Start with a working version of Eclipse with the Android SDK installed

2. Get and import the IOIO libraries into Eclipse from https://github.com/ytai/ioio/wiki/Downloads . It's recommended to use 
version 3.26 of the IOIO libraries but 3.x should also work. Do not use 4.x.

3. Get and import the Android Facebook SDk into Eclipse https://developers.facebook.com/android/ 
Note the original Droidalyzer code used the Android Facebook SDK 2.0 but 3.0 seems to work also.

4. Git clone or download the zip of this project and import this project into Eclipse, your screen should look like below. 
Note on the left you should have the following libraries: IOIOLibAndroid or IOIOLib depending on which version of 
the IOIO libraries you used, IOIOLibBT, IOIOLibAccessory, and Facebook SDK 
in addition to this project which will show up as AndroidBreathalyzer.

![alt tag](http://droidalyzer.com/files/droidalyzer-overall-eclipse.jpg)


5. Right click on the AndroidBreathalyzer project in Eclipse, choose "Properties", and then click "Android". 
Check the Target of "Google APIs" , platform 2.2, API level 8. IMPORTANT: If you select "Android 2.2" 
instead of the "Google APIs", the project won't compile as the projects needs the Google Maps API. You may need to also 
download the Android 2.2 API if you don't already have it, use the Android SDK manager to download.

6. Now from the same screen, add the three IOIO libraries and the facebook SDK. Your Eclipse screen should now look like this:

![alt tag](http://droidalyzer.com/files/droidalyzer-eclipse-properaties.jpg)

If all went well, you shouldn't have any Eclipse errors at this point. Do a quick test to make sure all is working and
then start with your modifications. Enjoy...
