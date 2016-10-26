# RxCache
简单一步，缓存搞定。这是一个专用于RxJava，解决Android中网络请求的缓存处理框架。

<img src="/screenshots/s0.gif" alt="screenshot" title="screenshot" width="270" height="486" />

##特性
###缓存层级

* 网络
* 磁盘缓存 - DiskLruCache
* 内存缓存 - LruCache

###缓存策略 

* 仅缓存
* 仅网络
* 优先缓存
* 优先网络
* 先缓存后网络

##如何使用

准备RxCache
```java
rxCache = new RxCache.Builder()
                .appVersion(1)//不设置，默认为1
                .diskDir(new File(getCacheDir().getPath() + File.separator + "data-cache"))
                .diskConverter(new SerializableDiskConverter())//目前只支持Serializable缓存
                .memoryMax(2*1024*1024)//不设置,默认为运行内存的8分之1
                .diskMax(20*1024*1024)//不设置， 默为认50MB
                .build();
```
在原有代码的基础上，仅需一行代码搞定
```java
.compose(rxCache.transformer(MD5.getMessageDigest("custom_key"), CacheStrategy.firstRemote()))
```
在这里声明缓存策略即可，不影响原有代码结构

###引入
此lib加入了JitPack 引用方法 根目录下的build.gradle

```groovy
	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```
项目中
```groovy
	dependencies {
	        compile 'com.github.z-chu:RxCache:1.0'
	}
```