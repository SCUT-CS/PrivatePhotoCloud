#  项目架构

## Before Start

如果你遇到了任何问题，欢迎一起讨论解决。

Part A是项目代码的相关规范，Part B是项目架构。

## Updates

#### 2022.7.21

1. Demo已上传到`PPPS-Algorithm`文件夹。
2. 使用JDK17。
3. 使用Android API 28（Android Pie 新增 ImageDecoder API，对HEIF格式支持更好）。

## Part A

### IDE和Git

使用Java语言，Android Studio开发。（Android Studio基于Intellij开发，暂不支持中文，可以先使用Intellij来熟悉java）

熟悉一下GitHub Desktop的相关操作及Git的有关概念。

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
/** [Description here]
 *  @author [Your name]
 *  @param [Parameters]
 *  @source (optional)
 */
public class Main {
    
    public static void main(String[] args) {
        
    }
}
```

#### Check Style

安装CS61B插件后，在源代码文件上右键会出现Check Style。

## Part B

### UI

UI的制作。

### 加密部分

包括加密、解密以及缩略图的本地计算。

- 加密不足一格的bug。
- 图像EXIF信息的加密。

### 多线程

加密和解密部分尝试使用多线程提高效率。

### Camera API

调用系统相机。

### 相册API

实现照片的单选和多选（快捷手势）等，自己完成。

### 云服务API

- 官方提供API的云服务可以直接使用。
- 多种云服务的支持（Interface）。
- 不提供API的云服务可能需要打开登录网页，获取Cookie或者Token得到云服务的访问权限。
  - 可以在GitHub搜索相关实现，但要注意其开源协议。
  - 使用其他来源的代码或者函数需要**在注释区域的@source字段表明来源**。

## TODO：

以下内容按照重要程度排序。

- 多种图片格式的支持（*.heif 等）
- 不同色彩范围（黑白？）和色深的支持
- 后台、服务、状态栏
- 自动同步
