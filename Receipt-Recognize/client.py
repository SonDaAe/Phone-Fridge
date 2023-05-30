# -*- coding: utf-8 -*-
"""
Created on Sun May  7 15:08:56 2023

@author: wndnj
"""

import base64
import requests

# 이미지 파일 경로
image_path = 't4.jpg'

print(image_path)

# 이미지 파일을 base64로 인코딩
with open(image_path, 'rb') as image_file:
    encoded_image = base64.b64encode(image_file.read())


# 서버로 이미지 전송
response = requests.post('http://iriya.iptime.org:5000/upload', data=encoded_image)

print(response.json())  # 서버 응답 출력