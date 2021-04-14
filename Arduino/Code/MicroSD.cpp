#include "MicroSD.h"

void init_MicroSD() {
  // see if the card is present and can be initialized:
  if (!SD.begin(chip_select)) {
    Serial.println("Card failed, or not present");
    // don't do anything more:
    while (1);
  }
  
  // Creating file for data
  last_file = Rtc.GetDateTime();
  String data_filename = getDateStringFilename(last_file);
  if (SD.exists(data_filename)) {
    Serial.print("Data file for current period already exists (");
    Serial.print(data_filename);
    Serial.println(")");
  } else {
    Serial.print("Creating data file for current period (");
    Serial.print(data_filename.c_str());
    Serial.println(")");
    File temp = SD.open(data_filename.c_str(), FILE_WRITE);
    if (temp) {
      Serial.println("test");
      temp.close();
    }
    if (SD.exists(data_filename)) {
      Serial.print("Data file created (");
      Serial.print(data_filename);
      Serial.println(")");
    } else {
      Serial.print("File creation failed (");
      Serial.print(data_filename);
      Serial.println(")");
    }
  }
}

#define countof(a) (sizeof(a) / sizeof(a[0]))
String getDateStringFilename(const RtcDateTime& dt) {
  char temp[13];
  snprintf_P(temp,
             countof(temp),
             PSTR("%02u%02u%04u.txt"),
             dt.Month(),
             dt.Day(),
             dt.Year());
  String datestring(temp);
  return datestring;
}

void printDirectory(File dir, int numTabs) {
  while (true) {
    File entry =  dir.openNextFile();
    if (!entry) {  // No more files
      break;
    }
    Serial.println(entry.name());   // Full caps since 8.3 file system, doesnt matter still works
    Serial2.println(entry.name());
    entry.close();
  }
}
