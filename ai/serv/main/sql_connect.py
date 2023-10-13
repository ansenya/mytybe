import sqlite3
import time

base_url = "serv/res/results.db"


def init_db():
    conn = sqlite3.connect(base_url)
    cursor = conn.cursor()
    cursor.execute('''CREATE TABLE IF NOT EXISTS results 
                            (id TEXT PRIMARY KEY, data TEXT, progress Text, start INT, eta TEXT, type TEXT)''')
    conn.commit()
    conn.close()


def create_result(req_id, vtype):
    conn = sqlite3.connect(base_url)
    cursor = conn.cursor()
    cursor.execute('INSERT INTO results (id, data, progress, start, type) values (?, ?, ?, ?, ?)',
                   (req_id, "", "0%", time.time(), vtype))
    conn.commit()
    conn.close()


def get_result(req_id):
    conn = sqlite3.connect(base_url)
    cursor = conn.cursor()
    res = str(cursor.execute('SELECT data FROM results WHERE id = ?', (req_id,)).fetchone())
    conn.commit()
    conn.close()
    return res


def increase_progress(req_id, eta, progress):
    conn = sqlite3.connect(base_url)
    cursor = conn.cursor()
    cursor.execute('UPDATE results SET progress = ?, eta = ? WHERE id = ?', (progress, eta, req_id))
    conn.commit()
    conn.close()


def get_progress(req_id):
    conn = sqlite3.connect(base_url)
    cursor = conn.cursor()
    res = cursor.execute('SELECT progress, eta FROM results WHERE id = ?', (req_id,)).fetchone()
    conn.commit()
    conn.close()
    return res


def insert_data(req_id, data):
    conn = sqlite3.connect(base_url)
    cursor = conn.cursor()
    cursor.execute('UPDATE results SET data = ? WHERE id = ?', (data, req_id))
    conn.commit()
    conn.close()


def get_type(req_id):
    conn = sqlite3.connect(base_url)
    cursor = conn.cursor()
    res = cursor.execute('SELECT type FROM results WHERE id = ?', (req_id,)).fetchone()
    conn.commit()
    conn.close()
    return str(res).replace("('", "").replace("',)", "")


def get_not_done():
    conn = sqlite3.connect(base_url)
    cursor = conn.cursor()
    res = cursor.execute('SELECT id, type FROM results WHERE eta > 0').fetchall()
    conn.commit()
    conn.close()
    return res
