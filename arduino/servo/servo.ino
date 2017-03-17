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

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  potVal = analogRead(potPin);
  angle = map(potVal, 0, 1023, 0, 179);

  int delayTime = 500;

  if (oldServo1Angle != angle) {
    oldServo1Angle = angle;
    servo1.attach(servo1Pin);
    servo1.write(angle);
    delay(delayTime);
    servo1.detach();
  }

  if (oldServo2Angle != angle) {
    oldServo2Angle = angle;
    servo2.attach(servo2Pin);
    servo2.write(angle);
    delay(delayTime);
    servo2.detach();
  }
  
  if (oldServo3Angle != angle) {
    oldServo3Angle = angle;
    servo3.attach(servo3Pin);
    servo3.write(angle);
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

