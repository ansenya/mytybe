#!/bin/bash
# shellcheck disable=SC2164
cd ai
source venv/bin/activate
pip install -r serv/res/r.txt
nohup python3 serv/main/main.py > ai.txt &
