#include "BluetoothSerial.h"

#define PIN_R 14
#define PIN_G 26
#define PIN_B 33
#define LED_CHANNEL_R 0
#define LED_CHANNEL_G 1
#define LED_CHANNEL_B 2 
#define LED_RESOLUTION 8
#define LED_FREQ 5000

BluetoothSerial SerialBT;

bool isBlinking = false;
bool isFading = false;
bool isPhonk = false;
bool isSOS = false;
bool isFlash = false;
bool isStrobe = false;
bool isCold = false;
bool isRomantic = false;
bool isRelax = false;
bool isChill = false;
bool isEpileptics = false;
bool isPolice = false;

int fadeDirection = 1;
int brightness = 0;
const int fadeSpeed = 5;

unsigned long phonkStartTime = 0;
int phonkPhase = 0;
unsigned long phonkLastSwitchTime = 0;
const unsigned long phonkInterval = 200;

bool redState = false;
bool greenState = false;
bool blueState = false;

void resetPins() 
{
  digitalWrite(PIN_R, LOW);
  digitalWrite(PIN_G, LOW);
  digitalWrite(PIN_B, LOW);

  pinMode(PIN_R, OUTPUT);
  pinMode(PIN_G, OUTPUT);
  pinMode(PIN_B, OUTPUT);
}

void turnOnColor(int r, int g, int b) 
{
  ledcWrite(LED_CHANNEL_R, r);
  ledcWrite(LED_CHANNEL_G, g);
  ledcWrite(LED_CHANNEL_B, b);
}

void setup() 
{ 
  pinMode(PIN_R, OUTPUT); 
  pinMode(PIN_G, OUTPUT);
  pinMode(PIN_B, OUTPUT);

  digitalWrite(PIN_G, LOW);
  digitalWrite(PIN_B, LOW);
  digitalWrite(PIN_R, LOW);

  SerialBT.begin("ESP32_LED_Control");
  Serial.println("Bluetooth initialized");
}

void loop() 
{
  if (SerialBT.available()) 
  {
    char command = SerialBT.read();

    if (command == 'B') 
    {
      String brightnessValue = "";
      while (SerialBT.available()) 
      {
        char ch = SerialBT.read();
        if (isDigit(ch)) 
        {
          brightnessValue += ch;
        }
        else
        {
          break;
        }
      }

      int brightness = brightnessValue.toInt();
      brightness = constrain(brightness, 0, 255);

      turnOnColor(brightness, brightness, brightness);
    }

    // Синий цвет
    if (command == 'M')
    {
      Serial.println("Включен синий цвет");
      pinMode(PIN_R, INPUT);
      digitalWrite(PIN_R, HIGH);
      pinMode(PIN_B, OUTPUT);
      pinMode(PIN_G, INPUT);
    }
    if (command == 'E') 
    {
      Serial.println("Выключен синий цвет");
      resetPins();
    }

    // Красный цвет
    if (command == 'Z')
    {
      Serial.println("Включен красный цвет");
      pinMode(PIN_R, OUTPUT);
      pinMode(PIN_G, INPUT);
      pinMode(PIN_B, INPUT);
    }
    if (command == 'X') 
    {
      Serial.println("Выключен красный цвет");
      resetPins();
    }

    // Зеленый цвет
    if (command == 'Q')
    {
      Serial.println("Включен зеленый цвет");
      pinMode(PIN_G, OUTPUT);
      digitalWrite(PIN_R, HIGH);
      pinMode(PIN_R, INPUT);
      pinMode(PIN_B, INPUT);
    }
    if (command == 'N') 
    {
      Serial.println("Выключен зеленый цвет");
      resetPins();
    }

    // Синий и красный цвет
    if (command == 'O')
    {
      Serial.println("Включен синий и красный цвет");
      pinMode(PIN_R, OUTPUT);
      pinMode(PIN_B, OUTPUT);
      digitalWrite(PIN_R, HIGH);
      pinMode(PIN_G, INPUT);
    }
    if (command == 'R') 
    {
      Serial.println("Выключен синий и красный цвет");
      resetPins();
    }

    // Синий и зеленый цвет
    if (command == '2')
    {
      Serial.println("Включен синий и зеленый цвет");
      pinMode(PIN_G, OUTPUT);
      pinMode(PIN_B, OUTPUT);
      pinMode(PIN_R, INPUT);
    }
    if (command == 'V') 
    {
      Serial.println("Выключен синий и зеленый цвет");
      resetPins();
    }

    // Зеленый и красный цвет
    if (command == 'b')
    {
      Serial.println("Включен зеленый и красный цвет");
      pinMode(PIN_G, OUTPUT);
      pinMode(PIN_R, OUTPUT);
      pinMode(PIN_B, INPUT);
    }
    if (command == 'a') 
    {
      Serial.println("Выключен зеленый и красный цвет");
      resetPins();
    }

    // Режимы
    // Blink
    if (command == '3')
    {
      Serial.println("Включен Blink");
      isBlinking = true;
    }
    if (command == '4') 
    {
      Serial.println("Выключен Blink");
      isBlinking = false;
      resetPins();
    }

    // Fade
    if (command == '5')
    {
      Serial.println("Включен Fade");
      isFading = true;
      brightness = 0;
    }
    if (command == '6') 
    {
      Serial.println("Выключен Fade");
      isFading = false;
      resetPins();
    }

    // Phonk
    if (command == '7') 
    {
      Serial.println("Включен режим Phonk");
      resetPins();
      isPhonk = true;
      phonkStartTime = millis();
      phonkLastSwitchTime = millis();
      phonkPhase = 0;
    }
    if (command == '8') 
    {
      Serial.println("Выключен режим Phonk");
      isPhonk = false;
      resetPins();
    }

    // SOS
    if (command == '9') 
    {
      Serial.println("Включен SOS");
      isSOS = true;
    }
    if (command == 'W') 
    {
      Serial.println("Выключен SOS");
      isSOS = false;
      resetPins();
    }

    // Flash
    if (command == 'C')
    {
      Serial.println("Включен Flash");
      isFlash = true;
    }
    if (command == 'D')
    {
      Serial.println("Выключен Flash");
      isFlash = false;
      resetPins();
    }

    // Strobe
    if (command == 'S')
    {
      Serial.println("Включен Strobe");
      isStrobe = true;
    }
    if (command == 'A')
    {
      Serial.println("Выключен Strobe");
      isStrobe = false;
      resetPins();
    }

    // Cold
    if (command == 'G')
    {
      Serial.println("Включен Cold");
      isCold = true;
      brightness = 0;
    }
    if (command == 'F')
    {
      Serial.println("Выключен Cold");
      isCold = false;
      resetPins();
    }

    // Romantic
    if (command == 'J')
    {
      Serial.println("Включен Romantic");
      isRomantic = true;
    }
    if (command == 'H')
    {
      Serial.println("Выключен Romantic");
      isRomantic = false;
      resetPins();
    }

    // Relax
    if (command == 'L')
    {
      Serial.println("Включен Relax");
      isRelax = true;
    }
    if (command == 'K')
    {
      Serial.println("Выключен Relax");
      isRelax = false;
      resetPins();
    }

    // Chill
    if (command == '1')
    {
      Serial.println("Включен Chill");
      isChill = true;
    }
    if (command == 'P')
    {
      Serial.println("Выключен Chill");
      isChill = false;
      resetPins();
    }

    // Epileptics
    if (command == 'U')
    {
      Serial.println("Включен Epileptics");
      isEpileptics = true;
    }
    if (command == 'I')
    {
      Serial.println("Выключен Epileptics");
      isEpileptics = false;
      resetPins();
    }

    // Police
    if (command == 'T')
    {
      Serial.println("Включен Police");
      isPolice = true;
    }
    if (command == 'Y')
    {
      Serial.println("Выключен Police");
      isPolice = false;
      resetPins();
    }
  }

  if (isBlinking) 
  {
    pinMode(PIN_B, OUTPUT);
    pinMode(PIN_G, OUTPUT);
    pinMode(PIN_R, OUTPUT);
    delay(1000);

    digitalWrite(PIN_R, LOW);
    digitalWrite(PIN_G, LOW);
    digitalWrite(PIN_B, LOW);
    pinMode(PIN_B, INPUT);
    pinMode(PIN_G, INPUT);
    pinMode(PIN_R, INPUT);
    delay(1000);
  }

  if (isFading)
  {
    analogWrite(PIN_R, brightness);
    analogWrite(PIN_G, brightness);
    analogWrite(PIN_B, brightness);

    brightness += fadeDirection * fadeSpeed;

    if (brightness <= 0 || brightness >= 255) 
    {
      fadeDirection = -fadeDirection;
    }

    delay(30);
  }

  if (isPhonk) 
  {
    unsigned long currentTime = millis();

    if (currentTime - phonkStartTime > 2000) 
    {
      phonkStartTime = currentTime;
      phonkPhase = (phonkPhase + 1) % 3;
    }

    if (currentTime - phonkLastSwitchTime > phonkInterval) 
    {
      phonkLastSwitchTime = currentTime;

      switch (phonkPhase) 
      {
        case 0:  // Фаза 0: мигание красного
          if (redState) 
          {
            pinMode(PIN_R, INPUT);
            redState = false;
          } 
          else 
          {
            pinMode(PIN_R, OUTPUT);
            digitalWrite(PIN_R, HIGH);
            redState = true;
          }
          break;

        case 1:  // Фаза 1: мигание красного и синего
          if (redState || blueState) 
          {
            pinMode(PIN_R, INPUT);
            pinMode(PIN_B, INPUT);
            redState = false;
            blueState = false;
          } 
          else 
          {
            pinMode(PIN_R, OUTPUT);
            digitalWrite(PIN_R, HIGH);
            redState = true;

            pinMode(PIN_B, OUTPUT);
            digitalWrite(PIN_B, HIGH);
            blueState = true;
          }
          break;

        case 2:  // Фаза 2: мигание зеленого и красного
          if (redState || greenState) 
          {
            pinMode(PIN_R, INPUT);
            pinMode(PIN_G, INPUT);
            redState = false;
            greenState = false;
          } 
          else 
          {
            pinMode(PIN_R, OUTPUT);
            digitalWrite(PIN_R, HIGH);
            redState = true;

            pinMode(PIN_G, OUTPUT);
            digitalWrite(PIN_G, HIGH);
            greenState = true;
          }
          break;

        default:
          break;
      }
    }
  }

  if (isSOS) 
  {
    for (int i = 0; i < 3; i++) 
    {
      pinMode(PIN_R, OUTPUT);
      pinMode(PIN_G, OUTPUT);
      pinMode(PIN_B, OUTPUT);
      delay(200);

      pinMode(PIN_R, INPUT);
      pinMode(PIN_G, INPUT);
      pinMode(PIN_B, INPUT);
      delay(200);
    }
    delay(600);

    for (int i = 0; i < 3; i++) 
    {
      pinMode(PIN_R, OUTPUT);
      pinMode(PIN_G, OUTPUT);
      pinMode(PIN_B, OUTPUT);
      delay(600);

      pinMode(PIN_R, INPUT);
      pinMode(PIN_G, INPUT);
      pinMode(PIN_B, INPUT);
      delay(200);
    }
    delay(600);

    for (int i = 0; i < 3; i++) 
    {
      pinMode(PIN_R, OUTPUT);
      pinMode(PIN_G, OUTPUT);
      pinMode(PIN_B, OUTPUT);
      delay(200);

      pinMode(PIN_R, INPUT);
      pinMode(PIN_G, INPUT);
      pinMode(PIN_B, INPUT);
      delay(200);
    }

    delay(2000);
  }

  if (isFlash)
  {
    pinMode(PIN_R, OUTPUT);
    pinMode(PIN_G, INPUT);
    pinMode(PIN_B, INPUT);
    delay(1000);

    pinMode(PIN_G, INPUT);
    pinMode(PIN_R, INPUT);
    pinMode(PIN_B, OUTPUT);
    delay(1000);

    pinMode(PIN_R, INPUT);
    pinMode(PIN_B, INPUT);
    pinMode(PIN_G, OUTPUT);
    delay(1000);
  }

  if (isStrobe)
  {
    pinMode(PIN_B, OUTPUT);
    pinMode(PIN_G, OUTPUT);
    pinMode(PIN_R, OUTPUT);
    delay(150);

    digitalWrite(PIN_R, LOW);
    digitalWrite(PIN_G, LOW);
    digitalWrite(PIN_B, LOW);
    pinMode(PIN_B, INPUT);
    pinMode(PIN_G, INPUT);
    pinMode(PIN_R, INPUT);
    delay(150);
  }

  if (isCold)
  {
    pinMode(PIN_G, INPUT);
    pinMode(PIN_R, INPUT);
    analogWrite(PIN_B, brightness);

    brightness += fadeDirection * fadeSpeed;

    if (brightness <= 0 || brightness >= 255) 
    {
      fadeDirection = -fadeDirection;
    }

    delay(30);
  }

  if (isRomantic)
  {
    pinMode(PIN_R, INPUT);
    pinMode(PIN_B, INPUT);
    pinMode(PIN_G, INPUT);

    delay(500);
    for (int i = 0; i < 2; i++) 
    {
      pinMode(PIN_R, OUTPUT);
      pinMode(PIN_G, OUTPUT);
      pinMode(PIN_B, OUTPUT);
      delay(200);

      pinMode(PIN_R, INPUT);
      pinMode(PIN_G, INPUT);
      pinMode(PIN_B, INPUT);
    }
  }

  if (isRelax)
  {
      for (int i = 0; i <= 255; i++)
      {
          analogWrite(PIN_G, 255 - i);
          analogWrite(PIN_B, i);
          delay(10);
      }

      for (int i = 0; i <= 255; i++)
      {
          analogWrite(PIN_B, 255 - i);
          analogWrite(PIN_R, i);
          delay(10);
      }

      for (int i = 0; i <= 255; i++)
      {
          analogWrite(PIN_R, 255 - i);
          analogWrite(PIN_G, i);
          delay(10);
      }
  }

  if (isChill)
  {
    for (int i = 0; i < 3; i++) 
    {
      pinMode(PIN_R, OUTPUT);
      pinMode(PIN_G, INPUT);
      pinMode(PIN_B, INPUT);
      delay(300);

      pinMode(PIN_R, INPUT);
      pinMode(PIN_G, INPUT);
      pinMode(PIN_B, INPUT);
      delay(300);
    }

    for (int i = 0; i < 3; i++) 
    {
      pinMode(PIN_R, INPUT);
      pinMode(PIN_G, OUTPUT);
      pinMode(PIN_B, OUTPUT);
      delay(300);

      pinMode(PIN_R, INPUT);
      pinMode(PIN_G, INPUT);
      pinMode(PIN_B, INPUT);
      delay(200);
    }

    for (int i = 0; i < 3; i++) 
    {
      pinMode(PIN_R, OUTPUT);
      pinMode(PIN_G, OUTPUT);
      pinMode(PIN_B, OUTPUT);
      delay(300);

      pinMode(PIN_R, INPUT);
      pinMode(PIN_G, INPUT);
      pinMode(PIN_B, INPUT);
      delay(200);
    }

    pinMode(PIN_R, OUTPUT);
    pinMode(PIN_G, INPUT);
    pinMode(PIN_B, INPUT);

    delay(400);

    pinMode(PIN_R, OUTPUT);
    pinMode(PIN_G, OUTPUT);
    pinMode(PIN_B, INPUT);

    delay(400);

    pinMode(PIN_R, OUTPUT);
    pinMode(PIN_G, OUTPUT);
    pinMode(PIN_B, OUTPUT);
  }

  if (isEpileptics)
  {
    for (int i = 0; i < 10; i++)
      {
          pinMode(PIN_R, OUTPUT);  
          pinMode(PIN_G, OUTPUT);  
          pinMode(PIN_B, OUTPUT);  

          digitalWrite(PIN_R, HIGH);
          digitalWrite(PIN_G, HIGH);
          digitalWrite(PIN_B, HIGH);

          delay(50);

          digitalWrite(PIN_R, LOW);
          digitalWrite(PIN_G, LOW);
          digitalWrite(PIN_B, LOW);

          delay(50);
      }
      resetPins();
  }

  if (isPolice)
  {
    pinMode(PIN_R, INPUT);
    pinMode(PIN_G, INPUT);
    pinMode(PIN_B, OUTPUT);
    
    delay(200);

    digitalWrite(PIN_R, LOW);
    digitalWrite(PIN_G, LOW);
    digitalWrite(PIN_B, LOW);
    pinMode(PIN_R, OUTPUT);
    pinMode(PIN_G, INPUT);
    pinMode(PIN_B, INPUT);

    delay(200);
  }
}
