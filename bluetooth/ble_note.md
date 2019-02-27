# Android BLE相关知识点的学习([摘](https://blog.csdn.net/xiaoyaoyou1212/article/details/51854454))
## 分类（分为3种）
* Bluetooth Smart Ready（bsr）
* Bluetooth Smart（bs）
* 标准Bluetooth
### 适用范围
* bsr 适用于任何双模蓝牙4.0的电子产品
* bs 应用在心率监视器或者记不起等适用纽扣电池并传输单一的装置
### 注意
bs可以和标准Bluetooth相通，但是标准Bluetooth无法和bs相通
## BLE介绍（Bluetooth Low Energy又名蓝牙4.0）
* 主要用于实现移动智能终端与周边配件之间的持续连接，是功耗极低的短距离无线通信技术，并且有效传输距离被提升到了100米以上。
* Android是在4.3后才支持BLE，这说明不是所有蓝牙手机都支持BLE，而且支持BLE的蓝牙手机一般是双模的。双模兼容传统蓝牙，可以和传统蓝牙通信，也可以和BLE通信。
***
## Android蓝牙开发基本名词解释
#### Generic Access Profile(GAP，通用接入描述文件)
>用来控制设备连接和广播，GAP使你的设备被其他设备可见，并决定了你的设备是否可以或者怎样与合同设备进行交互。
#### Generic Attribute Profile(GATT，通用属性描述文件)
>通过BLE连接，读写属性类数据的Profile通用规范，现在所有的BLE应用Profile都是基于GATT的。
#### Attribute Protocol (ATT，属性协议)
>GATT是基于ATTProtocol的，ATT针对BLE设备做了专门的优化，具体就是在传输过程中使用尽量少的数据，每个属性都有一个唯一的UUID，属性将以**characteristics and services**的形式传输。
#### Characteristic（一种数据类型）
>它包括一个value和0至多个对次value的描述（Descriptor）。
#### Descriptor（计量单位，针对于Characteristic）
>对Characteristic的描述，例如范围、计量单位等
#### Service（Characteristic集合）
>例如一个service叫做“Heart Rate Monitor”，它可能包含多个Characteristics。
#### UUID（唯一标识符）
>每个Service，Characteristic，Descriptor，都是由一个UUID定义。
***
## Android BLE相关API及介绍
#### BluetoothGatt（蓝牙设备从连接到断开的生命周期）
>继承BluetoothProfile，通过BluetoothGatt可以连接设备（connect）,发现服务（discoverServices），并把相应地属性返回到BluetoothGattCallback。
#### BluetoothGattCharacteristic（相当于一个数据类型）
>可以看成一个特征或能力，它包括一个value和0~n个value的描述（BluetoothGattDescriptor）。
#### BluetoothGattDescriptor（描述符）
>对Characteristic的描述，包括范围、计量单位等。
#### BluetoothGattService（服务）
>一个相对于描述符（BluetoothGattDescriptor）的集合。
#### BluetoothProfile（通用的规范）
>按照这个规范来收发数据
#### BluetoothManager
>使用这个来获取BluetoothAdapter
```java
BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
```
#### BluetoothAdapter（蓝牙适配器）
>可以通过适配器对蓝牙进行基本操作，一个Android系统只有一个Adapter，通过上述方式获取。
```java
BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
```
#### BluetoothDevice（蓝牙设备）
>扫描后发现可连接的设备，获取已连接的设备
```java
BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
```
#### BluetoothGattCallback（操作后回调）
>连接成功后，对蓝牙设备进行操作后的回调监听。
```java
BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback(){
    //实现回调方法，根据业务做相应处理
};
BluetoothGatt bluetoothGatt = bluetoothDevice.connectGatt(this, false, bluetoothGattCallback);
```
***
## 具体的操作流程
#### 开启蓝牙和检测本机是否支持BLE
```java
//是否支持蓝牙BLE
public static boolean isSupportBle(Context context) {
    if(context != null && context.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
        BluetoothManager manager = (BluetoothManager)context.getSystemService("bluetooth");
        return manager.getAdapter() != null;
    } else {
        return false;
    }
}
//开启蓝牙
public static void enableBle(Activity act, int requestCode) {
    Intent mIntent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
    act.startActivityForResult(mIntent, requestCode);
}
```
#### 搜索设备
##### 注意
* 可以通过BluetoothAdapter.startDiscovery来发现经典蓝牙和BLE的（大多数手机都支持）
* startDiscovery的回调无法返回BLE的广播，所以无法通过广播识别设备
* startDiscovery扫描Ble的效率比StartLeScan低很多
>最后，通过StartDiscovery和StartLeScan分开扫，前者扫传统蓝牙，后者扫低功耗蓝牙。
##### 具体代码演示
```java
//扫描BLE设备
BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
bluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        //对扫描到的设备进行处理，可以依据BluetoothDevice中的信息、信号强度rssi以及广播包和响应包组成的scanRecord字节数组进行分析
    }
});
```
#### 设备通信
>设备通信过程：Android端作为Client端 -> 连接GATT Server -> 连接后回调BluetoothGatt对象 -> 通过gatt对象进行相关操作
```java
//获取BLE设备
BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
//回调初始化
BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback(){
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt, descriptor, status);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        super.onReliableWriteCompleted(gatt, status);
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        super.onReadRemoteRssi(gatt, rssi, status);
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        super.onMtuChanged(gatt, mtu, status);
    }
};

//三个参数为：Context、autoConnect(boolean)和 BluetoothGattCallback 对象
BluetoothGatt bluetoothGatt = bluetoothDevice.connectGatt(this, false, bluetoothGattCallback);

//一些实例的操作方法
bluetoothGatt.setCharacteristicNotification(characteristic, true);

//readCharacteristic to onCharacteristicRead；
bluetoothGatt.readCharacteristic(characteristic);

//writeCharacteristic to onCharacteristicWrite；
bluetoothGatt.wirteCharacteristic(mCurrentcharacteristic);

//连接和断开连接
bluetoothGatt.connect();
bluetoothGatt.disconnect();

//readDescriptor to onDescriptorRead；
bluetoothGatt.readDescriptor(descriptor);

//writeDescriptor to onDescriptorWrite；
bluetoothGatt.writeDescriptor(descriptor);

//readRemoteRssi to onReadRemoteRssi；
bluetoothGatt.readRemoteRssi();

//executeReliableWrite to onReliableWriteCompleted；
bluetoothGatt.executeReliableWrite();

//发现服务
bluetoothGatt.discoverServices();
```
#### 数据解析
>BLE有两种类型的设备
* Central:中心设备，中心设备可以主动连接外围设备，外围设备发送广播或者被中心设备连接
* Peripheral：外围设备，围通过广播被中心设备发现，广播中带有外围设备自身的相关信息
>数据包有两种：
* 广播包
* 响应包
>其中广播包是每个设备必须广播的，而响应包是可选的。




























