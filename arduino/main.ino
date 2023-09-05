#include <WiFiNINA.h>
#include <OneWire.h>
#include <SPI.h>
#include <SD.h>
#include <ArduinoModbus.h>
#include <DallasTemperature.h>
#include <utility/wifi_drv.h>

const int chipSelect = 4;
#define ONE_WIRE_BUS 7

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);
DeviceAddress Thermometer;
int deviceCount = 0;
char server[] = "deayk.vasilis.pw";// Server address
String location = "Demo"; // Location identifier
String meter = "Kamstrup Multical 603"; // Meter identifier
const unsigned long REPORT_INTERVAL = 15000; // Reporting interval in milliseconds
WiFiClient client;

// WiFi credentials for multiple networks
char ssid[] = "vNet";
char pass[] = "Vasilis123";
char ssid2[] = "giannis and xaris wireless";
char pass2[] = "78175EDB8A...?GUITAR???(HELLO)";
char ssid3[] = "vSpot";
char pass3[] = "88888888";

int status = WL_IDLE_STATUS; // Wifi radio's status

void printData() {
  Serial.println("Board Information:");
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);

  Serial.println();
  Serial.println("Network Information:");
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());
  long rssi = WiFi.RSSI();
  Serial.print("Signal Strength (RSSI): ");
  Serial.println(rssi);
  byte encryption = WiFi.encryptionType();
  Serial.print("Encryption Type: ");
  Serial.println(encryption, HEX);
  Serial.println();
}

void printAddress(DeviceAddress deviceAddress) {
  for (uint8_t i = 0; i < 8; i++) {
    Serial.print("0x");
    if (deviceAddress[i] < 0x10) Serial.print("0");
    Serial.print(deviceAddress[i], HEX);
    if (i < 7) Serial.print(", ");
  }
  Serial.println("");
}

void setup() {
  WiFiDrv::pinMode(25, OUTPUT); // Define green pin
  WiFiDrv::pinMode(26, OUTPUT); // Define red pin
  WiFiDrv::pinMode(27, OUTPUT); // Define blue pin

  WiFiDrv::analogWrite(25, 0);
  WiFiDrv::analogWrite(26, 255);
  WiFiDrv::analogWrite(27, 0);

  Serial.begin(9600);

  Serial.print("Initializing SD card...");
  if (!SD.begin(chipSelect)) {
    Serial.println("Card failed, or not present");
  }
  Serial.println("SD card initialized.");

  status = WiFi.begin(ssid, pass);

  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present");
    while (true);
  }

  int attempts = 3;
  while (status != WL_CONNECTED && attempts > 0) {
    Serial.print("Attempting to connect to network: ");
    Serial.println(ssid);
    status = WiFi.begin(ssid, pass);
    attempts--;
    delay(10000);
  }

  if (status != WL_CONNECTED) {
    attempts = 3;
    while (status != WL_CONNECTED && attempts > 0) {
      Serial.print("Attempting to connect to network: ");
      Serial.println(ssid2);
      status = WiFi.begin(ssid2, pass2);
      attempts--;
      delay(10000);
    }
  }

  if (status != WL_CONNECTED) {
    attempts = 3;
    while (status != WL_CONNECTED && attempts > 0) {
      Serial.print("Attempting to connect to network: ");
      Serial.println(ssid3);
      status = WiFi.begin(ssid3, pass3);
      attempts--;
      delay(10000);
    }
  }

  if (status == WL_CONNECTED) {
    WiFiDrv::analogWrite(25, 255);
    WiFiDrv::analogWrite(26, 0);
    WiFiDrv::analogWrite(27, 0);
  }

  Serial.println("Connected to the network");
  Serial.println("----------------------------------------");
  printData();
  Serial.println("----------------------------------------");
  Serial.println("Initializing sensors...");
  sensors.begin();

  Serial.println("Initializing Modbus RTU...");
  if (!ModbusRTUClient.begin(9600)) {
    Serial.println("Failed to start Modbus RTU Client!");
    while (1);
  }
}

unsigned long lastMillis = 0;

String readHoldingRegisterValues() {
  Serial.print("Reading Holding Register values ... ");
  int registerNumber = 0;
  String query = "";

  if (!ModbusRTUClient.requestFrom(229, HOLDING_REGISTERS, 0x00, 82)) {
    Serial.print("failed! ");
    WiFiDrv::analogWrite(25, 0);
    WiFiDrv::analogWrite(26, 255);
    WiFiDrv::analogWrite(27, 0);
    Serial.println(ModbusRTUClient.lastError());
  } else {
    Serial.println("success");

    while (ModbusRTUClient.available()) {
      query = query + "&reg" + String(registerNumber) + "=" + String(ModbusRTUClient.read());
      registerNumber++;
    }
    Serial.println(query);
    WiFiDrv::analogWrite(25, 0);
    WiFiDrv::analogWrite(26, 0);
    WiFiDrv::analogWrite(27, 255);

    if (!ModbusRTUClient.requestFrom(229, HOLDING_REGISTERS, 0x52, 41)) {
      Serial.print("failed! ");
      Serial.println(ModbusRTUClient.lastError());
      WiFiDrv::analogWrite(25, 0);
      WiFiDrv::analogWrite(26, 255);
      WiFiDrv::analogWrite(27, 0);
    } else {
      Serial.println("success");

      while (ModbusRTUClient.available()) {
        query = query + "&reg" + String(registerNumber) + "=" + String(ModbusRTUClient.read());
        registerNumber++;
      }
      Serial.println(query);

      if (!ModbusRTUClient.requestFrom(229, HOLDING_REGISTERS, 0x7B, 30)) {
        Serial.print("failed! ");
        Serial.println(ModbusRTUClient.lastError());
        WiFiDrv::analogWrite(25, 0);
        WiFiDrv::analogWrite(26, 255);
        WiFiDrv::analogWrite(27, 0);
      } else {
        Serial.println("success");

        while (ModbusRTUClient.available()) {
          query = query + "&reg" + String(registerNumber) + "=" + String(ModbusRTUClient.read());
          registerNumber++;
        }
        Serial.println(query);
      }
    }
  }

  return query;
}

void loop() {
  if (millis() - lastMillis > REPORT_INTERVAL) {
    lastMillis = millis();
    String query = readHoldingRegisterValues();

    Serial.println("Sending data to server...");
    if (client.connect(server, 80)) {
      Serial.println("Connected to server");
      client.println("GET /registers?location=" + location + "&meter=" + meter + query + " HTTP/1.1");
      client.println("Host: deayk.vasilis.pw");
      client.println("Connection: close");
      client.println();
    }
    Serial.println("Data sent to server.");
    Serial.println("---------------------------------------");
    delay(500);
  }
}
