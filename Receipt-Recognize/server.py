# -*- coding: utf-8 -*-

import base64
from flask import Flask, request
from datetime import datetime
import json
import reciept_recognize
from category_classifier import classification


classify = classification("model20230520235612_32.h5")
classify.predict("")

app = Flask(__name__)

data_dir = "./data/"

@app.route('/upload', methods=['POST'])
def upload_image():
    # 전송된 데이터 받기
    encoded_image = request.data
    
    now = datetime.utcnow().strftime('%Y%m%d_%H%M%S_%f')
    filename = now + '.jpg'
    # base64 디코딩 후 이미지 파일로 저장
    with open(data_dir + filename, 'wb') as image_file:
        image_file.write(base64.b64decode(encoded_image))
    
    try:
        result = reciept_recognize.main(data_dir + filename)
        Array = []
        for item in result:
            temp_cat = classify.predict(item[0])
            temp = {"ProductName" : item[0], "UnitPrice" : item[1], 
                      "Quantity" : item[2], "Price" : item[3], 
                      "Category" : temp_cat,
                      "Exp" : int(classify.predict_exp(temp_cat)) }
            Array.append(temp.copy())
    except:
        Array = []
        
    JsonArray = json.dumps(Array)
    
    return JsonArray

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)