#  项目架构

## Before Start

如果你遇到了任何问题，欢迎一起讨论解决。

Part A是项目代码的相关规范，Part B是项目架构。

## Updates

### 2022.9.19-2022.9.23

#### Task：

完成UI的美化。

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

## Part B

### 算法部分

**Demo时间测试结果：**

1. 低分辨率JPG：0.1s
2. 中分辨率JPG：4s
3. 高分辨率JPG：22s

**以OneDrive为例：**

原图大小：6016×4000

中缩略图：1488×989

小缩略图：400×265

### 文件路径

原图：img.*1

密文1：img.*1.ori.webp		$cacheDir$/Disk1/

密文2：img.*1.ori.webp		$cacheDir$/Disk2/

溢出信息：img.*1		$dataDir$/overflow/

缩略图：img.*1.ori.webp $picturesDir$/Thumbnail/

解密结果：img.*1.jpg $picturesDir$/PPPS

### 多线程

加密和解密部分在文件层次上尝试使用多线程提高效率。

上传下载多线程。

### Camera API

使用CameraX API开发。

### 相册API

实现照片的单选和多选（快捷手势）等。

### 云服务API

- 创建通用的Cloud Interface

- 先使用阿里云OSS对象云存储服务实现
  - [云存储API](https://help.aliyun.com/document_detail/32007.html)
  - [图像处理API](https://help.aliyun.com/document_detail/101260.html)
  
- OSS存储桶已创建(ppps1和ppps2)

  **Bucket 域名：ppps1.oss-cn-hangzhou.aliyuncs.com**

  地域节点： oss-cn-hangzhou.aliyuncs.com

- 调用API使用以下AccessKey

  - **AccessKey泄露会带来极大的安全风险！**
  - AccessKey ID：LTAI5t9Wx9ZwYxCuPEGoxoct
  -  AccessKey Secret：IJWyl2xxwYC1vwaTkw8mZ4hWnKZXxP
  -  LTAI5t9Wx9ZwYxCuPEGoxoct:IJWyl2xxwYC1vwaTkw8mZ4hWnKZXxP

## Evaluation

图片大小S：3024*4032 JJPEG格式 2.39mb

图片大小M：4512*6016 JPEG格式 6.67mb

~~图片大小L：9024*12032~~
