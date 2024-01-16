import sys

import pika
import json
import random
import time
import threading
from datetime import datetime, timezone

rabbitmq_host = 'localhost'
rabbitmq_port = 5672
queue_name = 'iot.queue'
credentials = pika.PlainCredentials('user', 'password1234')
continue_sending = True

# Default values
default_sleep_time = 10
default_sensor_range = 10
default_max_value = 150
default_min_value = 50

if len(sys.argv) >= 5:
    try:
        sleep_time = float(sys.argv[1])
        sensor_range = int(sys.argv[2])
        min_value = int(sys.argv[3])
        max_value = int(sys.argv[4])

    except ValueError:
        print("Invalid arguments. Using default values.")
        sleep_time = default_sleep_time
        sensor_range = default_sensor_range
        min_value = default_min_value
        max_value = default_max_value
else:
    print("Not enough arguments. Using default values.")
    sleep_time = default_sleep_time
    sensor_range = default_sensor_range
    min_value = default_min_value
    max_value = default_max_value


def send_messages():
    global continue_sending

    connection = pika.BlockingConnection(
        pika.ConnectionParameters(credentials=credentials, host=rabbitmq_host, port=rabbitmq_port, ))
    channel = connection.channel()
    channel.queue_declare(queue=queue_name, durable=True)

    try:
        while continue_sending:
            test_data = {
                "value": round(random.uniform(min_value, max_value), 2),
                "sensorId": f"sensor{random.randint(1, sensor_range)}",
                "customerId": "Customer1",
                "time": datetime.utcnow().isoformat(timespec='milliseconds') + 'Z'
            }

            message = json.dumps(test_data)
            channel.basic_publish(exchange='iot.exchange',
                                  routing_key='',
                                  body=message,
                                  properties=pika.BasicProperties(
                                      delivery_mode=2,
                                  ))

            print(f" [x] Sent {message}")

            time.sleep(sleep_time)

    except KeyboardInterrupt:
        print("Stopping message sending...")
        connection.close()


thread = threading.Thread(target=send_messages)
thread.start()

try:
    while thread.is_alive():
        thread.join(timeout=1)

except KeyboardInterrupt:
    print("Stopping message sending...")
    continue_sending = False
    thread.join()

if __name__ == '__main__':
    send_messages()
