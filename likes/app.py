# app.py
# "hello world" flask app

from flask import Flask, render_template, json, request
import mysql.connector
import json
import urllib.parse

app = Flask(__name__)

import css, images, js
app.register_blueprint(css.app)
app.register_blueprint(images.app)
app.register_blueprint(js.app)

@app.route("/")
def hello():
	q = request.args.get('q', '')
	p = ""
	if request.args.get('all_mei_like') != None:
		p = "?all_mei_like"
	return render_template('index.html', query = q, search_params = p)

@app.route("/search")
def search():
	data = search_likes()
#	return json.dumps(data)
	response = app.response_class(
		response = json.dumps(data),
		status = 200,
		mimetype = 'application/json'
	)
	return response

def search_likes():
	sql = "SELECT a.account_id, b.username, b.profile_pic_url, a.shortcode, DATE_FORMAT(a.taken_at_time, '%Y/%m/%d %H:%i:%s') AS taken_at_time, c.display_url_json "
	sql = sql + " FROM in_liked_shortcodes a JOIN in_accounts b ON (a.account_id = b.account_id) JOIN in_post_info c ON (a.shortcode = c.shortcode) "
	sql = sql + " WHERE a.deleted_flag = 0 "
	if request.args.get('all_mei_like') == None:
		sql = sql + " AND NOT EXISTS (SELECT 'x' FROM in_secrets s WHERE s.account_id = a.account_id) "
	sql = sql + " ORDER BY a.taken_at_time"
	conn = get_connection()
	cursor = conn.cursor()
	cursor.execute(sql)

	list = []
	count = 0
	for row in cursor:
		obj = {}
		count = count + 1
		obj['no'] = count
		obj['account_id'] = row[0].strip()
		obj['username'] = row[1]
		obj['profile_pic_url'] = row[2]
		obj['shortcode'] = row[3]
		obj['takenAtTime'] = row[4]

		display_exp = '<!-- ' + obj['takenAtTime'] + ' -->'
		for url in json.loads(row[5]):
			display_exp = display_exp + '<li><a href="images/liked_images/' + obj['account_id'] + '/' + url + '" data-lightbox="' + obj['shortcode'] + '" data-title="' + obj['username'] + '"><img src="images/liked_images/' + obj['account_id'] + '/t_' + url + '"></a></li>'
		obj['display_exp'] = "<ul>" + display_exp + "</ul>" + obj['takenAtTime']

		list.append(obj)

	return list

def get_connection():
	conn = mysql.connector.connect(
		host = 'localhost',
		port = 3306,
		user = 'root',
		password = 'Inahime5!',
		database = 'insta_presents',
	)
	conn.ping(reconnect = True)
	return conn

if __name__ == "__main__":
	app.run()
