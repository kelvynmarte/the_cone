

const int TOLLERANZE = 2;

const int CONTROL_PIN_1[] = {2, 7, 10}; // 7 on L293(1),  10 on L293(1), 7 on L293(2)
const int CONTROL_PIN_2[] = {3, 6, 11}; // 2 on L293(1), 15 on L293(1), 2 on L293(2)
const int ENABLE_PIN[] = {4, 5, 12}; // 1 on L293(1), 9 on L293(1), 1 on L293(2)
const int SENDOR_PIN[] = {A3, A2, A1};


/*
const int M1_DIRECTION_PIN = 4;  // connected to the switch for direction
const int M1_SWITCH = 5; // connected to the switch for turning the motor on and off
const int potPin = A0;  // connected to the potentiometer's output
*/

void setup(){
  for(const int &pinNum : CONTROL_PIN_1)
      pinMode(pinNum, OUTPUT);
  for(const int &pinNum : CONTROL_PIN_2)
    pinMode(pinNum, OUTPUT);
  for(const int &pinNum : ENABLE_PIN)
    pinMode(pinNum, OUTPUT);
  for(const int &pinNum : SENDOR_PIN)
    pinMode(pinNum, INPUT);
    
  Serial.begin(9600);
}

void loop() {
/*
     int current_pos = analogRead(M1_SENDOR_PIN); // read current position from sensor
   Serial.print("current position :");
   Serial.println(current_pos);
   Serial.println(log(1.0f));
   Serial.println(log(10.0f));
   Serial.println(exp(log(1.0f)));
   Serial.println(exp(log(10.0f)));
 delay(100);*/

  
 gotoPosition(0, 100);
 delay(500);
 gotoPosition(1, 100);
 delay(500);
  gotoPosition(2, 100);
 delay(500);
   
 gotoPosition(0, 0);
 delay(500);
 gotoPosition(1, 0);
 delay(500);
  gotoPosition(2, 0);
 delay(500);



 

}


void gotoPosition(int motorId, int pos) {

 Serial.print("go to position :");
 Serial.println(pos);
 
 int current_pos = readPosition(SENDOR_PIN[motorId]); // read current position from sensor
 int difference = pos - current_pos;
 // Enable Motor
 if (difference < 0) {

   digitalWrite(CONTROL_PIN_1[motorId], HIGH);
   digitalWrite(CONTROL_PIN_2[motorId], LOW);

 } else {
   digitalWrite(CONTROL_PIN_1[motorId], LOW);
   digitalWrite(CONTROL_PIN_2[motorId], HIGH);
 }
 while  (abs(difference) > TOLLERANZE) {
   analogWrite(ENABLE_PIN[motorId], 255);
   current_pos = readPosition(SENDOR_PIN[motorId]); // read current position from sensor
   Serial.print("current position :");
   Serial.println(current_pos);
   Serial.println(pos);

   difference = pos - current_pos;
   Serial.println(difference);

   
   
 }
 analogWrite(ENABLE_PIN[motorId], 0);
}

int readPosition(int sensor) {
 int value = analogRead(sensor);
 value = map(value, 2,500, 0, 100);
 return value;
}



