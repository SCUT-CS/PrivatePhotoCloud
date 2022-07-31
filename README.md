#  项目架构

## Before Start

如果你遇到了任何问题，欢迎一起讨论解决。

Part A是项目代码的相关规范，Part B是项目架构。

## Updates

### 2022.7.23

创建Android项目。

- Android Studio第一次加载需要从Google下载一部分包和依赖，可能需要科学上网。

### 2022.7.21

1. Demo已上传到`PPPS-Algorithm`文件夹。
2. 使用JDK17。
3. 使用Android API 28（Android Pie 新增 ImageDecoder API，对HEIF格式支持更好）。

## Part A

### IDE和Git

使用Java语言，Android Studio开发。（Android Studio基于Intellij开发，暂不支持中文，可以先使用Intellij来熟悉java）

**熟悉一下GitHub Desktop的相关操作及Git的有关概念。**

建议尝试一下GitHub的Copilot插件，学生认证后免费。

### 代码规范

在开发分支上面编写代码，通过**Code Review**以及**代码风格检查**后合并进入主分支。

<u>请注意：在提交代码到仓库主分支之前务必参考上面的内容**编写注释**并**通过代码风格检查**。</u>

#### Code Style

Some of the most important features of good coding style are:

- Size (**lines that are not too wide**, source files that are not too long)
- **Descriptive naming** (variables, functions, classes), e.g. variables or functions with names like `year` or `getUserName` instead of `x` or `f`.
- **Avoidance of repetitive code**: You should almost never have two significant blocks of code that are nearly identical except for a few changes.

#### 注释格式

```java
/** 
 * [Description here（return what）]
 * @param [Parameters and Description](optional)
 * @param [Parameters and Description](optional)
 * @author [Your name]
 * @source (optional)
 */

/**
 * encrypt a image and return true if success.
 * @param img the image to be encrypted.
 * @author Cui Yuxin
 */
private boolean encrypt(Bitmap img) {
        
}
```

#### Check Style

安装CS61B插件后，在源代码文件上右键会出现Check Style。

## Part B

### UI

UI的制作。

### 加密部分

包括加密、解密以及缩略图的本地计算。

*TODO：图像EXIF信息的加密。*

- 本地缩略图的计算需要两个云端缩略图和一个溢出数组，对于溢出数组的存储提出以下思路：

  1. ~~利用溢出数组取缩略不影响生成的特性，将溢出数组存在云端其中一个图像的透明度通道中。~~
     
     - ~~优点：实现简单，溢出数组的缩略计算由云端提供。~~
     - ~~缺点：如果原始图片有透明度通道的话，原始图片的透明度通道要不加密存储在云端的另一个图像中。另外，云服务提供的缩略图可能不支持透明度通道。~~
     
  2. ~~利用LSB编码，使用RGB通道的最低位存储数组。~~
  
     ~~LSB不抗压缩，除非云端使用取样的方法计算缩略图。~~
  
     - ~~RGB通道的最低位需要不加密存储在云端的另一个图像中。另外，此方法取决于云服务计算缩略图的算法，如果是平均值的话会损坏数组，取样的话会降低缩略图结果的画质。~~
  
  3. ~~[StegaStamp: Invisible Hyperlinks in Physical Photographs](https://www.matthewtancik.com/stegastamp)~~
  
     ~~只能写入100位，远小于溢出数组大小。~~
  
  4. ~~[blind-watermark](https://github.com/guofei9987/blind_watermark)~~
  
     ~~时间复杂度过高。~~
  
  5. **将两张图片延长部分存放数组。 溢出数组可能泄露信息！需要混淆后编码进图片。**
  
  6. ~~单独的文件保存在本地或云端。~~
  
- 使用Android API 28 提供的ImageDecoder类进行图片处理。

- 新增对HEIF文件的支持。

- 新增对透明度的支持。

- 由于API的限制，色域限制在了8bit sRGB。

**Demo时间测试结果：**

1. 低分辨率JPG：0.1s
2. 中分辨率JPG：4s
3. 高分辨率JPG：22s

**所以在图片的层级上多线程处理**

**以OneDrive为例：**

原图大小：6016×4000

中缩略图：1488×989

小缩略图：400×265

### 多线程

加密和解密部分尝试使用多线程提高效率。

### Camera API

调用系统相机。

### 相册API

实现照片的单选和多选（快捷手势）等，自己完成。

### 云服务API

- 创建通用的Cloud Interface
- 先使用阿里云OSS对象云存储服务实现
  - [云存储API](https://help.aliyun.com/document_detail/31947.html)
  - [图像处理API](https://help.aliyun.com/document_detail/101260.html)

## TODO：

- 黑白图片的支持？
- 后台、服务、状态栏
- 自动同步
