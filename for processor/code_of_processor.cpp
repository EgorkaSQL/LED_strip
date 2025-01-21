#include "BluetoothSerial.h"

#undef LED_BUILTIN
#define LED_BUILTIN 14
#define LED_CHANNEL 0
#define LED_RESOLUTION 8
#define LED_FREQ 5000

BluetoothSerial SerialBT;

bool blinkEnabled = false;
bool fadeEnabled = false;
bool phonkEnabled = false;
bool sosEnabled = false;

unsigned long previousMillis = 0;
const unsigned long blinkInterval = 200;
const unsigned long longBlinkInterval = 500;
const unsigned long sosPauseInterval = 3000;
bool ledState = false;

int phonkStep = 0;
int sosStep = 0;

void setup() {
  pinMode(LED_BUILTIN, OUTPUT);
  digitalWrite(LED_BUILTIN, LOW);
  ledcSetup(LED_CHANNEL, LED_FREQ, LED_RESOLUTION);
  ledcAttachPin(LED_BUILTIN, LED_CHANNEL);

  ledcWrite(LED_CHANNEL, 0);

  Serial.begin(115200);
  delay(1000);
  SerialBT.begin("ESP32_Bluetooth");
  Serial.println("ESP32 запущен!");
  SerialBT.println("Bluetooth готов!");
}

void loop() {
  if (SerialBT.available()) {
    char command = SerialBT.read();
    Serial.print("Получено: ");
    Serial.println(command);

    if (command == 'B') {
      String brightnessValue = "";
      while (SerialBT.available()) {
        char ch = SerialBT.read();
        if (isDigit(ch)) {
          brightnessValue += ch;
        } else {
          break;
        }
      }

      int brightness = brightnessValue.toInt();
      brightness = constrain(brightness, 0, 255);

      ledcWrite(LED_CHANNEL, brightness);
      Serial.print("Яркость изменена на: ");
      Serial.println(brightness);
    }

    if (command == '3') {
      blinkEnabled = true;
      fadeEnabled = false;
      sosEnabled = false;
      Serial.println("Мигание включено");
    } else if (command == '4') {
      blinkEnabled = false;
      ledcWrite(LED_CHANNEL, 0);
      Serial.println("Мигание выключено");
    } else if (command == '5') {
      fadeEnabled = true;
      blinkEnabled = false;
      sosEnabled = false;
      Serial.println("Затухание включено");
    } else if (command == '6') {
      fadeEnabled = false;
      ledcWrite(LED_CHANNEL, 0);
      Serial.println("Затухание выключено");
    } else if (command == '7') {
      phonkEnabled = true;
      sosEnabled = false;
      blinkEnabled = false;
      fadeEnabled = false;
      phonkStep = 0;
      ledState = false;
      previousMillis = millis();
      Serial.println("Фонк включено");
    } else if (command == '8') {
      phonkEnabled = false;
      ledcWrite(LED_CHANNEL, 0);
      Serial.println("Фонк выключено");
    } else if (command == '9') {
      sosEnabled = true;
      blinkEnabled = false;
      fadeEnabled = false;
      phonkEnabled = false;
      sosStep = 0;
      ledcWrite(LED_CHANNEL, 0);
      previousMillis = millis();
      Serial.println("Режим SOS включён");
    } else if (command == '10') {
      sosEnabled = false;
      ledcWrite(LED_CHANNEL, 0);
      Serial.println("Режим SOS выключен");
    }
  }

  if (blinkEnabled) {
    unsigned long currentMillis = millis();
    if (currentMillis - previousMillis >= blinkInterval) {
      previousMillis = currentMillis;
      ledState = !ledState;
      ledcWrite(LED_CHANNEL, ledState ? 255 : 0);
    }
  }

  if (fadeEnabled) {
    for (int brightness = 0; brightness <= 255; brightness++) {
      ledcWrite(LED_CHANNEL, brightness);
      delay(10);
    }
    for (int brightness = 255; brightness >= 0; brightness--) {
      ledcWrite(LED_CHANNEL, brightness);
      delay(10);
    }
  }

  if (phonkEnabled) {
    unsigned long currentMillis = millis();
    unsigned long interval = (phonkStep < 3) ? longBlinkInterval : blinkInterval;

    if (currentMillis - previousMillis >= interval) {
      previousMillis = currentMillis;
      ledState = !ledState;
      ledcWrite(LED_CHANNEL, ledState ? 255 : 0);

      if (!ledState) {
        phonkStep++;
      }

      if (phonkStep >= 9) {
        phonkStep = 0;
      }
    }
  }

  if (sosEnabled) {
    unsigned long currentMillis = millis();
    unsigned long interval;

    if (sosStep >= 18) {
      interval = sosPauseInterval;
      if (currentMillis - previousMillis >= interval) {
        previousMillis = currentMillis;
        sosStep = 0;
      }
      return;
    } 

    if (sosStep % 2 == 0) {
      interval = (sosStep / 6 == 1) ? longBlinkInterval : blinkInterval;
      ledState = true;
    } else {
      interval = blinkInterval;
      ledState = false;
    }

    if (currentMillis - previousMillis >= interval) {
      previousMillis = currentMillis;

      ledcWrite(LED_CHANNEL, ledState ? 255 : 0);
      sosStep++;
    }
  }
}
