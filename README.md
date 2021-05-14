# RxCache

[ ![Download](https://api.bintray.com/packages/zchu/maven/rxcache/images/download.svg) ](https://bintray.com/zchu/maven/rxcache/_latestVersion)<a href="http://www.methodscount.com/?lib=com.zchu%3Arxcache%3A1.2.6"><img src="https://img.shields.io/badge/Size-42 KB-e91e63.svg"/></a>




简单一步，缓存搞定。这是一个专用于 RxJava，解决 Android 中对任何 Observable 发出的结果做缓存处理的框架。

<img src="/screenshots/s0.gif"  /><img src="/screenshots/s1.gif"  />

[下载APK](https://raw.githubusercontent.com/z-chu/RxCache/master/sample-debug.apk)

## 特性
### 缓存层级

* Observable
* 内存缓存 - LruCache
* 磁盘缓存 - DiskLruCache


### 目前已有的存储策略 

* 优先网络
* 优先缓存
* 优先缓存,并设置超时时间
* 仅加载网络，但数据依然会被缓存
* 先加载缓存，后加载网络
* 仅加载网络，不缓存



## Getting started
### Add the dependencies
```groovy
allprojects {
    repositories {     
        maven { url 'https://jitpack.io' }
    }
}
```
**RxJava 2.0**
```groovy
implementation 'com.github.z-chu.RxCache:rxcache:3.0.0'
```
**可添加 Kotlin 扩展,解决泛型擦除问题**
```groovy
implementation 'com.github.z-chu.RxCache:rxcache-kotlin:3.0.0'
```

### 首先创建一个 RxCache 实例



```java
rxCache = new RxCache.Builder()
                .appVersion(1)//当版本号改变,缓存路径下存储的所有数据都会被清除掉
                .diskDir(new File(getCacheDir().getPath() + File.separator + "data-cache"))
                .diskConverter(new GsonDiskConverter())//支持Serializable、Json(GsonDiskConverter)
                .memoryMax(2*1024*1024)
                .diskMax(20*1024*1024)
                .build();
```
再使用 `compose()`操作符变换, 注意把<~>替换成你的数据类型
```java
observable
	.compose(rxCache.<~>>transformObservable("custom_key", type, CacheStrategy.firstCache()))
	.subscribe(new Observer<CacheResult<~>>() {
		...
		@Override
		public void onNext(CacheResult<~> cacheResult) {
			Object data=cacheResult.getData();//获取你的数据
		}
		...
	}
	
```
## Retrofit

在如果你使用的是 [retrofit](https://github.com/square/retrofit) 那可原有代码的基础上，仅需2行代码搞定，**一步到位！！！**

Observable 调用
```java
//注意在 <~> 中声明数据源的类型
.compose(rxCache.<~>transformObservable（key,type,CacheStrategy.firstCache()))
.map(new CacheResult.MapFunc<~>())
```
Flowable 调用
```java
.compose(rxCache.<~>transformFlowable（key,type,CacheStrategy.firstCache()))
.map(new CacheResult.MapFunc<~>())
```
在这里声明缓存策略即可，不影响原有代码结构

如何你纠结 Key 值的取名，建议使用 **("方法名"+"参数名："+"加参数值")**


## CacheStrategy
在`CacheStrategy` 类中提供如下缓存策略：

 策略选择                   | 摘要      
 ------------------------- | ------- 
 firstRemote()             | 优先网络
 firstCache() |优先缓存
 firstCacheTimeout(milliSecond) |优先缓存,并设置超时时间
 onlyRemote() | 仅加载网络，但数据依然会被缓存
 onlyCache()           | 仅加载缓存 
 cacheAndRemote()              | 先加载缓存，后加载网络   
 none()              | 仅加载网络，不缓存

缓存的保存会在数据响应后用异步的方式保存，不会影响数据的响应时间。

如需要用同步方式保存，每个策略都有对应的同步保存方式
如： `CacheStrategy.firstRemoteSync()`
使用同步保存方式，数据会在缓存写入完以后才响应。


## CacheResult
`CacheResult` 类，包含的属性如下:

```java
public class CacheResult<T> {
    private ResultFrom from;//数据来源，原始observable、内存或硬盘
    private String key;
    private T data; // 数据
    private long timestamp; //数据写入到缓存时的时间戳，如果来自原始observable则为0
	...
}
```


## Default rxCache
你也可以使用默认的 `RxCache`:
<br/>
初始化默认的 `RxCache`
```java
RxCache.initializeDefault(rxcache)
```
再这样使用
```java
observable
	.compose(RxCache.getDefault().<~>>transformObservable("custom_key", type, strategy))
	...
```
如果不初始化默认的 `RxCache`，这样使用缓存会保存到 `Environment.getDownloadCacheDirectory()` <br/> 且 `appVersion` 会永远为 `1`


## Kotlin
**推荐使用 kotlin** ，规避了泛型擦除，可不传 `type`, 无比简单 :

```kotlin
observable
	.rxCache("custom_key", strategy) //这样会使用默认的 RxCache ，你也可以传入任意 rxcache 使用
	.subscribe(object : Observer<CacheResult<~>>  {
		...
	}
	
```






## 泛型
因为泛型擦除的原因，遇到 List<~> 这样的泛型时可以这样使用：

```java
// <~> 为List元素的数据类型
.compose(rxCache.<List<~>>transformer("custom_key", new TypeToken<List<~>>() {}.getType(), strategy))
```

没有泛型时 Type 直接传 Class 即可
```java
.compose(rxCache.<Bean>transformer("custom_key",Bean.class, strategy))
```

**如果你使用 Kotlin 则没有这个问题**
```kotlin
.rxCache(rxcache,"custom_key", strategy)
```



## 基础用法

### 保存缓存：
如 保存字符串到内存和硬盘：
```java
rxCache
	.save("test_key1","RxCache is simple", CacheTarget.MemoryAndDisk)
	.subscribeOn(Schedulers.io())
	.subscribe();
```
保存方式提供了 3 种选择：
```java
public enum CacheTarget {
    Memory,
    Disk,
    MemoryAndDisk;
...
}
```


### 读取缓存：
读取的顺序会按照内存-->硬盘的顺序读取
如 读取缓存中的字符串：
```java
 rxCache
	.<String>load("test_key1", String.class)
	.map(new CacheResult.MapFunc<String>())
	.subscribe(new Consumer<String>() {
		@Override
		public void accept(String value) throws Exception {
			
		}
	});
```

同步获取缓存：
```java
 CacheResult<String> = rxCache.<String>loadSync("test_key1", String.class);
```



## 混淆配置
本 Library 不需求添加额外混淆配置，所以代码都可被混淆

