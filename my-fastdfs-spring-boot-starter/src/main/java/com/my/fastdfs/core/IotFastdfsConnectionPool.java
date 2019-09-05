package com.my.fastdfs.core;

import com.my.fastdfs.properties.IotFastdfsProperties;
import org.csource.fastdfs.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public class IotFastdfsConnectionPool {
	private Object obj = new Object();
	private IotFastdfsProperties iotFastdfsProperties;
	/**
	 * 被使用的连接
	 */
	private ConcurrentHashMap<StorageClient1, Object> busyConnectionPool = new ConcurrentHashMap<StorageClient1, Object>();
	/**
	 * 空闲的连接
	 */
	private ArrayBlockingQueue<StorageClient1> idleConnectionPool;

	/**
	 * 构造器
	 * @param iotFastdfsProperties
	 */
	public IotFastdfsConnectionPool(IotFastdfsProperties iotFastdfsProperties) {
		this.iotFastdfsProperties=iotFastdfsProperties;
		idleConnectionPool = new ArrayBlockingQueue<>(Integer.parseInt(iotFastdfsProperties.getMaxSize()));
		init();
	}
	/**
	 * 取出链接
	 * @param waitTime 秒
	 * @return
	 */
	public StorageClient1 getStorageClinet(int waitTime) {
		StorageClient1 storageClient1 = null;
		try {
			storageClient1 = idleConnectionPool.poll(waitTime, TimeUnit.SECONDS);
			if (storageClient1 != null) {
				busyConnectionPool.put(storageClient1, obj);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return storageClient1;
	}

	/**
	 * 回收连接
	 * @param storageClient1
	 */
	public void giveBack(StorageClient1 storageClient1) {
		if (storageClient1!=null && busyConnectionPool.remove(storageClient1) != null) {
			idleConnectionPool.add(storageClient1);
		}
	}

	/**
	 *  初始化连接池
	 */
	private void init() {
		try {
			int coreSize = Integer.parseInt(iotFastdfsProperties.getCoreSize());
			for (int i = 0; i < coreSize; i++) {
				for (int i1 = 0; i1 < 5; i1++) {
                    StorageClient1 storageClient1One = getStorageClient1One();
                    if(storageClient1One!=null){
                        idleConnectionPool.add(storageClient1One);
                        break;
                    }
                }
			}
		}catch (Exception e){
			System.out.println("初始化fastdfs连接池失败！");
			e.printStackTrace();
		}
	}

	/**
	 * 获取一个StorageClient1
	 * @return
	 * @throws IOException
	 */
	private StorageClient1 getStorageClient1One(){
		TrackerServer trackerServer =null;
		StorageClient1 storageClient1=null;
		try {
			ClientGlobal.setG_charset(iotFastdfsProperties.getCharset());
			ClientGlobal.setG_connect_timeout(Integer.parseInt(iotFastdfsProperties.getConnectTimeout()));
			ClientGlobal.setG_network_timeout(Integer.parseInt(iotFastdfsProperties.getNetworkTimeout()));
			ClientGlobal.setG_anti_steal_token(false);
			//截取地址
			String trackerServers = iotFastdfsProperties.getTrackerServers();
			String[] split = trackerServers.split(",");
			InetSocketAddress[] tracker_servers = new InetSocketAddress[split.length];
			for (int i = 0; i < split.length; i++) {
				String[] split1 = split[i].split(":");
				tracker_servers[i] = new InetSocketAddress(split1[0],Integer.parseInt(split1[1]));
			}
			ClientGlobal.setG_tracker_group(new TrackerGroup(tracker_servers));
			trackerServer = new TrackerClient().getConnection();
			storageClient1= new StorageClient1(trackerServer, null);
		}catch (Exception e){
			e.printStackTrace();
			close(trackerServer);
		}
		return storageClient1;
	}

	private void close(TrackerServer trackerServer){
		if (trackerServer != null) {
			try {
				trackerServer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}