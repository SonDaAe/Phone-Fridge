# -*- coding: utf-8 -*-

import os
import sys
import io
import re
import json
import cv2
import math, numpy as np
from google.cloud import vision

os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = 'service_secret_key_2.json'
temp_file_name = "t18.jpg"

label_header_name = ["제품명", "상품명"]
label_header_unit_price = ["단가"]
label_header_quantity = ["수량"]
label_header_price = ["금액"]
label_header_value = label_header_unit_price + label_header_quantity + label_header_price
label_header = label_header_value + label_header_name

label_end_keyword = ["합계", "부가세", "과세", "부가", "가세", "판매액", "품목", "면세", "물품", "결제"]

height = 0
width = 0

def find_left(item):
    value = (item[1][0][0] + item[1][3][0]) / 2
    return value

def find_right(item):
    value = (item[1][1][0] + item[1][2][0]) / 2
    return value

def text_detect(file_name):
    with io.open(file_name, 'rb') as image_file:
        content = image_file.read()
    client = vision.ImageAnnotatorClient()
    image = vision.Image(content=content)
    response = client.text_detection(image=image)
    texts = response.text_annotations
    return texts

def calc_slope(texts):
    pattern = '[ㄱ-ㅎㅏ-ㅣ가-힣]{2,}'
    slopes = []
    for text in texts[1:]:
        contents = text.description
        verts = [[vertex.x, vertex.y]
                    for vertex in text.bounding_poly.vertices]
        if re.search(pattern, contents) is not None:
            slope = (verts[1][1]-verts[0][1])/(verts[1][0]-verts[0][0])
            slopes.append(slope)
    average = sum(slopes) / len(slopes)
    degree = np.arctan(average)
    degree = math.degrees(degree)
    return degree
    
def rotate_image(file_name, degree):
    img = cv2.imread(file_name)
    global height, width
    height, width, _ = img.shape
    #중심 기준으로 회전
    center = (img.shape[1] // 2, img.shape[0] // 2)
    M = cv2.getRotationMatrix2D(center, degree, 1.0)
    rotated = cv2.warpAffine(img, M, (img.shape[1], img.shape[0]))
    cv2.imwrite(temp_file_name, rotated)
    
def find_bound(texts):
    pattern = '[ㄱ-ㅎㅏ-ㅣ가-힣]{2,}'
    bound = dict()
    text_list = []
    left_bound = width
    right_bound = 0
    bottom_bound = 0
    top_bound = 0
    for text in texts[1:]:
    #    print('\n"{}"'.format(text.description))
        contents = text.description
        vertex = [[vertex.x, vertex.y] for vertex in text.bounding_poly.vertices]
        text_list.append([contents, vertex])
        if contents in label_header:
            bound[contents] = vertex
        if re.search(pattern, contents) is not None:
            if ((vertex[0][0] + vertex[3][0]) / 2) < left_bound:
                left_bound = (vertex[0][0] + vertex[3][0]) / 2
            if ((vertex[1][0] + vertex[2][0]) / 2) > right_bound:
                right_bound = (vertex[1][0] + vertex[2][0]) / 2
            if ((vertex[2][1] + vertex[3][1]) / 2) > bottom_bound:
                bottom_bound = (vertex[2][1] + vertex[3][1]) / 2
    
    # 상품명, 단가를 기준으로 위쪽 경계선 설정
    for key, value in bound.items():
        top_bound = top_bound + int((value[1][1] + value[2][1]) / 2)
    top_bound = int(top_bound / len(bound))
    bottom_bound = int(bottom_bound)
    
    # x 경계범위가 이미지를 넘어서는 경우의 예외처리
    left_bound = int(left_bound - 200)
    right_bound = int(right_bound + 200)
    if left_bound < 0:
        left_bound = 0
    if right_bound > width:
        right_bound = width
        
    return ([top_bound, bottom_bound, left_bound, right_bound], text_list)

def extract_purchase_line(texts):
    bounds, text_list = find_bound(texts)
    bound = dict()
    sorted_line_list = []
    line_list = []
    temp_list = []
    perchase_list = []
    continuity = 2
    line_pos = -1
    top_check = 1
    sorted_list = sorted(text_list, key=lambda x: (x[1][0][1] + x[1][1][1] + x[1][2][1] + x[1][3][1]) / 2)
    for item in sorted_list:
        top = (item[1][0][1] + item[1][1][1]) / 2
        bottom = (item[1][2][1] + item[1][3][1]) / 2
        left = (item[1][0][0] + item[1][3][0]) / 2
        right = (item[1][1][0] + item[1][2][0]) / 2
        
        if line_pos == -1:
            temp_list.append(item)
            line_pos = (top + bottom) / 2
        else:
            if (top <= line_pos <= bottom) and (left >= bounds[2]) and (right <= bounds[3]):
                temp_list.append(item)
            else:
                line_list.append(temp_list.copy())
                temp_list.clear()
                temp_list.append(item)
                line_pos = (top + bottom) / 2
    line_list.append(temp_list.copy())
                
    for row in line_list:
        sorted_row = sorted(row, key=lambda x: (x[1][0][0] + x[1][3][0]) / 2)
        sorted_line_list.append(sorted_row.copy())
        
    for idx, row in enumerate(sorted_line_list):
        content_list = [item[0] for item in row]
        count = sum(item in content_list for item in label_header)
        if count == 0:
            continue
        for row_ in sorted_line_list[idx:idx+3]:
            for item in row_:
                if item[0] in label_header:
                    bound[item[0]] = item[1]
        if len(bound) >= 2:
            break
        
    for row in sorted_line_list:
        content_list = [item[0] for item in row]
        content_text = "".join(content_list)
        count = 0
        for item in label_header:
            if item in content_text:
                count = count + 1
        if count >= 2 and top_check == 1:
            perchase_list.clear()
            top_check = 0
            continue
        count = 0
        for item in label_end_keyword:
            if item in content_text:
                count = count + 1
        if count > 0 and top_check == 0:
            break
        perchase_list.append(row)    
        
    return (perchase_list, bound)

def extract_purchase_list(purchase_list, bound):
    pattern = r'(^((-)?([1-9]([0-9]{0,2})?(,\d{3})*|0)(\.\d+)?)$)'
    start = -1
    end = -1
    right = 0
    space = 0
    pname = ""
    temp_list = [[], '-1', '-1', '-1']
    result_list = []
    for idx, row in enumerate(purchase_list):
        if len(row) == 1:
            continue
        if start == -1:
            start = idx
            pname = []
            pname_t = []
        for idx2, item in enumerate(row):
            isName = 1
            if idx2 >= 1:
                cur_left = (item[1][0][0] + item[1][3][0]) / 2
                if cur_left - right > space:
                    pname_t.append(" ")
            center = (item[1][0][0] + item[1][1][0] + item[1][2][0] + item[1][3][0]) / 4
            for category in label_header_value:
                temp = bound[category]
                bound_left = (temp[0][0] + temp[3][0]) / 2
                bound_right = (temp[1][0] + temp[2][0]) / 2
                bound_margin = (bound_right - bound_left) * 0.2
                bound_left -= bound_margin
                bound_right += bound_margin
                if bound_left <= center <= bound_right:
                    if re.match(pattern, item[0]):
                        if category in label_header_unit_price:
                            temp_list[1] = item[0]
                        elif category in label_header_quantity:
                            temp_list[2] = item[0]
                        elif category in label_header_price:
                            temp_list[3] = item[0]
                        isName = 0
                        end = idx
            if isName == 1:
                pname_t.append(item[0])
            right = (item[1][1][0] + item[1][2][0]) / 2
            left = (item[1][0][0] + item[1][3][0]) / 2
            space = ((right - left) / len(item[0])) / 2
            if re.search(r'^[!@#$%^&*()]+$', item[0]):
                space = (right - left) / len(item[0])
        if end == -1:
            pname = pname + pname_t.copy()
        else:
            if end == start:
                pname = pname + pname_t.copy()
            end = -1
            start = -1
            temp_list[0] = pname.copy()
            #print(temp_list)
            result_list.append(temp_list)
            temp_list = [[], '-1', '-1', '-1']
    count = 0
    pattern = r'\d+'
    for row in result_list:
        match = re.search(pattern, row[0][0])
        if match:
            count += 1
        
    # 숫자 제거
    if count > (len(result_list) * 0.5):
        for row in result_list:
            while(True):
                match = re.search(pattern, row[0][0])
                del row[0][0]
                if match:
                    break

    # * 또는 공백 제거
    for row in result_list:
        if row[0][0] == " " or row[0][0] == "*":
            del row[0][0]
            if row[0][0] == " " or row[0][0] == "*":
                del row[0][0]

    for idx, row in enumerate(result_list):
        temp_list = row.copy()
        pname = "".join(row[0])
        temp_list[0] = pname
        temp_list[1] = int(re.sub(r"[^0-9+-]", "", temp_list[1]))
        temp_list[2] = int(re.sub(r"[^0-9+-]", "", temp_list[2]))
        temp_list[3] = int(re.sub(r"[^0-9+-]", "", temp_list[3]))
        result_list[idx] = temp_list.copy()
        
    for idx, row in enumerate(result_list):
        if row[3] < 0:
            result_list[idx-1][3] += row[3]
            del(result_list[idx])
            
            
        
        
    #result = [{"ProductName":item[0], "UnitPrice":str(item[1]), "Quantity":str(item[2]), "Price":str(item[3])} for item in result_list] 
    
    #json_array = json.dumps(result)
    
    return result_list
    
def main(file_name):
    texts = text_detect(file_name)
    degree = calc_slope(texts)
    rotate_image(file_name, degree)
    texts = text_detect(temp_file_name)
    purchase_list, bound = extract_purchase_line(texts)
    result = extract_purchase_list(purchase_list, bound)
    
    return result
    
if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("Insufficient arguments")
        sys.exit()
    file_path = sys.argv[1]
    main(file_path)