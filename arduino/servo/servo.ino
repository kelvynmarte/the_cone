#include <Servo.h>

int const potPin = A0;
int const servo1Pin = 9;
int const servo2Pin = 10;
int const servo3Pin = 11;
int potVal;
int angle;
Servo servo1;
Servo servo2;
Servo servo3;
int oldServo1Angle = 0;
int oldServo2Angle = 0;
int oldServo3Angle = 0;
String inputString = "";         // a string to hold incoming data
boolean stringComplete = false;  // whether the string is complete

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  inputString.reserve(200);
}

void loop() {
  // put your main code here, to run repeatedly:
//  potVal = analogRead(potPin);
//  angle = map(potVal, 0, 1023, 0, 179);
  int angle1, angle2, angle3;
  if (stringComplete) {
//    Serial.println(inputString);
    if (inputString.substring(0) == "i") { //i;12;12;12
      angle1 = getSubstring(inputString, ";", 1).toInt();
      angle2 = getSubstring(inputString, ";", 2).toInt();
      angle3 = getSubstring(inputString, ";", 3).toInt();
    }
    // clear the string:
    inputString = "";
    stringComplete = false;
  }

  int delayTime = 700;
  if (oldServo1Angle != angle1) {
    oldServo1Angle = angle1;
    servo1.attach(servo1Pin);
//    servo1.write(angle);
    servo1.write(angle1);
    delay(delayTime);
    servo1.detach();
  }
  if (oldServo2Angle != angle2) {
    oldServo2Angle = angle2;
    servo2.attach(servo2Pin);
//    servo2.write(angle);
    servo2.write(angle2);
    delay(delayTime);
    servo2.detach();
  }
  if (oldServo3Angle != angle3) {
    oldServo3Angle = angle3;
    servo3.attach(servo3Pin);
//    servo3.write(angle);
    servo3.write(angle3);
    delay(delayTime);
    servo3.detach();
  }
  sendPotentiometerValues();
  
  delay(100);
}

void sendPotentiometerValues() {
  int value1 = map(analogRead(A1), 103, 436, 0, 180);
  int value2 = map(analogRead(A2), 102, 426, 0, 180);
  int value3 = map(analogRead(A3), 101, 434, 0, 180);
  Serial.print(value1);
  Serial.print(";");
  Serial.print(value2);
  Serial.print(";");
  Serial.println(value3);
}

void serialEvent() {
  while (Serial.available()) {
    // get the new byte:
    char inChar = (char)Serial.read();
    // add it to the inputString:
    inputString += inChar;
    // if the incoming character is a newline, set a flag
    // so the main loop can do something about it:
    if (inChar == '\n') {
      stringComplete = true;
    }
  }
}

String getSubstring(String data, char separator, int index) {
  int found = 0;
  int strIndex[] = {0, -1};
  int maxIndex = data.length()-1;

  for (int i=0; i<=maxIndex && found<=index; i++) {
    if (data.charAt(i)==separator || i==maxIndex) {
        found++;
        strIndex[0] = strIndex[1]+1;
        strIndex[1] = (i == maxIndex) ? i+1 : i;
    }
  }

  return found>index ? data.substring(strIndex[0], strIndex[1]) : "";
}
