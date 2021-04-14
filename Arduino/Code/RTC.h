#ifndef RTC_H
#define RTC_H

#include <Wire.h> 
#include <RtcDS3231.h>

extern RtcDS3231<TwoWire> Rtc;
extern RtcDateTime last_print;

void init_RTC();

void printDateTime(const RtcDateTime& dt);
void print_time();

#endif
