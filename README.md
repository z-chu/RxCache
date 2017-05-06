# RxCache
简单一步，缓存搞定。这是一个专用于RxJava，解决Android中网络请求的缓存处理框架。

<img src="/screenshots/s0.gif" alt="screenshot" title="screenshot" width="270" height="486" />

## 特性
### 缓存层级
* 网络
* 磁盘缓存 - DiskLruCache
* 内存缓存 - LruCache

### 缓存策略-尽可能适应多种使用场景
* 仅缓存
* 仅网络
* 优先缓存
* 优先网络
* 先缓存后网络

### 缓存置换算法
* 最久未使用算法（LRU）：最久没有访问的内容作为替换对象

### 存储策略 - 支持不同数据的缓存需求
* 不存储
* 仅内存
* 仅磁盘
* 内存+磁盘

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
.compose(rxCache.<~>transformer（"custom_key", CacheStrategy.firstRemote()))
```
在这里声明缓存策略即可，不影响原有代码结构

调用示例：
```java
gankApi.getHistoryGank(1)
                .compose(rxCache.<GankBean>transformer("custom_key", strategy))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CacheResult<GankBean>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        tvData.setText(e.getMessage());
                    }

                    @Override
                    public void onNext(CacheResult<GankBean> gankBeanCacheResult) {
                        Logger.e(gankBeanCacheResult);
                        if (gankBeanCacheResult.getFrom() == ResultFrom.Cache) {
                            tvData.setText("来自缓存：\n" + gankBeanCacheResult.toString());
                        } else {
                            tvData.setText("来自网络：\n" + gankBeanCacheResult.toString());
                        }

                    }
                });

```


### 引入
```groovy
	dependencies {
	        compile 'com.zchu:rxcache:1.0.0'
	}
```