#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>

#define FIREBASE_HOST "hackathon-69dd0.firebaseio.com"
#define FIREBASE_AUTH "qEUEEXPSnyRFE7AikgMTNI9QOtaYMVA64bonLMTV"
#define WIFI_SSID "Jio."
#define WIFI_PASSWORD "shiv@1234"
int usonic1();
int usonic2();
int usonic3();
void nmcu();

int trigPin1=D1;
int echoPin1=D2;
long duration1;
int distanceCm1;

int trigPin2=D3;
int echoPin2=D4;
long duration2;
int distanceCm2;

int trigPin3=D6;
int echoPin3=D7;
long duration3;
int distanceCm3;

int P1;
int P2;
int P3;

int L1=D0;
int L2=D8;
int L3=D5;

void setup() 
{
  Serial.begin(9600);
  
  // connect to wifi.
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("connecting");
  while (WiFi.status() != WL_CONNECTED) 
  {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.set("Slot_1", 0);
  Firebase.set("Slot_2", 0);
  Firebase.set("Slot_3", 0);
  Firebase.set("led_1", 0);
  Firebase.set("led_2", 0);
  Firebase.set("led_3", 0);

  pinMode(trigPin1,OUTPUT);
  pinMode(echoPin1,INPUT);
  pinMode(trigPin2,OUTPUT);
  pinMode(echoPin2,INPUT);
  pinMode(trigPin3,OUTPUT);
  pinMode(echoPin3,INPUT);
  pinMode(L1,OUTPUT);
  pinMode(L2,OUTPUT);
  pinMode(L3,OUTPUT);
}


void loop() 
{
  usonic1();
  if(distanceCm1 < 10)
  {
    P1=HIGH;
  }
  else
  {
    P1=LOW;
  }
  usonic2();
  if(distanceCm2 < 10)
  {
    P2=HIGH;
  }
  else
  {
    P2=LOW;
  }
  usonic3();
  if(distanceCm3 < 10)
  {
    P3=HIGH;
  }
  else
  {
    P3=LOW;
  }
  nmcu();
  digitalWrite(L1, Firebase.getInt("led_1"));
  digitalWrite(L2, Firebase.getInt("led_2"));
  digitalWrite(L3, Firebase.getInt("led_3"));
}

int usonic1()
{
  digitalWrite(trigPin1,LOW);
  delay(20);
  //delayMicroseconds(2);
  digitalWrite(trigPin1,HIGH);
  delay(20);
  //delayMicroseconds(10);
  digitalWrite(trigPin1,LOW);
  duration1=pulseIn(echoPin1,HIGH);
  distanceCm1=((duration1*0.034)/2);
  //distanceCm=(duration/2)/21.1;
  Serial.println(distanceCm1);
  return distanceCm1;
}

int usonic2()
{
  digitalWrite(trigPin2,LOW);
  delay(20);
  //delayMicroseconds(2);
  digitalWrite(trigPin2,HIGH);
  delay(20);
  //delayMicroseconds(10);
  digitalWrite(trigPin2,LOW);
  duration2=pulseIn(echoPin2,HIGH);
  distanceCm2=((duration2*0.034)/2);
  //distanceCm=(duration/2)/21.1;
  Serial.println(distanceCm2);
  return distanceCm2;
}

int usonic3()
{
  digitalWrite(trigPin3,LOW);
  delay(20);
  //delayMicroseconds(2);
  digitalWrite(trigPin3,HIGH);
  delay(20);
  //delayMicroseconds(10);
  digitalWrite(trigPin3,LOW);
  duration3=pulseIn(echoPin3,HIGH);
  distanceCm3=((duration3*0.034)/2);
  //distanceCm=(duration/2)/21.1;
  Serial.println(distanceCm3);
  return distanceCm3;
}

void nmcu()
{
  Serial.println("Park1");
  Serial.print(" : ");
  Serial.print(P1);
  Firebase.setInt("Slot_1", P1);
  delay(200);
  Serial.println("Park2");
  Serial.print(" : ");
  Serial.print(P2);
  Firebase.setInt("Slot_2", P2);
  delay(200);
  Serial.println("Park3");
  Serial.print(" : ");
  Serial.print(P3);
  Firebase.setInt("Slot_3", P3);
  delay(200);
}

