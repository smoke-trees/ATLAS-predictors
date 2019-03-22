import pandas as pd
from flask import render_template
from flask_cors import CORS
import flask
import numpy as np

TEST_DATA_FILE = './sample_submission_1.csv'
sample = pd.read_csv(TEST_DATA_FILE)
list_classes = ["toxic", "severe_toxic", "obscene", "threat", "insult", "identity_hate"]
y = sample[list_classes].values
sentences = sample["comments"].fillna("DUMMY_VALUE").values

import re


def clean_text(text):
    text = text.lower()

    text = re.sub(r"i'm", "i am", text)
    text = re.sub(r"he's", "he is", text)
    text = re.sub(r"she's", "she is", text)
    text = re.sub(r"it's", "it is", text)
    text = re.sub(r"that's", "that is", text)
    text = re.sub(r"what's", "that is", text)
    text = re.sub(r"where's", "where is", text)
    text = re.sub(r"how's", "how is", text)
    text = re.sub(r"\'ll", " will", text)
    text = re.sub(r"\'ve", " have", text)
    text = re.sub(r"\'re", " are", text)
    text = re.sub(r"\'d", " would", text)
    text = re.sub(r"\'re", " are", text)
    text = re.sub(r"won't", "will not", text)
    text = re.sub(r"can't", "cannot", text)
    text = re.sub(r"n't", " not", text)
    text = re.sub(r"n'", "ng", text)
    text = re.sub(r"'bout", "about", text)
    text = re.sub(r"'til", "until", text)
    text = re.sub(r"[-()\"#/@;:<>{}`+=~|.!?,]", "", text)

    return text


app = flask.Flask(__name__)

CORS(app)


@app.route("/")
def predict():
    send = ['False', 'False', 'False', 'False', 'False', 'False']
    i = np.random.choice(len(sample))
    for j, prob in enumerate(list(y[i:i + 1].flatten())):
        if prob > 0.5:
            send[j] = 'True'
    return render_template('form.html', send=send, comments=(sentences[i:i + 1]))


if __name__ == '__main__':
    print('Running on port {}'.format(3000))
    app.run(host='0.0.0.0', port=3000)
