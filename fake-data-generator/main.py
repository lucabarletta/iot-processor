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


def send_messages():
    global continue_sending

    connection = pika.BlockingConnection(
        pika.ConnectionParameters(credentials=credentials, host=rabbitmq_host, port=rabbitmq_port, ))
    channel = connection.channel()
    channel.queue_declare(queue=queue_name, durable=True)

    try:
        while continue_sending:
            test_data = {
                "value": round(random.uniform(50, 150), 2),
                "sensorId": f"sensor{random.randint(1, 10)}",
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

            time.sleep(0.001)

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
