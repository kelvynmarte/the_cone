#include <Arduino.h>

int potentiometerValue1;
int potentiometerValue2;
int potentiometerValue3;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);

}

void loop() {
  // put your main code here, to run repeatedly:
  if (Serial) {
    // Serial.write(map(analogRead(A0), 0, 1024, 0, 128));

    if(readPotentiometerValues()){
      sendPotentiometerValues();
    }
    
  }
  delay(60);
  
}


boolean readPotentiometerValues(){
  int p1 = map(analogRead(A0), 0, 1024, 0, 255);
  int p2 = map(analogRead(A1), 0, 1024, 0, 255);
  int p3 = map(analogRead(A2), 0, 1024, 0, 255);
  if(potentiometerValue1 != p1 ||
    potentiometerValue2 != p2 ||
    potentiometerValue3 != p3){
        potentiometerValue1 = p1;
        potentiometerValue2 = p2;
        potentiometerValue3 = p3;
        return true;
    }
    return false;
}
void sendPotentiometerValues(){
    Serial.print(potentiometerValue1);
    Serial.print(";");
    Serial.print(potentiometerValue2);
    Serial.print(";");
    Serial.println(potentiometerValue3);
}


