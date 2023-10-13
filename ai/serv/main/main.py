import threading
from collections import OrderedDict

from flask import Flask, request, jsonify, send_file
from multiprocessing import BoundedSemaphore

from utils import *
from sql_connect import *

app = Flask(__name__)

semaphore = BoundedSemaphore(3)


@app.route('/process', methods=['POST'])
def upload_video():
    uploaded_file = request.files['video']

    req_id = request.args['uuid']

    video_path = "serv/videos/{}.mp4".format(req_id)
    uploaded_file.save(video_path)

    create_result(req_id, uploaded_file.filename.split(".")[1])

    def process():
        with semaphore:
            process_video(video_path, req_id)

    processing_thread = threading.Thread(target=process)
    processing_thread.start()

    return {'id': req_id}


@app.route('/progress', methods=['GET'])
def progress():
    req_id = str(request.args.get('id'))
    pr = get_progress(req_id)

    response = OrderedDict()
    response['id'] = req_id
    response['progress'] = pr[0]
    response['eta'] = pr[1]

    return jsonify(response)


@app.route('/result', methods=['GET'])
def result():
    req_id = str(request.args.get('id'))
    s = get_result(req_id).replace("'", '"').replace('("', "").replace('",)', "").replace("\\", "")
    if s == 'None':
        return {}, 425
    return {'id': req_id, 'tags': [{tag: probability} for tag, probability in
                                   json.loads(s).items()]}


@app.route('/video', methods=['GET'])
def video():
    req_id = str(request.args.get('id'))
    return send_file("../videos/" + f'{req_id}.{get_type(req_id)}', as_attachment=False)


if __name__ == '__main__':
    init_db()
    not_done_list = get_not_done()


    def do_process(not_done_item):
        with semaphore:
            print(not_done_item)
            process_video("serv/videos/{}.mp4".format(not_done_item[0]), not_done_item[0])


    processing_threads = []
    for not_done in not_done_list:
        processing_threads.append(threading.Thread(target=do_process, args=(not_done,)))

    for task in processing_threads:
        task.start()

    app.run(host='0.0.0.0', port=8642)
