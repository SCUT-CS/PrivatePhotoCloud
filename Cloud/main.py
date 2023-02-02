import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers, optimizers, losses, datasets, Sequential
from tensorflow.keras.datasets import mnist
from tensorflow.keras.layers import Dense, Dropout, Activation, Flatten, Conv2D, MaxPooling2D, GlobalMaxPool2D
from tensorflow.keras.optimizers import RMSprop
import matplotlib.pyplot as plt
import numpy as np

# 数据集的加载
(x_train, y_train), (x_test, y_test) = datasets.mnist.load_data()  # 加载手写数据集数据
x_train, x_test = x_train / 255.0, x_test / 255.0

# 处理数据使其增加一个维度
x_train = np.expand_dims(x_train, axis=3)
x_test = np.expand_dims(x_test, axis=3)
# 打印查看数据
print("train shape:", x_train.shape)
print("test shape:", x_test.shape)

# 模型建立
datagen = tf.keras.preprocessing.image.ImageDataGenerator(
    rotation_range=20,
    width_shift_range=0.20,
    shear_range=15,
    zoom_range=0.10,
    validation_split=0.15,
    horizontal_flip=False
)

train_generator = datagen.flow(
    x_train,
    y_train,
    batch_size=256,
    subset='training',
)

validation_generator = datagen.flow(
    x_train,
    y_train,
    batch_size=64,
    subset='validation',
)
# 定义模型
def create_model():
    model = tf.keras.Sequential([
        tf.keras.layers.Reshape((28, 28, 1)),
        tf.keras.layers.Conv2D(filters=32, kernel_size=(5, 5), activation="relu", padding="same",
                               input_shape=(28, 28, 1)),
        tf.keras.layers.MaxPool2D((2, 2)),

        tf.keras.layers.Conv2D(filters=64, kernel_size=(3, 3), activation="relu", padding="same"),
        tf.keras.layers.Conv2D(filters=64, kernel_size=(3, 3), activation="relu", padding="same"),
        tf.keras.layers.MaxPool2D((2, 2)),

        tf.keras.layers.Conv2D(filters=128, kernel_size=(3, 3), activation="relu", padding="same"),
        tf.keras.layers.Conv2D(filters=128, kernel_size=(3, 3), activation="relu", padding="same"),
        tf.keras.layers.MaxPool2D((2, 2)),

        tf.keras.layers.Flatten(),
        tf.keras.layers.Dense(512, activation="sigmoid"),
        tf.keras.layers.Dropout(0.25),

        tf.keras.layers.Dense(512, activation="sigmoid"),
        tf.keras.layers.Dropout(0.25),

        tf.keras.layers.Dense(256, activation="sigmoid"),
        tf.keras.layers.Dropout(0.1),

        tf.keras.layers.Dense(10, activation="sigmoid")
    ])

    model.compile(optimizer="adam",
                  loss='sparse_categorical_crossentropy',
                  metrics=['accuracy'])

    return model


model = create_model()

# 模型训练参数的设置
reduce_lr = tf.keras.callbacks.ReduceLROnPlateau(monitor='val_loss',
                                                 factor=0.1,
                                                 patience=5,
                                                 min_lr=0.000001,
                                                 verbose=1)

checkpoint = tf.keras.callbacks.ModelCheckpoint(filepath='model.hdf5',
                                                monitor='val_loss',
                                                save_best_only=True,
                                                save_weights_only=True,
                                                verbose=1)

# 模型训练
history = model.fit(train_generator,
                    epochs=10,
                    validation_data=validation_generator,
                    callbacks=[reduce_lr, checkpoint],
                    verbose=1)
print(model.summary())

# 模型测试
loss, acc = model.evaluate(x_test, y_test)
print("accuracy:{:5.2f}%".format(100 * acc))

model.load_weights('model.hdf5')
final_loss, final_acc = model.evaluate(x_test,  y_test, verbose=2)
print("final_test_accuracy: ", final_acc, ", model loss: ", final_loss)

# 绘制训练过程中训练集和测试集合的准确率值
plt.plot(history.history['accuracy'])
plt.plot(history.history['val_accuracy'])
plt.title('Model accuracy')
plt.ylabel('Accuracy')
plt.xlabel('Epoch')
plt.legend(['Train', 'Test'], loc='upper left')
plt.show()

# 绘制训练过程中训练集和测试集合的损失值
plt.plot(history.history['loss'])
plt.plot(history.history['val_loss'])
plt.title('Model loss')
plt.ylabel('Loss')
plt.xlabel('Epoch')
plt.legend(['Train', 'Test'], loc='upper left')
plt.show()
