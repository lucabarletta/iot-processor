# Setup
## Prerequisities
- Docker Compose Desktop (v2.23.3-desktop or higher)
- Terminal (or VS Code with Docker Plugin)

## Running the System
1. Clone this project
2. Start Docker Engine (running Docker Desktop will start the engine)
3. Go to directory ```cd .\iot-processor\docker-compose```
4. Start infrastructure (-d detached mode) ```docker-compose up -d --build``` 
5. Open backend logs in a new terminal ```docker compose logs backend --follow```
6. Open testdata generator logs in a new terminal ```docker compose logs testdata-generator --follow```
9. Start testdata generator (Port 5000) with the follow POST Request:
 ``` 
 curl
  --request POST \
  --url http://localhost:5000/start \
  --header 'Content-Type: application/json' \
  --data '{
    "sleep_time": 0.01,
    "sensor_range": 10,
    "min_value": 35,
    "max_value": 50
    }' 
  ```

11. Stop testdata generator with:
``` 
curl 
  --request POST \
  --url http://localhost:5000/stop \
  --header 'Content-Type: application/json'
  ```

### Testdata Generator Parameters
| param  |  definition  |
|---|---|
| 0.01  | intervall generated data in seconds  |
|  10 | unique count of sensors  |
| 35 | min value of random generated values  |
|  50   | max value of random generated values  |

Caution with use intervals higher then 0.00001 and count of sensors higher then 1000.
