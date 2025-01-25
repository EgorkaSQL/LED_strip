# Документация к коду процессора

## Характеристики ##

+ В работе используется - ESP32-WROOM
+ Среда разработки - Arduino IDE 2.3.4 (требуется 1.8.10 и выше)
+ Используемая версия esp32 by Espressif Systems - 2.0.14
+ Board - ESP32 Dev Module
+ Port - COM7
+ Upload Speed - 912600 (в том числе и для Serial Monitor)
+ CPU Frequency - 240MHz (WI-FI/BT)
+ Библиотека - BluetoothSerial предустановленная в Arduino Core для ESP32
+ Схема системы для ленты - Schema1.jpg в этой же директории

## Ссылки ##

+ Arduino IDE - https://www.arduino.cc/en/software
+ Android Studio - https://developer.android.com/studio

## Про код ##

Данный код позволяет управлять светодиодной лентой подключенной к ESP32-WROOM через BT. \
Управление осуществляется с помощью текстовых команд, которые включают режимы свечения ленты.\
Команды отправляются с помощью BT-терминала находящимся в устройстве - телефон.

## Код ##

