/*macro definitions of the sound sensor and the LED*/
#define SOUND_SENSOR A0
#define LED 3      // the number of the LED pin
 
#define THRESHOLD_VALUE 400//The threshold to turn the led on 400.00*5/1024 = 1.95v
void setup()
{
    Serial.begin(9600);
    pins_init();
}
 
void loop()
{
    int sensorValue = analogRead(SOUND_SENSOR);//use A0 to read the electrical signal
    Serial.print("sensorValue ");
    Serial.println(sensorValue);
    if(sensorValue > THRESHOLD_VALUE && sensorValue <= THRESHOLD_VALUE + 100)
    {
      //if the value read from A0 is larger than 400,then light the LED
        setLEDLevel(4);
        delay(200);
    }
    else if(sensorValue > THRESHOLD_VALUE + 100 && sensorValue <= THRESHOLD_VALUE + 200)
    {
        setLEDLevel(3);//if the value read from A0 is larger than 400,then light the LED
        delay(200);
    }
    else if(sensorValue > THRESHOLD_VALUE + 200 && sensorValue <= THRESHOLD_VALUE + 300)
    {
        setLEDLevel(2);//if the value read from A0 is larger than 400,then light the LED
        delay(200);
    }
    else if(sensorValue > THRESHOLD_VALUE + 300 && sensorValue <= THRESHOLD_VALUE + 400)
    {
        setLEDLevel(1);//if the value read from A0 is larger than 400,then light the LED
        delay(200);
    }
    else if(sensorValue > THRESHOLD_VALUE + 400)
    {
        //when the noise exceed max level, we turn off the light as whole
        setLEDLevel(0);
        delay(200);
    }
    else
    {
      //when there is no noise, we set the light to basic of nightlight
      setLEDLevel(5);
    }
    
}
 
void pins_init()
{
    pinMode(LED, OUTPUT);
    pinMode(SOUND_SENSOR, INPUT);
}


void setLEDLevel(int level)
{
    //TODO: Once have access to light API, we can program the level at the light level
    
}
