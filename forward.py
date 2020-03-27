#!/usr/bin/env python 

#import library ros 
import rospy 
import time


from geometry_msgs.msg import Twist
from std_msgs.msg import String 
from std_msgs.msg import Empty 
from ardrone_autonomy.msg import Navdata




COMMAND_PERIOD = 1000


class AutonomousFlight():
    def __init__(self):
        self.status = ""
        rospy.init_node('forward', anonymous=False)
        self.rate = rospy.Rate(10)
        self.pubTakeoff = rospy.Publisher("ardrone/takeoff",Empty, queue_size=10)
        self.pubLand = rospy.Publisher("ardrone/land",Empty, queue_size=10)
        self.pubCommand = rospy.Publisher('cmd_vel',Twist, queue_size=10)
        self.command = Twist()
        #self.commandTimer = rospy.Timer(rospy.Duration(COMMAND_PERIOD/1000.0),self.SendCommand)
        self.state_change_time = rospy.Time.now()    
        rospy.on_shutdown(self.SendLand)

    def SendTakeOff(self):
        self.pubTakeoff.publish(Empty()) 
        self.rate.sleep()
                
    def SendLand(self):
        self.pubLand.publish(Empty())
    
        
    def SetCommand(self, linear_x, linear_y, linear_z, angular_x, angular_y, angular_z):
        self.command.linear.x = linear_x
        self.command.linear.y = linear_y
        self.command.linear.z = linear_z
        self.command.angular.x = angular_x
        self.command.angular.y = angular_y
        self.command.angular.z = angular_z
        self.pubCommand.publish(self.command)
        self.rate.sleep()

if __name__ == '__main__': 
    try: 
        i = 0
        drone= AutonomousFlight()
            
        while not rospy.is_shutdown():
            drone.SendTakeOff()
            if i <= 30 :
                drone.SetCommand(0,0,1,0,0,0)
                i+=1
            elif i<=60 :
                drone.SetCommand(1,0,0,0,0,0)
                i+=1
	    elif i<=63:
		drone.SetCommand(0,0,0,0,1,0)
                i+=1
            elif i<=93:
                drone.SetCommand(0,1,0,0,0,0)
                i+=1
            elif i<=96:
                drone.SetCommand(0,0,0,-1,0,0)
                i+=1
            elif i<=126:
                drone.SetCommand(-1,0,0,0,0,0)
                i+=1
	    elif i<=129:
		drone.SetCommand(0,0,0,0,-1,0)
                i+=1
            elif i<=159:
                drone.SetCommand(0,-1,0,0,0,0)
                i+=1
            else:
                drone.SetCommand(0,0,0,0,0,0)
         
    except rospy.ROSInterruptException:
        pass

