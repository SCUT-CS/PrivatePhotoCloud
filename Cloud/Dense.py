import numpy as np


class Dense:
    def __init__(self, num, weights, bias, func):
        self.neurons = []
        self.n = num
        for i in range(num):
            self.neurons.append(Neuron(weights[:, i], bias[i], func))

    def forward(self, x):
        res = np.empty(self.n, self.neurons[0].w.dtype)
        for i in range(self.n):
            res[i] = self.neurons[i].forward(x)
        return res


class Neuron:
    def __init__(self, weight, bias, func):
        self.w = weight
        self.f = func
        self.b = bias

    def forward(self, x):
        return self.f((x * self.w).sum() + self.b)


class OutputLayer:
    def __init__(self, num, weights, bias, func):
        self.dense = Dense(num, weights, bias, same)
        self.f = func

    def forward(self, x):
        return self.f(self.dense.forward(x))


def relu(x):
    if x > 0:
        return x
    return 0


def same(x):
    return x


def soft_max(x):
    if not isinstance(x[0], float):
        t = np.empty(x.shape)
        for i in range(x.shape[0]):
            t[i] = np.exp(x[i].recover())
    else:
        t = x
    return np.exp(t) / np.sum(np.exp(t))
