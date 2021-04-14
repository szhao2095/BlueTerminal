//////////////////
//  DS3231 RTC  //
//////////////////
//#include <Wire.h> 
//#include <RtcDS3231.h>
#include "RTC.h"
#define RtcSquareWavePin 3        // Interrupt pin for RTC, D3 -> INT1
RtcDS3231<TwoWire> Rtc(Wire);

/////////////////
//  HM-10 BLE  //
/////////////////
// Serial2 for Mega2560
// BT_RX -> D_PIN 16
// BT_TX -> D_PIN 17
#include "HM10.h"
#define HM10TXPin 2               // Interrupt pin for HM10, D2 -> INT0
#define HM10_sleep_retries 5      // Number of times to retry sending sleep command to HM10 
String inData = "";

/////////////////////////
//  Ultrasound sensor  //
/////////////////////////
#include "Ultrasound.h"
const int trigPin = 7;            // Trigger Pin of Ultrasonic Sensor
const int echoPin = 6;            // Echo Pin of Ultrasonic Sensor
#define sensor_delay 60           // Time delay between each sensor reading
RtcDateTime last_reading;         // Used to find out when to take next reading

//////////////////////
//  MicroSD Reader  //
//////////////////////
// SD MISO to D_PIN 50
// SD MOSI to D_PIN 51
// SD SCK to D_PIN 5200
// SD CS to D_PIN 53
#include "MicroSD.h"
const int chip_select = 53;       // CS pin for MicroSD card module
const int FILE_DELAY = 86400;     // Setting to new datafile each day
RtcDateTime last_file;            // Used to find out when to create next datafile

///////////////
//  General  //
///////////////
#include <avr/sleep.h>
volatile bool interuptFlag = false;   // Used to find out who triggered interrupt
#define sleep_delay 300           // Time delay after inactivity before entering sleep mode
RtcDateTime last_active;          // Used to find out when to sleep

const String PASSWORD = "123456"; // Password for mobile app communications
RtcDateTime last_print;           // Used to print out timestamp every second, non critical

void setup () {
  Serial.begin(9600);   

  // Prepping interrupt pins
  pinMode(HM10TXPin, INPUT_PULLUP);
  pinMode(RtcSquareWavePin, INPUT_PULLUP);

  init_HM10();
  init_RTC();
  init_MicroSD();

  last_active = Rtc.GetDateTime();
  last_print = Rtc.GetDateTime();
}

void loop ()  {
  print_time();

  // Process commands for HM10
  process_command();

  // Checks last reading time for sensor reading
  if ((Rtc.GetDateTime().Epoch32Time() - last_reading.Epoch32Time()) > sensor_delay) { 
    // Take new reading
    double reading = ultrasoundTakeReading();
    double inches = microsecondsToInches(reading);
    double centimeters = microsecondsToCentimeters(reading);

    last_reading = Rtc.GetDateTime();

    printDateTime(last_reading);
    Serial.println();
    Serial.print("Sensor reading: ");
    Serial.print(reading);
    Serial.print(", inches: ");
    Serial.print(inches);
    Serial.print(", centimeters: ");
    Serial.println(centimeters);

    // Save reading to file
    save_reading(reading, inches, centimeters);

    last_active = Rtc.GetDateTime();
  }

  // Checks last active time for sleep
  if ((Rtc.GetDateTime().Epoch32Time() - last_active.Epoch32Time()) > sleep_delay) {
    DS3231AlarmFlag flag = Rtc.LatchAlarmsTriggeredFlags();

    // Going to sleep, execution recovers here
    HM10_To_Sleep();
    Mega2560_To_Sleep();

    if (interuptFlag) {
      interuptFlag = false;

      // Retrieve alarm bit and check for alarm firing
      DS3231AlarmFlag flag = Rtc.LatchAlarmsTriggeredFlags();
      if (flag & DS3231AlarmFlag_Alarm1) {
        Serial.println("Alarm One triggered");
      } else if (flag & DS3231AlarmFlag_Alarm2) {
        Serial.println("Alarm Two triggered");
      } else {
        Serial.println("Bluetooth triggered");
      }
    }
    last_active = Rtc.GetDateTime();
  }
}

void HM10_To_Sleep() {
  int counter = 0;
  String response = Serial2.readString();

  // Disconnect any connected user
  while (response != "OK" && counter < HM10_sleep_retries) {
    Serial2.write("AT");
    delay(500);
    response = Serial2.readString();
    counter++;
  }
  counter = 0;

  // Put HM10 to sleep
  while (response != "OK+SLEEP" && counter < HM10_sleep_retries) {
    Serial2.write("AT+SLEEP");
    delay(500);
    response = Serial2.readString();
    counter++;
  }
}


void Mega2560_To_Sleep() {
  Serial.print("Sleeping... ");

  sleep_enable();                       //Enabling sleep mode
  set_sleep_mode(SLEEP_MODE_PWR_DOWN);  //Setting the sleep mode, in our case full sleep

  // Setting up interrupts
  attachInterrupt(digitalPinToInterrupt(RtcSquareWavePin), wakeUp, LOW);
  attachInterrupt(digitalPinToInterrupt(HM10TXPin), wakeUp, LOW);

  delay(1000);

  // Sleep, execution recovers here
  sleep_cpu();

  Serial.println("just woke up!");  //next line of code executed after the interrupt
  delay(1000);

  flushSerial2();
}

void wakeUp() {
  interuptFlag = true;

  //Disable sleep mode
  sleep_disable();

  // Detach interrupts to prevent multiple triggers
  detachInterrupt(digitalPinToInterrupt(RtcSquareWavePin));
  detachInterrupt(digitalPinToInterrupt(HM10TXPin));
}

void flushSerial2() {
  byte w = 0;
  for (int i = 0; i < 100; i++) {
    while (Serial2.available() > 0) {
      char k = Serial2.read();
      w++;
      delay(1);
    }
    delay(1);
  }
}
