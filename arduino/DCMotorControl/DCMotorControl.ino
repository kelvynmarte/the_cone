

const int TOLLERANZE = 2;

const int CONTROL_PIN_1[] = {2, 7, 10}; // 7 on L293(1),  10 on L293(1), 7 on L293(2)
const int CONTROL_PIN_2[] = {3, 6, 11}; // 2 on L293(1), 15 on L293(1), 2 on L293(2)
const int ENABLE_PIN[] = {4, 5, 12}; // 1 on L293(1), 9 on L293(1), 1 on L293(2)
const int SENSOR_PIN[] = {A3, A2, A1};

const int SENSOR_INPUT_VALUE[] = {0, 4, 20,65,127,285,427,509,533, 600};
const int SENSOR_INPUT_VALUE_MAPPING[] = {0, 20, 60,100,140,180,220,260,300, 300};

const int VALUE_CHANGED_TOLERANCE = 5;
const int MOTOR_COUNT = 3;
const int MAX_POSITION = 180;

int lastSentPosition[] = {0, 0, 0};
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
  for (const int &pinNum : SENSOR_PIN)
    pinMode(pinNum, INPUT);

  Serial.begin(9600);
}

void loop() {
  // Serial.println("HELLO");

  /*
    float value = analogRead(SENSOR_PIN[0]);
            Serial.print("0: ");

  Serial.print(value);
  float valuef = 1000.0*pow(value/550.,10);
  Serial.print(" => ");
  Serial.println(mapLogarithmicValue(value));


  
     value = analogRead(SENSOR_PIN[1]);
        Serial.print("1: ");

  Serial.print(value);
  valuef = 1000.0*pow(value/550.,10);
  Serial.print(" => ");
  Serial.println(mapLogarithmicValue(value));

   value = analogRead(SENSOR_PIN[2]);
    Serial.print("2: ");

  Serial.print(value);
  valuef = 1000.0*pow(value/550.,10);
  Serial.print(" => ");
  Serial.println(mapLogarithmicValue(value));


  delay(2000); */
  
  
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
  for (int i = 0; i < MOTOR_COUNT; i++) { // sizeof crashes the sketch
    currentPosition[i] = readPosition(SENSOR_PIN[i]);

    if(targetPosition[i] != -1) moveToPosition(i, targetPosition[i]);
    
    if(abs(lastSentPosition[i] - currentPosition[i]) > VALUE_CHANGED_TOLERANCE) {
      // sent new values
      valuesChanged = true;
    }
  }
  if(valuesChanged) sendPotentiometerValues();


  delay(100);
  
}

boolean moveToPosition(int motorId, int pos) {
  int current_pos = readPosition(SENSOR_PIN[motorId]); // read current position from sensor
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
  // int value = analogRead(sensor);
  int value = mapLogarithmicValue(analogRead(sensor));
  //value = min(map(value, 0, 300, 0, MAX_POSITION), 180);
  value = map(value, 0, 300, 0, MAX_POSITION);
  return value;
}

int mapLogarithmicValue(int value){
  int returnValue = 0;
  for(int i = 1; i < sizeof(SENSOR_INPUT_VALUE); i++){
    if(value < SENSOR_INPUT_VALUE[i]){
      returnValue = SENSOR_INPUT_VALUE_MAPPING[i-1];
      returnValue += map(value-SENSOR_INPUT_VALUE[i-1], 0, SENSOR_INPUT_VALUE[i] - SENSOR_INPUT_VALUE[i-1], 0, SENSOR_INPUT_VALUE_MAPPING[i] - SENSOR_INPUT_VALUE_MAPPING[i-1]);
      return returnValue;
    }
  }
  return returnValue;
}

void sendPotentiometerValues() {
  
  for (int i = 0; i < MOTOR_COUNT; i++) {
    lastSentPosition[i] = currentPosition[i];
    // targetPosition[i] = currentPosition[i]; // dont move back when there 
    Serial.print(currentPosition[i]);
    if(i != MOTOR_COUNT-1) {
      Serial.print(";");
    }else{
      Serial.println();
    }
    
  }
}


