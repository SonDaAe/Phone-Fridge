# -*- coding: utf-8 -*-

import os, io, sys
import re
import cv2
import math, numpy as np

os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = 'service_secret_key.json'

    

from google.cloud import vision
client = vision.ImageAnnotatorClient()

pattern = '[ㄱ-ㅎㅏ-ㅣ가-힣]{2,}'

fname = 't19.jpg'
file_name = os.path.abspath(fname)
img = cv2.imread(fname)

height, width, _ = img.shape

def find_left(item):
    return (item[1][0][0] + item[1][3][0]) / 2

def find_right(item):
    return (item[1][1][0] + item[1][2][0]) / 2

with io.open(file_name, 'rb') as image_file:
    content = image_file.read()
    
image = vision.Image(content=content)

response = client.text_detection(image=image)
texts = response.text_annotations

print('Texts:')

slopes = [];

for text in texts[1:]:
#    print('\n"{}"'.format(text.description))
    contents = text.description

    vertices = (['({},{})'.format(vertex.x, vertex.y)
                for vertex in text.bounding_poly.vertices])
    
    verts = [[vertex.x, vertex.y]
                for vertex in text.bounding_poly.vertices]
#    img = cv2.line(img, verts[0], verts[1], (0, 0, 255), 1)
    
    if re.search(pattern, contents) is not None:
        slope = (verts[1][1]-verts[0][1])/(verts[1][0]-verts[0][0])
        slopes.append(slope)
        
#cv2.imwrite("0418p_01.jpg", img)
#     if (len(contents) >= 2 and all(ord(c) > 127 or c.isdigit() or c.isspace() for c in contents)):
#         
#         
#     #print(verts)

average = sum(slopes) / len(slopes)
degree = np.arctan(average)
degree = math.degrees(degree)

center = (img.shape[1] // 2, img.shape[0] // 2)
M = cv2.getRotationMatrix2D(center, degree, 1.0)
rotated = cv2.warpAffine(img, M, (img.shape[1], img.shape[0]))

    
sname = "temp_r.jpg"
cv2.imwrite(sname, rotated)
img = rotated

file_name = os.path.abspath(sname)

with io.open(file_name, 'rb') as image_file:
    content = image_file.read()
    
image = vision.Image(content=content)
response = client.text_detection(image=image)
texts = response.text_annotations


label_top_name = ["제품명", "상품명"]
label_unit_price = ["단가"]
label_quantity = ["수량"]
label_price = ["금액"]
label_top_value = label_unit_price + label_quantity + label_price
label_top = label_top_name + label_top_value
label_bottom = ["합계", "부가세", "과세", "부가", "가세", "판매액", "품목", "물품", "결제"]
bound = dict()
left_bound = width
right_bound = 0
bottom_bound = 0
ext_list = []

#print(texts[0].description)

for text in texts[1:]:
#    print('\n"{}"'.format(text.description))
    contents = text.description
    vertex = [[vertex.x, vertex.y] for vertex in text.bounding_poly.vertices]
    ext_list.append([contents, vertex])
    if contents in label_top:
        bound[contents] = vertex
    if re.search(pattern, contents) is not None:
        if ((vertex[0][0] + vertex[3][0]) / 2) < left_bound:
            left_bound = (vertex[0][0] + vertex[3][0]) / 2
        if ((vertex[1][0] + vertex[2][0]) / 2) > right_bound:
            right_bound = (vertex[1][0] + vertex[2][0]) / 2
        if ((vertex[2][1] + vertex[3][1]) / 2) > bottom_bound:
            bottom_bound = (vertex[2][1] + vertex[3][1]) / 2
            
    
sorted_list = sorted(ext_list, key=lambda x: (x[1][0][1] + x[1][1][1] + x[1][2][1] + x[1][3][1]) / 2)


# 상품명, 단가를 기준으로 위쪽 경계선 설정
top_bound = 0
for key, value in bound.items():
#    print(value)
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
    
line_list = []
temp_list = []
line_pos = -1
for item in sorted_list:
    top = (item[1][0][1] + item[1][1][1]) / 2
    bottom = (item[1][2][1] + item[1][3][1]) / 2
    left = (item[1][0][0] + item[1][3][0]) / 2
    right = (item[1][1][0] + item[1][2][0]) / 2
    
    if line_pos == -1:
        temp_list.append(item)
        line_pos = (top + bottom) / 2
    else:
        if (top <= line_pos <= bottom) and (left >= left_bound) and (right <= right_bound):
            temp_list.append(item)
        else:
            line_list.append(temp_list.copy())
            temp_list.clear()
            temp_list.append(item)
            line_pos = (top + bottom) / 2
            
sorted_line_list = []
#print(sorted_list)


for row in line_list:
    sorted_row = sorted(row, key=lambda x: (x[1][0][0] + x[1][3][0]) / 2)
    sorted_line_list.append(sorted_row.copy())

# for row in sorted_line_list:
#     space = 10000000
#     temp_list = []
#     temp_item = ["", [[-1,-1],[-1,-1],[-1,-1],[-1,-1]]]
#     last_word = ""
#     for idx, item in enumerate(row):
#         last_word = item[0]
#         # if idx != 0:
#         #     if (find_left(row[idx]) - find_right(row[idx-1])) > space:
#         #         if temp_item[0] == "":
#         #             temp_list.append(row[idx-1].copy())
#         #         else:
#         #             temp_item[0] += row[idx-1][0]
#         #             temp_list.append(temp_item.copy())
#         #             temp_item = ["", [[-1,-1],[-1,-1],[-1,-1],[-1,-1]]]
#         #     else:
#         #         temp_item[0] += row[idx-1][0]
#         #         if find_left(temp_item) < 0:
#         #             temp_item[1][0] = item[1][0].copy()
#         #             temp_item[1][3] = item[1][3].copy()
#         #         temp_item[1][1] = item[1][1].copy()
#         #         temp_item[1][2] = item[1][2].copy()
                
#         # space = ((find_right(item) - find_left(item)) / len(item[0])) / 2 
#         if idx != 0:
#             if (find_left(row[idx]) - find_right(row[idx-1])) > space:
#                 temp_list.append(temp_item)
#                 temp_item = ["", [[-1,-1],[-1,-1],[-1,-1],[-1,-1]]]
        
#         temp_item[0] += item[0]
#         if find_left(temp_item) < 0:
#             temp_item[1][0] = item[1][0].copy()
#             temp_item[1][3] = item[1][3].copy()
#         temp_item[1][1] = item[1][1].copy()
#         temp_item[1][2] = item[1][2].copy()
#         space = ((find_right(temp_item) - find_left(temp_item)) / len(temp_item[0])) * 0.5
        
#     temp_list.append(temp_item.copy())
    
#     print(temp_list)
        

print([[item[0] for item in row] for row in sorted_line_list])

lb_ = 0

perchase_list = []
top_check = 1

bound = dict()

label_count = 0
continuity = 2
for idx, row in enumerate(sorted_line_list):
    content_list = [item[0] for item in row]
    label_count = 0
    count = sum(item in content_list for item in label_top)
    if count == 0:
        continue
    for row_ in sorted_line_list[idx:idx+3]:
        for item in row_:
            if item[0] in label_top:
                bound[item[0]] = item[1]
    if len(bound) >= 2:
        break
    
    

# for row in sorted_line_list:
#     content_list = [item[0] for item in row]
#     if sum(item in content_list for item in label_top) >= 2 and top_check == 1:
#         perchase_list.clear()
#         top_check = 0
#     elif any(item in content_list for item in label_bottom) and top_check == 0:
#         break
#     else:
#         perchase_list.append(row)
        
for row in sorted_line_list:
    content_list = [item[0] for item in row]
    content_text = "".join(content_list)
    count = 0
    for item in label_top:
        if item in content_text:
            count = count + 1
    if count >= 2 and top_check == 1:
        perchase_list.clear()
        top_check = 0
        continue
    count = 0
    for item in label_bottom:
        if item in content_text:
            count = count + 1
    if count > 0 and top_check == 0:
        break
    perchase_list.append(row)
    
#print(perchase_list)

pattern = r'(^((-)?([1-9]([0-9]{0,2})?(,\d{3})*|0)(\.\d+)?)$)'
start = -1
end = -1
space = 0
pname = ""
temp_list = ["", -1, -1, -1]
result_list = []
for idx, row in enumerate(perchase_list):
    if start == -1:
        start = idx
        pname = ""
        pname_t = ""
    for idx2, item in enumerate(row):
        isName = 1
        if idx >= 1:
            cur_left = (item[1][0][0] + item[1][3][0]) / 2
            if cur_left - right > space:
                pname_t = pname_t + " "
        center = (item[1][0][0] + item[1][1][0] + item[1][2][0] + item[1][3][0]) / 4

        for category in label_top_value:
            #rint(item[0])
            #print("Center: ", center)
            #print(category)
            temp = bound[category]
            
            bound_left = (temp[0][0] + temp[3][0]) / 2
            bound_right = (temp[1][0] + temp[2][0]) / 2
            bound_margin = (bound_right - bound_left) * 0.2
            bound_left -= bound_margin
            bound_right += bound_margin
            #print(temp, "\n", category, ":", bound_left, bound_right)
            if bound_left <= center <= bound_right:
                if re.match(pattern, item[0]):
                    #print(item[0])
                    if category in label_unit_price:
                        temp_list[1] = item[0]
                    elif category in label_quantity:
                        temp_list[2] = item[0]
                    elif category in label_price:
                        temp_list[3] = item[0]
                    isName = 0
                    end = idx
        if isName == 1:
            pname_t = pname_t + item[0]     
            
        right = (item[1][1][0] + item[1][2][0]) / 2
        left = (item[1][0][0] + item[1][3][0]) / 2
        space = ((right - left) / len(item[0])) / 3
    if end == -1:
        pname = pname + pname_t
    else:
        if end == start:
            pname = pname + pname_t
        end = -1
        start = -1
        temp_list[0] = pname
        print(temp_list)
        result_list.append(temp_list)
        temp_list = ["", -1, -1, -1]
    
pattern = r'[^a-zA-Z가-힣]+(\w+)'


for item in result_list:
    match = re.search(pattern, item[0])
    if match:
        print(match.group(1))
        



            
#줄 단위로 읽어가며 누적 => 단가 수량 금액 항목 만나면 초기화 후 데이터베이스 등록
#for row in sorted_line_list:
    
    
    
# 이미지 자르기
#img = img[0:height, left_bound:right_bound]

#cv2.rectangle(img, (left_bound, top_bound), (right_bound, bottom_bound), (0, 255, 0), 2)


#cv2.imshow("Res", img)
#cv2.waitKey()
#cv2.destroyAllWindows()