int ledPin = 13;  // use the built in LED on pin 13 of the Uno
double max_hr = 0;
double min_hr = 0;
double hr = 0;
int flag = 0;        // make sure that you return the state only once

void setup() {
    // sets the pins as outputs:
    pinMode(ledPin, OUTPUT);
    digitalWrite(ledPin, LOW);

    Serial.begin(9600); // Default connection rate for my BT module
}

void loop() {
    //if some data is sent, read it and save it in the state variable
    if(Serial.available() > 0){
      string receive = Serial.readString();
      min_hr = recieve.substring(0, 3);
      max_hr = receive.substring(4, 7);
      flag=0;
    }
    // if the state is 0 the led will turn off
    if (hr > min_hr && hr < max_hr) {
        digitalWrite(ledPin, LOW);
        if(flag == 0){
          Serial.println("LED: off");
          flag = 1;
        }
    }
    // if the state is 1 the led will turn on
    else if (hr <= min_hr && hr >= max_hr) {
        digitalWrite(ledPin, HIGH);
        if(flag == 0){
          Serial.println("LED: on");
          flag = 1;
        }
    }
}
