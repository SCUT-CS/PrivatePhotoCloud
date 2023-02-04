import tensorflow as tf
import matplotlib.pyplot as plt
import numpy as np
import multiprocessing

import time

from Dense import Dense, relu, OutputLayer, soft_max
from ss.encoded_float import Float


def worker(x, y, layer1, layer2, queue):
    l1 = layer1.forward(x)
    l2 = layer2.forward(l1)
    if np.argmax(l2) == y:
        queue.put(1)


def flatten(array):
    temp1 = np.empty([array.shape[0], array.shape[1] * array.shape[2]])
    for i in range(array.shape[0]):
        temp2 = array[i].flatten()
        temp1[i] = temp2
    return temp1


if __name__ == '__main__':
    # 初始化
    plt.rcParams['font.sans-serif'] = ['SimHei']

    # 加载数据
    mnist = tf.keras.datasets.mnist
    (train_x, train_y), (test_x, test_y) = mnist.load_data()
    print(
        '\n train_x:%s, train_y:%s, test_x:%s, test_y:%s' % (train_x.shape, train_y.shape, test_x.shape, test_y.shape))

    # 数据预处理
    train_x = flatten(train_x) / 255.0
    test_x = flatten(test_x) / 255.0

    X_train, X_test = tf.cast(train_x, tf.float32), tf.cast(test_x, tf.float32)
    y_train, y_test = tf.cast(train_y, tf.int16), tf.cast(test_y, tf.int16)

    # 建立模型
    model = tf.keras.Sequential()
    model.add(tf.keras.layers.InputLayer([28 * 28]))
    model.add(tf.keras.layers.Dense(128, activation='relu'))  # 添加隐含层，为全连接层，128个节点，relu激活函数
    model.add(tf.keras.layers.Dense(10, activation='softmax'))  # 添加输出层，为全连接层，10个节点，softmax激活函数
    print('\n', model.summary())  # 查看网络结构和参数信息

    # #配置模型训练方法
    # #adam算法参数采用keras默认的公开参数，损失函数采用稀疏交叉熵损失函数，准确率采用稀疏分类准确率函数
    model.compile(optimizer='adam', loss='sparse_categorical_crossentropy', metrics=['sparse_categorical_accuracy'])
    # #训练模型
    # #批量训练大小为64，迭代5次，测试集比例0.2（48000条训练集数据，12000条测试集数据）
    # history = model.fit(X_train,y_train,batch_size=64,epochs=10,validation_split=0.2)
    # model.save('mnist_weights.h5')

    # 保存整个模型
    model.load_weights('mnist_weights.h5')
    t = model.get_weights()

    # layer1 = Dense(128, t[0], t[1], relu)
    # layer2 = OutputLayer(10, t[2], t[3], soft_max)
    # correct = 0
    # for i in range(test_x.shape[0]):
    #     l1 = layer1.forward(test_x[i])
    #     l2 = layer2.forward(l1)
    #     if np.argmax(l2) == test_y[i]:
    #         correct += 1
    # print('准确率：', correct / test_x.shape[0])

    a1 = np.empty(t[0].shape, dtype=Float)
    a2 = np.empty(t[1].shape, dtype=Float)
    a3 = np.empty(t[2].shape, dtype=Float)
    a4 = np.empty(t[3].shape, dtype=Float)
    for i in range(t[0].shape[0]):
        for j in range(t[0].shape[1]):
            a1[i][j] = Float.float((t[0][i][j]))
    for i in range(t[1].shape[0]):
        a2[i] = Float.float(t[1][i])
    for i in range(t[2].shape[0]):
        for j in range(t[2].shape[1]):
            a3[i][j] = Float.float(t[2][i][j])
    for i in range(t[3].shape[0]):
        a4[i] = Float.float(t[3][i])
    layer1 = Dense(128, a1, a2, relu)
    layer2 = OutputLayer(10, a3, a4, soft_max)
    pool = multiprocessing.Pool(12)
    queue = multiprocessing.Manager().Queue()
    case = test_x.shape[0]
    for i in range(case):
        pool.apply_async(worker, (test_x[i], test_y[i], layer1, layer2, queue))
    pool.close()
    pool.join()
    correct = 0
    while not queue.empty():
        correct += queue.get()
    print('准确率：', correct / case)

    # 评估模型
    model.evaluate(X_test, y_test, verbose=2)  # 每次迭代输出一条记录，来评价该模型是否有比较好的泛化能力
