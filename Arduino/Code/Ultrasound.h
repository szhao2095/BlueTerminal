#ifndef Ultrasound_H
#define Ultrasound_H

#include "RTC.h"
#include "MicroSD.h"

extern RtcDateTime last_reading;
extern const int trigPin;         
extern const int echoPin;

long ultrasoundTakeReading();
long microsecondsToInches(long microseconds);
long microsecondsToCentimeters(long microseconds);
void save_reading(double reading, double inches, double centimeters);

#endif
