# -*- coding: utf-8 -*-
"""
Created on Sat Mar 23 01:08:15 2019

@author: tanma
"""

from imageai.Detection import VideoObjectDetection
import os

execution_path = os.getcwd()

detector = VideoObjectDetection()
detector.setModelTypeAsYOLOv3()
detector.setModelPath( os.path.join(execution_path , "yolo.h5"))
detector.loadModel()

video_path = detector.detectObjectsFromVideo(input_file_path=os.path.join( execution_path, "Humans.mp4"),
                                output_file_path=os.path.join(execution_path, "Humans_Detected_1")
                                , frames_per_second=12, log_progress=True)
for eachObject in video_path:
    print(eachObject["name"] , " : " , eachObject["percentage_probability"] )