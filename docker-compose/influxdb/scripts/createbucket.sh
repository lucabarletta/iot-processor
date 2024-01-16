#!/bin/bash
set -e
influx bucket create -n sensordata -r 1d
