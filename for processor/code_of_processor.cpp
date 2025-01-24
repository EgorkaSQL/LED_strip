#include "BluetoothSerial.h"

#define PIN_R 14
#define PIN_G 26
#define PIN_B 33

BluetoothSerial SerialBT;

bool isBlinking = false;
bool isFading = false;
bool isPhonk = false;
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

void loop() {
  if (SerialBT.available()) 
  {
    char command = SerialBT.read();

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
}
