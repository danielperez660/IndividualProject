# Individual Project - Office Beacon System
Initial setup of individual project
Project will consist of a beacon system which will allow the Elders office to see who is currently in the office at the time.

There will be an interaction with a mobile android applciation which will have a hook with the slack server and will change the status of the user depenending on wether they are in the office or not. 

If initial interaction between the phone and the beacon is set up then there will be an attempt to interact with the time logging website to log how long the worker has been in the office for.

## Log
### 02-03-20
* added programatical image addition and dragAndDrop features for the beacon vectors added

### 01-03-20
* After hours and hours on end I found the issue! The error originated in the SVG image i was converting to a vector image. Will attempt to find better source for SVG image next time...

### 22-02-20
* Tried to modify how the image is set so that its SVG and not PNG. Failed miserably.

### 20-02-20
* Fixed info retrieval
* Changed some database shit
* added images for in/out of office
* changed around home and status. replaced status for beaconManager

### 18-02-20
* Made changes to database to allow us to collect and insert databases
* Changed the status fragment to represent beacons we registered (TEMP)

### 17-02-20
* Changes to naming convention
* Creation of activity for registration of beacons
* Removed region persistance and changed the UI slightly
* Did some stuff for a local database for registered beacons 

### 12-02-20
* Set up inter process communication between beacon detection service
* Data type communication needs to be specific

### 10-02-20
* Fixed issues with androidX where app would crash on startup for no reason
* Modularised more with fragments rather than activities as menu items
* changed beacon detector to a service, need to create a range one

### 06-11-19
* I can now detect beacons and am very proud of myself. Prototype for basic beacon entry has been created

### 30-10-19
* Research into the use of androidstudio
* changes to the android application which allows it to use bluetooth

### 22-10-19
* Github repository setup
* Research into previous projects using beacons
