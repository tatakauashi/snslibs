# js.py
# ./js を静的フォルダとして定義する
from flask import Blueprint
app = Blueprint("js", __name__, static_url_path = '/js', static_folder = './js')
