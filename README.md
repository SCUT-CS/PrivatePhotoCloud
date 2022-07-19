#  项目架构

## Before Start

如果你遇到了任何问题，欢迎一起讨论解决。

Part A是项目代码的相关规范，Part B是项目架构。

## 目录

[Part A](#Part A)

​	[IDE和Git](#IDE和Git)

​	[代码规范](#代码规范)

​		[Code Style](#Code Style)

​		[注释](#注释)

​		[Check Style](#Check Style)

[Part B](#Part B)

​	[UI](#UI)

​	[加密部分](#加密部分)

​	[多线程](#多线程)

​	[Camera API](#Camera API)

​	[相册API](#相册API)

​	[云服务API](云服务API)

[TODO](#TODO)

## Part A

### IDE和Git

使用Java语言，Android Studio开发。（Android Studio基于Intellij开发，暂不支持中文，可以先使用Intellij来熟悉java）

建议尝试一下GitHub的Copilot插件，学生认证后免费。

建议熟悉一下GitHub Desktop的相关操作及概念。

在开发分支上面编写代码，通过Code Review后合并进入主分支。

### 代码规范

#### Code Style

- Some of the most important features of good coding style are:
- Size (**lines that are not too wide**, source files that are not too long)
- **Descriptive naming** (variables, functions, classes), e.g. variables or functions with names like `year` or `getUserName` instead of `x` or `f`.
- **Avoidance of repetitive code**: You should almost never have two significant blocks of code that are nearly identical except for a few changes.

#### 注释

```java
/** [Description here]
 *  @author [Your name]
 *  @source (optional)
 */
public class Main {
    
    public static void main(String[] args) {
        
    }
}
```

#### Check Style

安装CS61B插件

在源代码文件上右键会出现Check Style

<u>请注意：在提交代码到仓库主分支之前务必参考上面的内容**编写注释**并**通过代码风格检查**。</u>

## Part B

### UI

UI的制作

### 加密部分

包括加密、解密以及缩略图的本地计算。

### 多线程

加密和解密部分尝试使用多线程提高效率。

### Camera API

实现拍照并保存在相册的功能，具体需求和实现待定。

暂不确定调用系统相机服务或自己完成。

### 相册API

实现照片的单选和多选（快捷手势）。

暂不确定调用系统文件（相册）服务或自己完成。

### 云服务API

- 官方提供API的云服务可以直接使用
- 不提供API的云服务可能需要打开登录网页，获取Cookie或者Token得到云服务的访问权限
  - 可以在GitHub搜索相关实现，但要注意其开源协议。
  - 使用其他来源的代码或者函数需要**在注释区域的@source字段表明来源和作者**

## TODO：

- 图像EXIF信息的加密
- 加密不足一格的bug
- 选项菜单的简单版本和高级选项
- 多种云服务的支持
- 多种图片的支持（*.heif 等）
- 不同色彩范围或色深的支持