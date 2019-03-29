# -*- coding: utf-8 -*-
"""
@author: tanma
"""

import spacy
from spacy import displacy
from collections import Counter
import en_core_web_sm
nlp = en_core_web_sm.load()

doc = nlp('Amogh Lele integrates USA arsenal into Amazon web archives, Russia protests vehemently due to Kremlin concerns.')
print([(X.text, X.label_) for X in doc.ents])


