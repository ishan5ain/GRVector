# GRVector
----------


This is a personal project which aims to test the possibilities of human interaction using an Android Wear device. Smartwatches are becoming the next technological extension to humans after smartphones, which is still in development and has not been explored to it's full potential yet. 
As of now, only Game Rotation Vector is being used to send sensor data over to handheld device. It is a virtual sensor which makes use of the physical Accelerometer and Gyroscope sensors to compute the rotation vector values of the device, which can be further used to figure out the orientation of our wearable device. 

----
####What works?
The Rotation Vector values received on the handheld device are computed to get the Euler Rotation Angles around each of the three axis. These values are then mapped to a 3D cube which rotates in the virtual world just as the smartwatch rotates in physical.

----
####What doesn't work?
As Euler angles are to calculate the values of rotation which results in a well-known problem called the Gimbal Lock Problem. So, as a result Cube loses one of its axis when the device is rotated in such a way that two of the three axis are driven into parallel configuration.

----

####How to solve the Gimbal Lock Problem?
Quaternions can be implemented instead of Euler Angles for evading the Gimbal Lock Problem. But, in order to use Quaternions either the functionality of converting Rotation Vector to Quaternions has to be entirely written in OpenGL which the projects presently use to render the 3D Cube or, the entire 3D scene has to be built using Unity 3D, as it already contains the functions needed use quaternions for orientation. Using Unity 3D is a better option as it gives more freedom to create any kind of 3D scene and even the possibility of building a game.

----
####Tested on
**Motorola 360** *paired with* **OnePlus 2**
