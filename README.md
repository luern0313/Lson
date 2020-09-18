# Lson

一个用于JSON序列化/反序列化的Java库，拥有良好的性能和高拓展性。  
A Java library for JSON serialization/deserialization, it has good performance and high extensibility.  

[![Build Status](https://travis-ci.com/luern0313/Lson.svg?branch=master)](https://travis-ci.com/luern0313/Lson)
[![GitHub release](https://img.shields.io/github/release/luern0313/Lson.svg)](https://github.com/luern0313/Lson)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cn.luern0313.lson/Lson/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cn.luern0313.lson/Lson)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
![Language](https://img.shields.io/badge/language-java-yellow.svg)

中文&#160;&#160;&#160;|&#160;&#160;&#160;[English](README-en.md)

## 功能

* 容易上手，安装即可快速使用
* 基于注解和JSONPath工作的反序列化/序列化流程，直观、方便、容易使用
* 支持复杂实体类和变量的反序列化等
* 对泛型的良好支持
* 不断更新的内置注解，方便处理数据
* 可自定义注解，拓展Lson功能

## 下载

### Gradle

``` groovy
implementation 'cn.luern0313.lson:Lson:0.3'
```

### Maven

``` xml
<dependency>
    <groupId>cn.luern0313.lson</groupId>
    <artifactId>Lson</artifactId>
    <version>0.3</version>
</dependency>
```

## 快速上手

### JSON解析

``` java
LsonElement lsonElement = LsonParser.parse(jsonString);
```

该方法返回一个LsonElement，若你能确定JSON为Object或Array，你也可以使用:  

``` java
LsonObject lsonObject = LsonParser.parseAsObject(jsonString);
```

或

``` java
LsonArray lsonArray = LsonParser.parseAsArray(jsonString);
```

### JSON反序列化

``` java
XXXModel model = LsonUtil.fromJson(lsonElement, XXXModel.class);
```

你需要一个实体类来供Lson反序列化，在需要反序列化的变量上添加注解@LsonPath，并填写path来描述此变量在JSON中的位置。详细用法请查看[Lson Wiki](https://github.com/luern0313/Lson/wiki)。

### JSON序列化

敬请期待

## 参与此项目

欢迎你与本项目一起成长！  
要贡献你的代码，请Fork后Pull Requests。  
或[提出Issue](https://github.com/luern0313/Lson/issues)反馈问题或建议。

## 开发者

[luern0313](https://github.com/luern0313)  

特别感谢: [lzjyzq2](https://github.com/lzjyzq2)

## 联系我

Bilibili: [https://space.bilibili.com/8014831](https://space.bilibili.com/8014831)  
Github: [https://github.com/luern0313](https://github.com/luern0313)  
个人网站: [https://luern0313.cn](https://luern0313.cn)

## 许可证

Lson使用[Apache 2.0 license](LICENSE.txt)。

``` txt
Copyright 2020 luern0313

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
