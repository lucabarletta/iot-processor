import json
import random
import threading
import time
from datetime import datetime

import pika
from flask import Flask, request, jsonify

thread = None

# Flask web server setup
app = Flask(__name__)

# Function to send messages, now with parameters
def send_messages(sleep_time, sensor_range, min_value, max_value):
    global continue_sending

    rabbitmq_host = 'rabbitmq'
    rabbitmq_port = 5672
    queue_name = 'iot.queue'
    credentials = pika.PlainCredentials('admin', 'password1234')

    connection = pika.BlockingConnection(
        pika.ConnectionParameters(credentials=credentials, host=rabbitmq_host, port=rabbitmq_port))
    channel = connection.channel()
    channel.queue_declare(queue=queue_name, durable=False)

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
    finally:
        connection.close()

# Start the message sending process via REST call with parameters
@app.route('/start', methods=['POST'])
def start_script():
    global continue_sending, thread

    # Default values
    default_sleep_time = 0.1
    default_sensor_range = 10
    default_max_value = 150
    default_min_value = 50

    data = request.json
    sleep_time = data.get('sleep_time', default_sleep_time)
    sensor_range = data.get('sensor_range', default_sensor_range)
    min_value = data.get('min_value', default_min_value)
    max_value = data.get('max_value', default_max_value)

    if thread and thread.is_alive():
        return jsonify({'status': 'Already running'}), 400

    continue_sending = True
    thread = threading.Thread(target=send_messages, args=(sleep_time, sensor_range, min_value, max_value))
    thread.start()

    return jsonify({'status': 'Started'}), 200

# Stop the message sending process via REST call
@app.route('/stop', methods=['POST'])
def stop_script():
    global continue_sending, thread

    if thread and thread.is_alive():
        continue_sending = False
        thread.join()
        return jsonify({'status': 'Stopped'}), 200
    else:
        return jsonify({'status': 'Not running'}), 400


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
