# css.py
# ./css を静的フォルダとして定義する
from flask import Blueprint
app = Blueprint("css", __name__, static_url_path = '/css', static_folder = './css')
