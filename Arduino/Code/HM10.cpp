#include "HM10.h"
#include "MicroSD.h"
#include "RTC.h"

void init_HM10() {
  Serial2.begin(9600);
    
  Serial2.write("AT");  
  delay(500); 
  Serial2.write("AT+NAME ECE496-RC1");  // Setting the name of the bluetooth module 
  delay(500); 
}

void process_command() {
  if (Serial2.available())  {
    char c = Serial2.read();
    if (c != '~') {   // Character reserved for waking up the UNO
      if (c == '#') { // Character that signifies end of data
        Serial.println(inData);

        String command = getValue(inData, ':', 0);
        String value = getValue(inData, ':', 1);
        String pass = getValue(inData, ':', 2);
        Serial.println(command);
        Serial.println(value);
        Serial.println(pass);

        // Process command here
        if (pass == PASSWORD) {
          if (command == "DUMP") {       
            Serial.println("File dump request...");  
            File data_file = SD.open(value);
  
            // if the file is available:
            if (data_file) {
              printDateTime(Rtc.GetDateTime());
              Serial.println();
              while (data_file.available()) {
                char temp = data_file.read();
                Serial.write(temp);
                Serial2.write(temp);
              } 
              Serial2.write("\n");
              Serial2.write("ENDENDENDENDENDEND");    // To notify mobile app that file is done
              Serial.println("=== Finished dumping ===");
              printDateTime(Rtc.GetDateTime());
              Serial.println();
              data_file.close();
            }
            else {
              Serial.print("error opening ");
              Serial.println(value);
            }
          }
  
          if (command == "LIST") {          
            Serial.println("LIST COMMAND");
            File temp = SD.open("/");
            printDirectory(temp, 0);
          }
        } else { // If password is wrong
          Serial.println("Invalid password, disconnecting...");
          Serial2.write("Invalid password, disconnecting...\n");
          Serial2.write("AT");  // Disconnects user
          delay(500); 
        }
        
        // Reset command for next one
        inData = "";
      } else {
        inData += c;
      }
    }
    // As long as we are processing commands, we don't want to go to sleep in the middle
    last_active = Rtc.GetDateTime();
  }
}

String getValue(String data, char separator, int index) {
    int found = 0;
    int strIndex[] = { 0, -1 };
    int maxIndex = data.length() - 1;

    for (int i = 0; i <= maxIndex && found <= index; i++) {
        if (data.charAt(i) == separator || i == maxIndex) {
            found++;
            strIndex[0] = strIndex[1] + 1;
            strIndex[1] = (i == maxIndex) ? i+1 : i;
        }
    }
    return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}
