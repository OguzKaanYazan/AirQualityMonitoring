int input = A0;
#include <SoftwareSerial.h>
SoftwareSerial bt_conn(7,6); //RX TX


void setup() {
 
 Serial.begin(9600);
 bt_conn.begin(9600);

}

void loop() {
  bt_conn.println(analogRead(input));
  Serial.println(analogRead(input));

  delay(30000);

}
