import requests
from PIL import Image
import torch
from torchvision import transforms
import cv2
from efficientnet_pytorch import EfficientNet
import json
import mysql.connector

from moviepy.editor import VideoFileClip

from sql_connect import *

print(torch.cuda.is_available())


def process_video(video_path, req_id):
    video_capture = cv2.VideoCapture(video_path)
    total_frames = int(video_capture.get(cv2.CAP_PROP_FRAME_COUNT))
    # total_frames = 2 * int(video_capture.get(cv2.CAP_PROP_FRAME_COUNT))
    start = time.time()
    video_capture.release()

    finish = []
    for i in process_ef(start, total_frames, video_path, req_id):
        finish.append(i)
    # for i in list(process_yolo(start, total_frames, video_path, req_id)):
    #     finish.append(i)

    print(finish)

    insert_data(req_id, str(finish))
    set_processed(req_id, finish)

    # insert_data(req_id, str(piska))
    # set_processed(req_id, list(piska))

    set_time(req_id)


def process_ef(start, total_frames, video_path, req_id):
    model = EfficientNet.from_pretrained('efficientnet-b0')
    video_capture = cv2.VideoCapture(video_path)

    if torch.cuda.is_available():
        t = 'cuda'
    else:
        t = 'cpu'

    model = model.to(t)

    k = 0
    tags = {}

    tfms = transforms.Compose([transforms.Resize(224), transforms.ToTensor(),
                               transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225]), ])

    labels_map = json.load(open('../efficientnet_pytorch/labels_map.txt'))
    labels_map = [labels_map[str(i)] for i in range(1000)]

    while 1:
        ret, frame = video_capture.read()

        try:
            increase_progress(req_id, total_frames / (k / (time.time() - start)) - (time.time() - start),
                              "{:.2f}%".format(k * 100 / total_frames))
        except ZeroDivisionError:
            pass
        k += 1

        # if k > 100:
        #     break

        if not ret:
            break

        frame_pil = Image.fromarray(cv2.cvtColor(frame, cv2.COLOR_BGR2RGB))

        img = tfms(frame_pil).unsqueeze(0)
        img = img.to(t)

        model.eval()
        with torch.no_grad():
            outputs = model(img)

        for idx in torch.topk(outputs, k=5).indices.squeeze(0).tolist():
            prob = torch.softmax(outputs, dim=1)[0, idx].item()
            if labels_map[idx] not in tags:
                tags[labels_map[idx]] = prob

            elif tags[labels_map[idx]] < prob:
                tags[labels_map[idx]] = prob

    penis = [item[0] for item in sorted(tags.items(), key=lambda item: item[1], reverse=True) if item[1] >= 0.5]

    return penis


def process_yolo(start, total_frames, video_path, req_id):
    # print(cv2.getBuildInformation())
    video_capture = cv2.VideoCapture(video_path)

    tags = set()

    Conf_threshold = 0.4
    NMS_threshold = 0.4

    with open('fclasses.txt', 'r') as f:
        class_name = [cname.strip() for cname in f.readlines()]
    net = cv2.dnn.readNet('yolov4-tiny.weights', 'yolov4-tiny.cfg')
    net.setPreferableBackend(cv2.dnn.DNN_BACKEND_CUDA)
    net.setPreferableTarget(cv2.dnn.DNN_TARGET_CUDA_FP16)

    model = cv2.dnn_DetectionModel(net)
    model.setInputParams(size=(416, 416), scale=1 / 255, swapRB=True)

    k = total_frames / 2
    while True:
        ret, frame = video_capture.read()

        try:
            increase_progress(req_id, total_frames / (k / (time.time() - start)) - (time.time() - start),
                              "{:.2f}%".format(k * 100 / total_frames))
        except ZeroDivisionError:
            pass
        k += 1

        # if k > 100:
        #     break

        if not ret:
            break
        classes, scores, boxes = model.detect(frame, Conf_threshold, NMS_threshold)
        for (classid, score, box) in zip(classes, scores, boxes):
            if score >= 0.5:
                tags.add(class_name[classid])

    video_capture.release()

    return tags


def set_processed(req_id, tags):
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

    cursor.execute("UPDATE videos SET processed = %s WHERE vid_uuid = %s",
                   (True, req_id))
    connection.commit()

    cursor.execute("SELECT id FROM videos WHERE vid_uuid = %s", (req_id,))

    vid_id = int(cursor.fetchone()[0])

    for p in tags:
        url = f"http://localhost:1984/api/v/tag?tag={p}&id={vid_id}"
        response = requests.post(url)
        print(response.status_code)

    url = f"http://localhost:1984/api/v/done?id={vid_id}"
    requests.post(url)

    cursor.close()
    connection.close()


def set_time(req_id):
    video = VideoFileClip(f"../videos/{req_id}.mp4")
    duration = video.duration

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

    cursor.execute("UPDATE videos SET duration = %s WHERE vid_uuid = %s",
                   (int(duration), req_id))
    connection.commit()

    cursor.close()
    connection.close()
