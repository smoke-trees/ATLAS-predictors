# -*- coding: utf-8 -*-
"""
@author: tanma
"""
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras.models import Model
from tensorflow.keras.layers import Input, LSTM, Dense, Embedding, CuDNNLSTM, Flatten, TimeDistributed, Dropout, LSTMCell, RNN
from tensorflow.keras.callbacks import ModelCheckpoint
from tensorflow.python.keras.utils import tf_utils
from tensorflow.keras import backend as K

import unicodedata
import re
import numpy as np
import os
import time
import shutil

path_to_file = 'C:\\Users\\tanma.TANMAY-STATION\\Downloads/deu.txt'

class LanguageIndex():
    def __init__(self, lang):
        self.lang = lang
        self.word2idx = {}
        self.idx2word = {}
        self.vocab = set()
        self.create_index()
    def create_index(self):
        for phrase in self.lang:
            self.vocab.update(phrase.split(' '))
        self.vocab = sorted(self.vocab)
        self.word2idx["<pad>"] = 0
        self.idx2word[0] = "<pad>"
        for i,word in enumerate(self.vocab):
            self.word2idx[word] = i + 1
            self.idx2word[i+1] = word
            
def unicode_to_ascii(s):
    return ''.join(c for c in unicodedata.normalize('NFD', s) if unicodedata.category(c) != 'Mn')

def preprocess_sentence(w):
    w = unicode_to_ascii(w.lower().strip())
    w = re.sub(r"([?.!,¿])", r" \1 ", w)
    w = re.sub(r'[" "]+', " ", w)
    w = re.sub(r"[^a-zA-Z?.!,¿]+", " ", w)
    w = w.rstrip().strip()
    w = "<start> " + w + " <end>"
    return w

def max_length(t):
    return max(len(i) for i in t)

def create_dataset(path, num_examples):
    lines = open(path, encoding="UTF-8").read().strip().split("\n")
    word_pairs = [[preprocess_sentence(w) for w in l.split("\t")] for l in lines[:num_examples]]
    return word_pairs

def load_dataset(path, num_examples):
    pairs = create_dataset(path, num_examples)
    out_lang = LanguageIndex(hin for en, hin in pairs)
    in_lang = LanguageIndex(en for en, hin in pairs)
    input_data = [[in_lang.word2idx[s] for s in en.split(' ')] for en, sp in pairs]
    output_data = [[out_lang.word2idx[s] for s in sp.split(' ')] for en, sp in pairs]

    max_length_in, max_length_out = max_length(input_data), max_length(output_data)
    input_data = tf.keras.preprocessing.sequence.pad_sequences(input_data, maxlen=max_length_in, padding="post")
    output_data = tf.keras.preprocessing.sequence.pad_sequences(output_data, maxlen=max_length_out, padding="post")
    return input_data, output_data, in_lang, out_lang, max_length_in, max_length_out

num_examples = 20000

input_data, teacher_data, input_lang, target_lang, len_input, len_target = load_dataset(path_to_file, num_examples)

target_data = [[teacher_data[n][i+1] for i in range(len(teacher_data[n])-1)] for n in range(len(teacher_data))]
target_data = tf.keras.preprocessing.sequence.pad_sequences(target_data, maxlen=len_target, padding="post")
target_data = target_data.reshape((target_data.shape[0], target_data.shape[1], 1))

p = np.random.permutation(len(input_data))
input_data = input_data[p]
teacher_data = teacher_data[p]
target_data = target_data[p]

BUFFER_SIZE = len(input_data)
BATCH_SIZE = 64
embedding_dim = 256
units = 1024
vocab_in_size = len(input_lang.word2idx)
vocab_out_size = len(target_lang.word2idx)

class AttentionLSTMCell(LSTMCell):
    def __init__(self, **kwargs):
        self.attentionMode = False
        super(AttentionLSTMCell, self).__init__(**kwargs)

    @tf_utils.shape_type_conversion
    def build(self, input_shape):        

        self.dense_constant = TimeDistributed(Dense(self.units, name="AttLstmInternal_DenseConstant"))
        
        self.dense_state = Dense(self.units, name="AttLstmInternal_DenseState")
        
        self.dense_transform = Dense(1, name="AttLstmInternal_DenseTransform")

        batch, input_dim = input_shape[0]
        batch, timesteps, context_size = input_shape[-1]
        lstm_input = (batch, input_dim + context_size)
        
        return super(AttentionLSTMCell, self).build(lstm_input)
    
    def setInputSequence(self, input_seq):
        self.input_seq = input_seq
        self.input_seq_shaped = self.dense_constant(input_seq)
        self.timesteps = tf.shape(self.input_seq)[-2]

    def setAttentionMode(self, mode_on=False):
        self.attentionMode = mode_on
    
    def call(self, inputs, states, constants):
        ytm, stm = states

        stm_repeated = K.repeat(self.dense_state(stm), self.timesteps)

        combined_stm_input = self.dense_transform(
            keras.activations.relu(stm_repeated + self.input_seq_shaped))
        score_vector = keras.activations.softmax(combined_stm_input, 1)
        
        context_vector = K.sum(score_vector * self.input_seq, 1)
    
        inputs = K.concatenate([inputs, context_vector])
        
        res = super(AttentionLSTMCell, self).call(inputs=inputs, states=states)
        
        if(self.attentionMode):
            return (K.reshape(score_vector, (-1, self.timesteps)), res[1])
        else:
            return res

class LSTMWithAttention(RNN):
    def __init__(self, units, **kwargs):
        cell = AttentionLSTMCell(units=units)
        self.units = units
        super(LSTMWithAttention, self).__init__(cell, **kwargs)
        
    @tf_utils.shape_type_conversion
    def build(self, input_shape):
        self.input_dim = input_shape[0][-1]
        self.timesteps = input_shape[0][-2]
        return super(LSTMWithAttention, self).build(input_shape) 

    def call(self, x, constants, **kwargs):
        if isinstance(x, list):
            self.x_initial = x[0]
        else:
            self.x_initial = x

        self.cell._dropout_mask = None
        self.cell._recurrent_dropout_mask = None
        self.cell.setInputSequence(constants[0])
        return super(LSTMWithAttention, self).call(inputs=x, constants=constants, **kwargs)


attenc_inputs = Input(shape=(len_input,), name="attenc_inputs")
attenc_emb = Embedding(input_dim=vocab_in_size, output_dim=embedding_dim)
attenc_lstm = CuDNNLSTM(units=units, return_sequences=True, return_state=True)
attenc_outputs, attstate_h, attstate_c = attenc_lstm(attenc_emb(attenc_inputs))
attenc_states = [attstate_h, attstate_c]

attdec_inputs = Input(shape=(None,))
attdec_emb = Embedding(input_dim=vocab_out_size, output_dim=embedding_dim)
attdec_lstm = LSTMWithAttention(units=units, return_sequences=True, return_state=True)

attdec_lstm_out, _, _ = attdec_lstm(inputs=attdec_emb(attdec_inputs), 
                                    constants=attenc_outputs, 
                                    initial_state=attenc_states)
attdec_d1 = Dense(units, activation="relu")
attdec_d2 = Dense(vocab_out_size, activation="softmax")
attdec_out = attdec_d2(Dropout(rate=.4)(attdec_d1(Dropout(rate=.4)(attdec_lstm_out))))

attmodel = Model([attenc_inputs, attdec_inputs], attdec_out)
attmodel.compile(optimizer=tf.train.AdamOptimizer(), loss="sparse_categorical_crossentropy", metrics=['sparse_categorical_accuracy'])

epochs = 20
atthist = attmodel.fit([input_data, teacher_data], target_data,
                 batch_size=64,
                 epochs=epochs,
                 validation_split=0.2)


def sentence_to_vector(sentence, lang):
    pre = preprocess_sentence(sentence)
    vec = np.zeros(len_input)
    sentence_list = [lang.word2idx[s] for s in pre.split(' ')]
    for i,w in enumerate(sentence_list):
        vec[i] = w
    return vec

attmodel.save('attn.h5')
model = tf.keras.models.load_model('attn.h5')

def translate(input_sentence, infenc_model, infmodel, attention=False):
    sv = sentence_to_vector(input_sentence, input_lang)
    sv = sv.reshape(1,len(sv))
    [emb_out, sh, sc] = infenc_model.predict(x=sv)
    
    i = 0
    start_vec = target_lang.word2idx["<start>"]
    stop_vec = target_lang.word2idx["<end>"]

    cur_vec = np.zeros((1,1))
    cur_vec[0,0] = start_vec
    cur_word = "<start>"
    output_sentence = ""

    while cur_word != "<end>" and i < (len_target-1):
        i += 1
        if cur_word != "<start>":
            output_sentence = output_sentence + " " + cur_word
        x_in = [cur_vec, sh, sc]
        if attention:
            x_in += [emb_out]
        [nvec, sh, sc] = infmodel.predict(x=x_in)
        cur_vec[0,0] = np.argmax(nvec[0,0])
        cur_word = target_lang.idx2word[np.argmax(nvec[0,0])]
    return output_sentence

def createAttentionInference(attention_mode=False):
    attencoder_model = Model(attenc_inputs, [attenc_outputs, attstate_h, attstate_c])
    state_input_h = Input(shape=(units,), name="state_input_h")
    state_input_c = Input(shape=(units,), name="state_input_c")
    attenc_seq_out = Input(shape=attenc_outputs.get_shape()[1:], name="attenc_seq_out")
    inf_attdec_inputs = Input(shape=(None,), name="inf_attdec_inputs")
    attdec_lstm.cell.setAttentionMode(attention_mode)
    attdec_res, attdec_h, attdec_c = attdec_lstm(attdec_emb(inf_attdec_inputs), 
                                                 initial_state=[state_input_h, state_input_c], 
                                                 constants=attenc_seq_out)
    attinf_model = None
    if not attention_mode:
        inf_attdec_out = attdec_d2(attdec_d1(attdec_res))
        attinf_model = Model(inputs=[inf_attdec_inputs, state_input_h, state_input_c, attenc_seq_out], 
                             outputs=[inf_attdec_out, attdec_h, attdec_c])
    else:
        attinf_model = Model(inputs=[inf_attdec_inputs, state_input_h, state_input_c, attenc_seq_out], 
                             outputs=[attdec_res, attdec_h, attdec_c])
    return attencoder_model, attinf_model

attencoder_model, attinf_model = createAttentionInference()

def decode(input_seq):
    return translate(input_seq, attencoder_model, attinf_model, True)