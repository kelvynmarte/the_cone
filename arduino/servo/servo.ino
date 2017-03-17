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
int angle1, angle2, angle3 = 0;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  //  potVal = analogRead(potPin);
  //  angle = map(potVal, 0, 1023, 0, 179);

  if (Serial.available() > 0) {
    String first  = Serial.readStringUntil(';');
    //Serial.read(); //next character is comma, so skip it using this
    String second = Serial.readStringUntil(';');
    //Serial.read();
    String third  = Serial.readStringUntil('\n');
    //Serial.print(first); Serial.print(" - "); Serial.print(second); Serial.print(" - "); Serial.println(third); 
    angle1 = (int)first.toInt();
    angle2 = (int)second.toInt();
    angle3 = (int)third.toInt();
    if ((angle1 == 0) || (angle2 == 0) || (angle3 ==0)) {
      Serial.print("error");
    }
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

String getSubstring(String data, char separator, int index) {
  int found = 0;
  int strIndex[] = {0, -1};
  int maxIndex = data.length() - 1;

  for (int i = 0; i <= maxIndex && found <= index; i++) {
    if (data.charAt(i) == separator || i == maxIndex) {
      found++;
      strIndex[0] = strIndex[1] + 1;
      strIndex[1] = (i == maxIndex) ? i + 1 : i;
    }
  }

  return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}
