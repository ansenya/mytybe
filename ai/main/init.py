import json
import mysql.connector

host = "5.180.174.71"
user = "user"
password = ""
database = "tube"

connection = mysql.connector.connect(
    host=host,
    user=user,
    password=password,
    database=database
)

cursor = connection.cursor()

data = json.load(open('labels_map.txt'))

i = 1
for key, value in data.items():
    query = "INSERT INTO tags (id, en_tag) VALUES (%s, %s)"
    values = (i, value)
    cursor.execute(query, values)
    connection.commit()
    i += 1


data = open('classes.txt').readlines()

i = 1001
for value in data:
    query = "INSERT INTO tags (id, en_tag) VALUES (%s, %s)"
    values = (i, value.strip())
    cursor.execute(query, values)
    connection.commit()
    i += 1

data = open('ru_tags.txt').readlines()

for i in range(999, len(data)):
    query = "UPDATE tags SET ru_tag = %s where id = %s"
    values = (data[i].strip(), i + 1)
    cursor.execute(query, values)
    connection.commit()

cursor.close()
connection.close()
