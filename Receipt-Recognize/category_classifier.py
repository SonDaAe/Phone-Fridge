# -*- coding: utf-8 -*-

import pandas as pd
import numpy as np
import pickle
import os
import re
from tensorflow.keras.utils import to_categorical
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder
from tensorflow.keras.preprocessing.text import *
from tensorflow.keras.preprocessing.sequence import pad_sequences
from tensorflow.keras.models import *
from tensorflow.keras.layers import *
from konlpy.tag import Okt
from konlpy.tag import Mecab
from keras.callbacks import EarlyStopping
import datetime

root_path = "./20230521/"
data_path = "train_set"

class classification:
    def __init__(self, model_path=None):
        self.model_path = root_path + model_path
        self.df = None

        self.tagger = Mecab("./mecab-ko-dic/mecab-ko-dic")
        
        self.stopwords = pd.read_csv(root_path + 'stopwords.csv', encoding="utf-8", index_col = 0)
        self.stopwords = self.stopwords['stopword'].tolist()
        
        self.category_data = pd.read_csv(root_path + 'category.csv', encoding="utf-8", index_col = 0)
        
        self.prepare_encoder()
        self.prepare_tokenizer()
        self.prepare_dataset()
        
        self.model = None
        
    def prepare_data(self, file_path):
        if os.path.isfile(root_path + "prepossed.csv"):
            df = pd.read_csv(root_path + "prepossed.csv", encoding="utf-8", index_col = 0)
        else:
            df = pd.read_csv(root_path + file_path, encoding="utf-8", index_col = 0)
            
            # 중복 제거
            df = df.drop_duplicates(subset = ['name'])
            df = df.dropna(subset=['name'])
            df.reset_index(drop = True, inplace = True)
           
            stopwords = pd.read_csv(root_path + 'stopwords.csv', encoding="utf-8", index_col = 0)
            stopwords = stopwords['stopword'].tolist()
            
            mecab = Mecab("./mecab-ko-dic/mecab-ko-dic")
            for i in range(len(df)):
                print(f"{i}/{len(df)}")
                word = []
                rc = re.sub(r'\[[^)]*\]', '', df['name'][i])
                rc = re.sub(r'\([^)]*\)', '', rc)
                rc = re.sub(r'\★[^)]*\★', '', rc)
                words = mecab.nouns(rc)
                for w in words:
                    if w not in stopwords:
                        if w not in word:
                            word.append(w)
                if len(word) < 2:
                    word = []
                df['name'][i] = " ".join(word)
            
            df = df.replace("", pd.NA)
            df = df.dropna(subset=['name'])
            df.reset_index(drop = True, inplace = True)

            new_df = df[['name', 'cat']]
            new_df.to_csv(root_path + "prepossed.csv", encoding='utf-8', mode='w')
        
        return df
        
    def prepare_encoder(self):
        encoder_filename = root_path + "category_encoder.pickle"
        if os.path.isfile(encoder_filename):
            print("Encoder Exist")
            with open(encoder_filename, 'rb') as file:
                self.encoder = pickle.load(file)
        else:
            if self.df == None:
                self.df = self.prepare_data(root_path + data_path)
            print("Encoder not Exist")
            Y = df['cat']
            self.encoder = LabelEncoder()
            labeled_Y = encoder.fit_transform(Y)
            print(labeled_Y)
            with open(encoder_filename, 'wb') as f:
                pickle.dump(encoder, f)
    
    def prepare_tokenizer(self):
        tokenizer_filename = root_path + "tokenizer.pickle"
        if os.path.isfile(tokenizer_filename):
            print("Tokenizer Exist")
            with open(tokenizer_filename, 'rb') as file:
                self.tokenizer = pickle.load(file)
        else:
            if self.df == None:
                self.df = self.prepare_data(root_path + data_path)
            print("Tokenizer Not Exist")
            X = self.df['name']
            self.tokenizer = Tokenizer()
            self.tokenizer.fit_on_texts(X)
            with open(tokenizer_filename, 'wb') as f:
                pickle.dump(self.tokenizer, f)
                
    def prepare_dataset(self):
        dataset_filename = root_path + "train_set.npy"
        if os.path.isfile(dataset_filename):
            print("dataset Exist")
            wordsize, maxsize, label_count, X_train, X_test, Y_train, Y_test = np.load(dataset_filename, allow_pickle=True)
        else:
            if self.df == None:
                self.df = self.prepare_data(root_path + data_path)
            print("dataset Not Exist")
            wordsize = len(self.tokenizer.word_index) + 1
            tokened_X = self.tokenizer.texts_to_sequences(df['name'])
            onehot_Y = to_categorical(self.encoder.fit_transform(df['cat']))
        
            maxsize = 0
            for i in range(len(tokened_X)):
                if maxsize < len(tokened_X[i]):
                    maxsize = len(tokened_X[i])
            
            
            
            X_pad = pad_sequences(tokened_X, maxsize)
            label_count = len(self.encoder.classes_)
            

            X_train, X_test, Y_train, Y_test = train_test_split(X_pad, onehot_Y, test_size = 0.1)
            xy = wordsize, maxsize, label_count, X_train, X_test, Y_train, Y_test
            np.save(dataset_filename, xy)
        
        self.label_count = label_count
        self.wordsize = wordsize
        self.maxsize = maxsize
        self.X_train = X_train
        self.X_test = X_test
        self.Y_train = Y_train
        self.Y_test = Y_test
    
    def train(self):
        if self.model == None:
            self.model = Sequential()
            self.model.add(Embedding(wordsize, 300, input_length=maxsize))
            self.model.add(Conv1D(512, kernel_size=5, padding='same', activation='relu'))
            self.model.add(MaxPool1D(pool_size=1))
            self.model.add(LSTM(128, activation='tanh', return_sequences=True))
            self.model.add(Dropout(0.2))
            self.model.add(LSTM(64, activation='tanh', return_sequences=True))
            self.model.add(Dropout(0.1))
            self.model.add(Flatten())
            self.model.add(Dense(128, activation = 'relu'))
            self.model.add(Dense(label_count, activation = 'softmax'))
            
        early_stopping = EarlyStopping(monitor='val_accuracy', patience = 5)
            
        self.model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])
        
        fit_hist = self.model.fit(self.X_train, self.Y_train, batch_size=32, epochs = 100, validation_data=(self.X_test, self.Y_test), callbacks=[early_stopping], shuffle=True)
        
        score = self.model.evaluate(self.X_test, self.Y_test)
        
        now = datetime.datetime.now()
        nowtime = now.strftime("%Y%m%d%H%M%S")
        self.model.save(f'model{nowtime}.h5')
        
    def predict(self, pname):
        if self.model_path == None:
            print("예측을 위해선 모델이 필요합니다. ")
            return
        else:
            if self.model == None:
                self.model = load_model(self.model_path)
        
        word = []
        
        rc = re.sub(r'\[[^)]*\]', '', pname)
        rc = re.sub(r'\([^)]*\)', '', rc)
        rc = re.sub(r'\★[^)]*\★', '', rc)
        
        words = self.tagger.nouns(rc)
        for w in words:
            if w not in self.stopwords:
                word.append(w)
        
        name = " ".join(word)
        
        
        tokened = self.tokenizer.texts_to_sequences([name])
        X_pad = pad_sequences(tokened, self.maxsize)
        
        result = np.argmax(self.model.predict(X_pad)[0])
        
        return self.encoder.classes_[result]
    
    def predict_exp(self, category):
        filtered = self.category_data[self.category_data['cat'] == category]
        
        return filtered['exp'].unique()[0]
        
    
# classifi = classification("model20230520235612_32.h5")
# classifi.predict_exp("")
# while True:
#     in_ = input("입력: ")
#     print(classifi.predict_exp(in_))