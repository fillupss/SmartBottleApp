int fsrAnalogPin = 0;
int fsrReading;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  sendAndroidValues();
  delay(2000);
}

void sendAndroidValues(){
  fsrReading = analogRead(fsrAnalogPin);
  Serial.print("#");
  Serial.print(fsrReading);
  Serial.print("~");
  Serial.println();
  delay(10);
}
