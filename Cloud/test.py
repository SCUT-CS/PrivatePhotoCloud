import array

import tensorflow as tf
import matplotlib.pyplot as plt
import numpy as np
from keras.layers import Flatten
from tensorflow.python import tf_export
input_data1 = np.arange(20).reshape(2,2,5)
print(input_data1)

x2 = array([[[ 0,  1,  2,  3,  4],
        [ 5,  6,  7,  8,  9]],

       [[10, 11, 12, 13, 14],
        [15, 16, 17, 18, 19]]])

x2_f = tf.keras.layers.Flatten()(x2)
print(x2_f)
'''
<tf.Tensor: shape=(2, 10), dtype=int32, numpy=
array([[ 0,  1,  2,  3,  4,  5,  6,  7,  8,  9],
       [10, 11, 12, 13, 14, 15, 16, 17, 18, 19]])>
'''
