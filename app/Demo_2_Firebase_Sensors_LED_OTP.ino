#include <Keypad.h>
#include<LiquidCrystal.h>
#include <Servo.h>

const byte ROWS = 2; 
const byte COLS = 3; 

LiquidCrystal lcd(12, 11,5 , 4, 3, 2);

char hexakeys[ROWS][COLS] = 
{
  {'1','2','3'},
  {'4','5','6'}
};

byte colPins[COLS] = { 8, 7, 6};

byte rowPins[ROWS] = { 13, 10}; 

Servo ServoMoto;

Keypad customKeypad = Keypad( makeKeymap(hexakeys), rowPins, colPins, ROWS, COLS );
int a=0;
int b=0;
int c=0;
int i=0;
int j=0;

int sum=0;

void setup()
{
  Serial.begin(9600);
  lcd.begin(16, 2);
  lcd.setCursor(0,0);
  lcd.print("Enter OTP: ");
  ServoMoto.attach(9);
  ServoMoto.write(91);
} 
void loop()
{
    char key = customKeypad.getKey();
    if(key)
    {
        lcd.setCursor(2+i,1);
        lcd.print(key);
        Serial.print("KEY: ");
        Serial.println(key);
        Serial.print("SUM: ");
        Serial.println(sum);
        i++;
        
        /*if(key=='1')
        {
          a++;
        }
        else if(key=='2')
        {
          b++;
        }
        else if(key=='3')
        {
          c++;
        }*/
        
        if(sum==5451 && key=='6')
        {
           lcd.setCursor(0,0);
           lcd.print("OTP is Correct");
           lcd.setCursor(2,1);
           lcd.print("Gate Opened" );
           ServoMoto.write(10);
           delay(300);
           ServoMoto.write(91);
           delay(3000);
           ServoMoto.write(170);
           delay(300);
           ServoMoto.write(91);
           delay(3000);
           ServoMoto.write(10);
           delay(300);
           ServoMoto.write(91);
           delay(3000);
         }
         else if(sum!=5451 && key=='6')
         {
           lcd.setCursor(0,0);
           lcd.print("OTP is Incorrect");
           lcd.setCursor(0,1);
           lcd.print("Gate cant Open" );   
         }
         else
         {
          sum=(sum*10)+key;
         }
       
        /*if(a==2);
        {
            a=1;
        }
        if(b==2);
        {
            b=1;
        }
        if(c==2);
        {
            c=1;
        }*/
        
    }
}

