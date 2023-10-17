import requests
from PIL import Image
import torch
from torchvision import transforms
import cv2
from efficientnet_pytorch import EfficientNet
import json
import mysql.connector

from sql_connect import *


def process_video(video_path, req_id):
    model = EfficientNet.from_pretrained('efficientnet-b0')

    t = ''
    if torch.cuda.is_available():
        t = 'cuda'
    else:
        t = 'cpu'

    model = model.to(t)

    video_capture = cv2.VideoCapture(video_path)
    total_frames = int(video_capture.get(cv2.CAP_PROP_FRAME_COUNT))
    k = 0
    tags = {}

    tfms = transforms.Compose([transforms.Resize(224), transforms.ToTensor(),
                               transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225]), ])

    labels_map = json.load(open('efficientnet_pytorch/labels_map.txt'))
    labels_map = [labels_map[str(i)] for i in range(1000)]

    start = time.time()
    while 1:
        ret, frame = video_capture.read()

        try:
            increase_progress(req_id, total_frames / (k / (time.time() - start)) - (time.time() - start),
                              "{:.2f}%".format(k * 100 / total_frames))
        except ZeroDivisionError:
            pass
        k += 1

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

    video_capture.release()

    penis = [item for item in sorted(tags.items(), key=lambda item: item[1], reverse=True) if item[1] >= 0.3]

    insert_data(req_id, str(penis))

    set_processed(req_id, penis)

    return penis


def set_processed(req_id, tags):
    host = "localhost"
    user = "newuser"
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
        url = f"http://localhost:6666/api/v/tag?tag={p[0]}&id={vid_id}"
        response = requests.post(url)

        print(response.status_code)

    cursor.close()
    connection.close()


set_processed("91a6e876-59b2-4be2-a17a-fb88102c4167",
              "[('web site, website, internet site, site', 0.4986461102962494)]")
