# images.py
# ./images を静的フォルダとして定義する
from flask import Blueprint
app = Blueprint("images", __name__, static_url_path = '/images', static_folder = './images')
