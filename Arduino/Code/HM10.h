#ifndef HM10_H
#define HM10_H

#include <Arduino.h>
#include "RTC.h"

extern String inData;
extern const String PASSWORD;
extern RtcDateTime last_active;

void init_HM10();
void process_command();

String getValue(String data, char separator, int index);

#endif
