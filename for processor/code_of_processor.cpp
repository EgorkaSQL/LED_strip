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

unsigned long previousMillis = 0;
const unsigned long blinkInterval = 200;
const unsigned long longBlinkInterval = 500;
bool ledState = false;

int phonkStep = 0;

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

    if (command == '3') {
      blinkEnabled = true;
      fadeEnabled = false;
      Serial.println("Мигание включено");
    } else if (command == '4') {
      blinkEnabled = false;
      ledcWrite(LED_CHANNEL, 0);
      Serial.println("Мигание выключено");
    } else if (command == '5') {
      fadeEnabled = true;
      blinkEnabled = false;
      Serial.println("Затухание включено");
    } else if (command == '6') {
      fadeEnabled = false;
      ledcWrite(LED_CHANNEL, 0);
      Serial.println("Затухание выключено");
    } else if (command == '7') {
      phonkEnabled = true;
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
}
