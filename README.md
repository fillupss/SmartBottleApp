# Smart Bottle App

This project is a health application that monitors the user's water consumption. The data we will use to determine the water consumption levels comes from an analog signal from the Force Sensor Resistor, where the user places down their water contents, and processed by an Arduino Uno that will be sampled at a 9600 baud rate to digitalize the signal so that the Android app will receive the signal via Bluetooth. At the moment, some functions the App has is the user can choose three modes: Sicko, Workout, and Normal mode. Essentially these modes are created so that depending on how the user feels during that day, he/she can choose the mode and the modes determine how frequently the user should be drinking water. If the app notices the user has not been drinking water, then there will be an alarm sound to notify the user to drink water.

We plan to update the application along with the device, where there will be a hibernation mode such that the app can run in the background and the Ardunio Uno does not have to transmit data every second. Also we look to add a function that returns the data back to the user on how much water they drink throughout the day and provide feedback. Also we would like to add a functionality to detect what kind water bottle the user uses so we can provide mre accurate data.

Click [here](https://sites.google.com/view/smartbottle/home) to check the website

