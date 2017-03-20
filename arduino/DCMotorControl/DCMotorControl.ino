

const int TOLLERANZE = 2;

const int CONTROL_PIN_1[] = {2, 7, 10}; // 7 on L293(1),  10 on L293(1), 7 on L293(2)
const int CONTROL_PIN_2[] = {3, 6, 11}; // 2 on L293(1), 15 on L293(1), 2 on L293(2)
const int ENABLE_PIN[] = {4, 5, 12}; // 1 on L293(1), 9 on L293(1), 1 on L293(2)
const int SENDOR_PIN[] = {A3, A2, A1};

const int MOTOR_COUNT = 2;
const int MAX_POSITION = 512;

int lastPosition[] = {0, 0, 0};
int currentPosition[] = {0, 0, 0};
int targetPosition[] = {-1, -1, -1};


/*
  const int M1_DIRECTION_PIN = 4;  // connected to the switch for direction
  const int M1_SWITCH = 5; // connected to the switch for turning the motor on and off
  const int potPin = A0;  // connected to the potentiometer's output
*/

void setup() {
  for (const int &pinNum : CONTROL_PIN_1)
    pinMode(pinNum, OUTPUT);
  for (const int &pinNum : CONTROL_PIN_2)
    pinMode(pinNum, OUTPUT);
  for (const int &pinNum : ENABLE_PIN)
    pinMode(pinNum, OUTPUT);
  for (const int &pinNum : SENDOR_PIN)
    pinMode(pinNum, INPUT);

  Serial.begin(9600);
}

void loop() {
   if (Serial.available() > 0) {
    String first  = Serial.readStringUntil(';');
    //Serial.read(); //next character is comma, so skip it using this
    String second = Serial.readStringUntil(';');
    //Serial.read();
    String third  = Serial.readStringUntil('\n');
    //Serial.print(first); Serial.print(" - "); Serial.print(second); Serial.print(" - "); Serial.println(third); 
    targetPosition[0] = (int)first.toInt();
    targetPosition[1]  = (int)second.toInt();
    targetPosition[2]  = (int)third.toInt();
    if ((targetPosition[0] == 0) || (targetPosition[1] == 0) || (targetPosition[2] ==0)) {
      Serial.print("error");
    }
  }

  boolean valuesChanged = false;
  for (int i = 0; i < MOTOR_COUNT; i++) {
    lastPosition[i] = currentPosition[i];
    currentPosition[i] = readPosition(SENDOR_PIN[i]);

    if(targetPosition[i] != -1) moveToPosition(i, targetPosition[i]);
    
    if(lastPosition[i] != currentPosition[i]) {
      // sent new values
      valuesChanged = true;
    }
    /*
    Serial.print(lastPosition[i]);
    Serial.print(" != ");
    Serial.println(currentPosition[i]);
    */
  }
  if(valuesChanged) sendPotentiometerValues();

  delay(40);

  
}

boolean moveToPosition(int motorId, int pos) {
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
  if (abs(difference) > TOLLERANZE) {
    analogWrite(ENABLE_PIN[motorId], 255);
    return false;
  } else {
    analogWrite(ENABLE_PIN[motorId], 0);
    targetPosition[motorId] = -1;
    return true;
  }

}

int readPosition(int sensor) {
  int value = analogRead(sensor);
  value = map(value, 2, 500, 0, MAX_POSITION);
  return value;
}

void sendPotentiometerValues() {
  for (int i = 0; i <= MOTOR_COUNT; i++) {
    // targetPosition[i] = currentPosition[i]; // dont move back when there 
    Serial.print(currentPosition[i]);
    if(i != MOTOR_COUNT) {
      Serial.print(";");
    }else{
      Serial.println();
    }
    
  }
}


