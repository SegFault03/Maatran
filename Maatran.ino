#include<SoftwareSerial.h>

int flag=0;
int RX_pin = 4;
int TX_pin = 2;
int somkeAO = A0;
float temp;
const int z_out = A2;
const int y_out = A3;
const int x_out = A4;

// Arduino IR Sensor Code
int IRSensor = 8; // connect ir sensor module to Arduino pin 9
int LED = 13; // conect LED to Arduino pin 13

const int GSR=A5;
int sensorValue=0;
int gsr_average=0;

SoftwareSerial BTserial(RX_pin, TX_pin);
String BT_data;
String Arduino_data;
int data;

void setup() {
  // put your setup code here, to run once:
  pinMode(somkeAO, INPUT);
  pinMode(IRSensor, INPUT); // IR Sensor pin INPUT
  pinMode(LED, OUTPUT); // LED Pin Output
  Serial.begin(9600);
  BTserial.begin(9600);

}

void loop() {
  // put your main code here, to run repeatedly:
  
  int analogSensor = analogRead(somkeAO);

  temp = analogRead(A1);
  temp = temp*0.48828125;

  int x_adc_value, y_adc_value, z_adc_value; 
  double x_g_value, y_g_value, z_g_value;
  double roll, pitch, yaw;
  x_adc_value = analogRead(x_out); /* Digital value of voltage on x_out pin */ 
  y_adc_value = analogRead(y_out); /* Digital value of voltage on y_out pin */ 
  z_adc_value = analogRead(z_out); /* Digital value of voltage on z_out pin */ 
  x_g_value = ( ( ( (double)(x_adc_value * 5)/1024) - 1.65 ) / 0.330 ); /* Acceleration in x-direction in g units */ 
  y_g_value = ( ( ( (double)(y_adc_value * 5)/1024) - 1.65 ) / 0.330 ); /* Acceleration in y-direction in g units */ 
  z_g_value = ( ( ( (double)(z_adc_value * 5)/1024) - 1.80 ) / 0.330 ); /* Acceleration in z-direction in g units */ 

  roll = ( ( (atan2(y_g_value,z_g_value) * 180) / 3.14 ) + 180 ); /* Formula for roll */
  pitch = ( ( (atan2(z_g_value,x_g_value) * 180) / 3.14 ) + 180 ); /* Formula for pitch */
  yaw = ( ( (atan2(x_g_value,y_g_value) * 180) / 3.14 ) + 180 ); /* Formula for yaw */
  /* Not possible to measure yaw using accelerometer. Gyroscope must be used if yaw is also required */

  long sum=0;
  for(int i=0;i<10;i++)           //Average the 10 measurements to remove the glitch
  {
    sensorValue=analogRead(GSR);
    sum += sensorValue;
    delay(5);
  }
  gsr_average = sum/10;

  // Serial.println(data);
  
  // if(flag==0){
  //   BTserial.println("Enter 1 for Temperature: ");
  //   BTserial.println("Enter 2 for Air Quality: ");
  //   BTserial.println("Enter 3 for x, y ,Z: ");
  //   BTserial.println("Enter 4 for emotion detective: ");
  //   BTserial.println("Enter 5 for pitch, roll, yaw: ");
  //   if(BTserial.available()){
  //     BT_data = BTserial.readString();
  //     data = BT_data.toInt();
  //     Serial.println(data);
  //   }
  //   flag=1;
  // }

  // if(flag==1){
  //   switch(data){
  //     case 1:
  //       BTserial.println("You Entered 1");
  //       BTserial.print("Temperature: ");
  //       BTserial.println(temp);
  //       break;
  //     case 2:
  //       BTserial.println("You Entered 2");
  //       BTserial.print("Air Quality: ");
  //       BTserial.println(analogSensor);
  //       break;
  //     case 3:
  //       BTserial.println("You Entered 3");
  //       BTserial.print("X, Y, Z: ");
  //       BTserial.print(x_adc_value);
  //       BTserial.print(" ");
  //       BTserial.print(y_adc_value);
  //       BTserial.print(" ");
  //       BTserial.println(z_adc_value);
  //       break;
  //     case 4:
  //       BTserial.println("You Entered 4");
  //       BTserial.print("Emotion Level: ");
  //       BTserial.println(gsr_average);
  //       break;
  //     case 5:
  //       BTserial.println("You Entered 5");
  //       BTserial.print("pitch, roll, yaw: ");  
  //       BTserial.print(roll);
  //       BTserial.print(" ");
  //       BTserial.print(pitch);
  //       BTserial.print(" ");
  //       BTserial.println(yaw);
  //       break;
  //     default:
  //       BTserial.println("Enter a valid number");
  //       break;
  //   }
  //   flag=0;
  // }



  BTserial.print(analogSensor);
  // BTserial.print(" ");

  // BTserial.print(temp);
  // BTserial.print(" ");

  // BTserial.print(x_adc_value);
  // BTserial.print(" ");
  // BTserial.print(y_adc_value);
  // BTserial.print(" ");
  // BTserial.print(z_adc_value);
  // BTserial.print(" ");
  // BTserial.print(roll);
  // BTserial.print(" ");
  // BTserial.print(pitch);
  // BTserial.print(" ");
  // BTserial.println(yaw);

  // BTserial.println(gsr_average);

  // int sensorStatus = digitalRead(IRSensor); // Set the GPIO as Input
  // if (sensorStatus == 0) // Check if the pin high or not
  // {
  //   digitalWrite(LED, HIGH); // LED High
  //   BTserial.println("Motion Detected!"); // print Motion Ended! on the serial monitor window
  // }


  delay(2000);
}
