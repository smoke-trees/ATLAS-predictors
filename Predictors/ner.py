# -*- coding: utf-8 -*-
"""
Created on Sat Mar 23 02:37:21 2019

@author: tanma
"""

import spacy
from spacy import displacy
from collections import Counter
import en_core_web_sm
nlp = en_core_web_sm.load()

doc = nlp('India to finally induct desi Bofors next week to upgrade long-range, high-volume firepower')
print([(X.text, X.label_) for X in doc.ents])


