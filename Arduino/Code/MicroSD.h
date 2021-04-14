#ifndef MicroSD_H
#define MicroSD_H

#include <SD.h>
#include "RTC.h"

extern const int chip_select;
extern RtcDateTime last_file;
extern const int FILE_DELAY;

void init_MicroSD();

String getDateStringFilename(const RtcDateTime& dt);
void printDirectory(File dir, int numTabs);

#endif
