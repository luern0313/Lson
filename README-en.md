# Lson

A Java library for JSON serialization/deserialization, it has good performance and high extensibility.  

[![Build Status](https://travis-ci.com/luern0313/Lson.svg?branch=master)](https://travis-ci.com/luern0313/Lson)
[![GitHub release](https://img.shields.io/github/release/luern0313/Lson.svg)](https://github.com/luern0313/Lson)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cn.luern0313.lson/Lson/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cn.luern0313.lson/Lson)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
![Language](https://img.shields.io/badge/language-java-yellow.svg)

[中文](README.md)&#160;&#160;&#160;|&#160;&#160;&#160;English

## Features

* Easy to get started, quick to use with installation
* Intuitive, convenient, and easy to use serialization/deserialization process based on annotations and JSONPath
* Support for deserializing complex entity classes and variables
* Good support for generics
* Continuously updated built-in annotations for easy data processing
* Customizable annotations to extend Lson's functionality

## Download

### Gradle

``` groovy
implementation 'cn.luern0313.lson:Lson:0.90'
```

### Maven

``` xml
<dependency>
    <groupId>cn.luern0313.lson</groupId>
    <artifactId>Lson</artifactId>
    <version>0.90</version>
</dependency>
```

## Getting Started

### JSON Parsing

``` java
LsonElement lsonElement = LsonUtil.parse(jsonString);
```

This method returns a LsonElement. If you are sure that the JSON is an Object or Array, you can also use:

``` java
LsonObject lsonObject = LsonUtil.parseAsObject(jsonString);
```

or

``` java
LsonArray lsonArray = LsonUtil.parseAsArray(jsonString);
```

### JSON Deserialization

``` java
XXXModel model = LsonUtil.fromJson(lsonElement, XXXModel.class);
```

You need an entity class for Lson to deserialize, and you need to add the @LsonPath annotation to the variables you want to deserialize and fill in the path to describe the location of this variable in the JSON.
You can also use Lson's built-in annotations or custom annotations to handle variables in deserialization.
For more detailed usage, see the [Lson Wiki](https://github.com/luern0313/Lson/wiki)。

### JSON Serialization

``` java
String json = LsonUtil.toJson(model);
```

Again, the @LsonPath annotation is also needed for Lson to locate the value in a specific location in the json.
Built-in and custom annotations can also be used for serialization.
For more detailed usage, see the [Lson Wiki](https://github.com/luern0313/Lson/wiki)。

## Contribute to this project

You are welcome to grow with this project!
To contribute your code, please Fork and Pull Requests.
Or [raise an Issue](https://github.com/luern0313/Lson/issues) to report problems or suggestions.

## Developers

[luern0313](https://github.com/luern0313)  

Special thanks to: [lzjyzq2](https://github.com/lzjyzq2)、[setTile group](https://github.com/setTileGroup)

## Contact me

Bilibili: [https://space.bilibili.com/8014831](https://space.bilibili.com/8014831)  
Github: [https://github.com/luern0313](https://github.com/luern0313)  
Personal website: [https://luern0313.cn](https://luern0313.cn)

## License

Lson uses the[Apache 2.0 license](LICENSE.txt)。

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
