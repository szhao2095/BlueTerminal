#include "RTC.h"

void init_RTC() {
  Rtc.Begin();
  RtcDateTime compiled = RtcDateTime(__DATE__, __TIME__);
  if (!Rtc.IsDateTimeValid()) {
    if (Rtc.LastError() != 0) {
      // We have a communications error
      // see https://www.arduino.cc/en/Reference/WireEndTransmission for
      // what the number means
      Serial.print("RTC communications error = ");
      Serial.println(Rtc.LastError());
    } else {
      Serial.println("RTC lost confidence in the DateTime!");
      Rtc.SetDateTime(compiled);
    }
  }
  if (!Rtc.GetIsRunning()) {
    Serial.println("RTC was not actively running, starting now");
    Rtc.SetIsRunning(true);
  }
  RtcDateTime now = Rtc.GetDateTime();
  if (now < compiled)  {
    Serial.println("RTC is older than compile time!  (Updating DateTime)");
    Rtc.SetDateTime(compiled);
  }

  Rtc.Enable32kHzPin(false);
  Rtc.SetSquareWavePin(DS3231SquareWavePin_ModeAlarmTwo);

  // Alarm 1 set to trigger every day when
  // the hours, minutes, and seconds match
  //    RtcDateTime alarmTime = now + 88; // into the future
  //    DS3231AlarmOne alarm1(
  //            alarmTime.Day(),
  //            alarmTime.Hour(),
  //            alarmTime.Minute(),
  //            alarmTime.Second(),
  //            DS3231AlarmOneControl_HoursMinutesSecondsMatch);
  //    Rtc.SetAlarmOne(alarm1);

  // Alarm 2 set to trigger at the top of the minute
  DS3231AlarmTwo alarm2(
    0,
    0,
    0,
    DS3231AlarmTwoControl_OncePerMinute);
  Rtc.SetAlarmTwo(alarm2);

  // Throw away any old alarm state before we ran
  Rtc.LatchAlarmsTriggeredFlags();
}

#define countof(a) (sizeof(a) / sizeof(a[0]))
void printDateTime(const RtcDateTime& dt) {
  char datestring[20];
  snprintf_P(datestring,
             countof(datestring),
             PSTR("%02u/%02u/%04u %02u:%02u:%02u"),
             dt.Month(),
             dt.Day(),
             dt.Year(),
             dt.Hour(),
             dt.Minute(),
             dt.Second() );
  Serial.print(datestring);
}

void print_time() {
  if (!Rtc.IsDateTimeValid()) {
    if (Rtc.LastError() != 0) {
      Serial.print("RTC communications error = ");
      Serial.println(Rtc.LastError());
    } else {
      Serial.println("RTC lost confidence in the DateTime!");
    }
  }
  if ((Rtc.GetDateTime().Epoch32Time() - last_print.Epoch32Time()) > 1) {
    printDateTime(Rtc.GetDateTime());
    Serial.println();
    last_print = Rtc.GetDateTime();
  }
}
