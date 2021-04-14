#include "Ultrasound.h"

long ultrasoundTakeReading() {
  pinMode(trigPin, OUTPUT);
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  pinMode(echoPin, INPUT);
  return pulseIn(echoPin, HIGH);
}

long microsecondsToInches(long microseconds) {
  return microseconds / 74 / 2;
}

long microsecondsToCentimeters(long microseconds) {
  return microseconds / 29 / 2;
}

void save_reading(double reading, double inches, double centimeters) {
  // If we exceed current period of data collection, make new data file
  if ((Rtc.GetDateTime().Epoch32Time() - last_file.Epoch32Time()) > FILE_DELAY) {
    last_file = Rtc.GetDateTime();
    String data_filename = getDateStringFilename(last_file);

    Serial.print("Creating data file for current period (");
    Serial.print(data_filename);
    Serial.println(")");
    File temp = SD.open(data_filename, FILE_WRITE);
    if (temp) {
      temp.close();
    }
  }

  // Get data file for current period of data collection
  File data_file = SD.open(getDateStringFilename(last_file), FILE_WRITE);
  if (data_file) {
    data_file.print(last_reading.Epoch32Time());
    data_file.print(", ");
    data_file.print(reading);
    data_file.print(", ");
    data_file.print(inches);
    data_file.print(", ");
    data_file.println(centimeters);

    data_file.close();
  }
}
