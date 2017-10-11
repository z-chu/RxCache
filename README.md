# RxCache

[ ![Download](https://api.bintray.com/packages/zchu/maven/rxcache/images/download.svg) ](https://bintray.com/zchu/maven/rxcache/_latestVersion)

简单一步，缓存搞定。这是一个专用于RxJava，解决Android中网络请求的缓存处理框架。

<img src="/screenshots/s0.gif" alt="screenshot" title="screenshot" width="270" height="486" />

## 特性
### 缓存层级
* 网络
* 磁盘缓存 - DiskLruCache
* 内存缓存 - LruCache


### 缓存置换算法
* 最久未使用算法（LRU）：最久没有访问的内容作为替换对象

### 存储策略 - 支持不同数据的缓存需求
* 仅内存
* 仅磁盘
* 内存+磁盘

## 引入
* **RxJava 2.0**
```groovy
dependencies {
	compile 'com.zchu:rxcache:2.0.5'
}
```
* **RxJava 1.0**
```groovy
dependencies {
	compile 'com.zchu:rxcache:1.2.6'
}
```

## 如何使用
准备RxCache,可以用单例模式创建一个全局的RxCache
```java
rxCache = new RxCache.Builder()
                .appVersion(1)
                .diskDir(new File(getCacheDir().getPath() + File.separator + "data-cache"))
                .diskConverter(new SerializableDiskConverter())//支持Serializable、Json(GsonDiskConverter)
                .memoryMax(2*1024*1024)
                .diskMax(20*1024*1024)
                .build();
```
在原有代码的基础上，仅需一行代码搞定，**一步到位！！！**
```java
//Observable调用，注意在<~>中声明数据源的类型
.compose(rxCache.<~>transformObservable（key,type,strategy))

//Flowable也是支持的
.compose(rxCache.<~>transformFlowable（key,type,strategy))
```
在这里声明缓存策略即可，不影响原有代码结构

调用示例：
```java
gankApi.getHistoryGank(1)
                .compose(rxCache.<GankBean>transformObservable("custom_key", GankBean.class, strategy))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CacheResult<GankBean>>() {
                    @Override
                    public void accept(@NonNull CacheResult<GankBean> result) throws Exception {
                        GankBean data = result.getData();
                        if (result.getFrom() == ResultFrom.Cache) {
							//来自缓存
                        } else {
							//来自网络
                        }
                    }
                });

```
## 泛型
因为泛型擦除的原因，遇到List<~>这样的泛型时可以使用：

```java
//<~>为List元素的数据类型
.compose(rxCache.<List<~>>transformer("custom_key", new TypeToken<List<~>>() {}.getType(), strategy))
```

没有泛型时Type直接传Class即可
```java
//<~>为List元素的数据类型
.compose(rxCache.<Bean>transformer("custom_key",Bean.class, strategy))
```

## 策略选择
`CacheStrategy`类中可供选择的策略如下：

 策略选择                   | 摘要      
 ------------------------- | ------- 
 firstRemote()             | 优先网络
 firstCache() |优先缓存
 onlyRemote() | 仅加载网络，但数据依然会被缓存
 onlyCache()           | 仅加载缓存 
 cacheAndRemote()              | 先加载缓存，后加载网络   
 none()              | 仅加载网络，不缓存

缓存的保存会在数据响应后用异步的方式保存，不会影响数据的响应时间。

如需要用同步方式保存，每个策略都有对应的同步保存方式
如：`CacheStrategy.firstRemoteSync()`
使用同步保存方式，数据会在缓存写入完以后才响应。
