FROM python:3.9.10-alpine3.14
RUN mkdir -p /srv
WORKDIR /srv
RUN pip install --upgrade pip
RUN pip install flask
RUN pip install pika
COPY . /srv
ENV FLASK_APP=app
CMD ["python","main.py"]