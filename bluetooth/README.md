# 蓝牙开发相关实用方法

## 获取已绑定蓝牙设备
```Java
private void getBonedDevices() {
    Set<BluetoothDevice> pairedDevices = mBluetoothManager.getmBluetoothAdapter().getBondedDevices();
    if (pairedDevices.size() > 0) {
        for (BluetoothDevice device : pairedDevices) {
            Logger.i("已绑定设备："+device.getName() + "\n" + device.getAddress());
        }
    }
}
```
## 获取已连接蓝牙设备
```Java
int a2dp = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);  
int headset = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);  
int health = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEALTH); 

int flag = -1;  
  if (a2dp == BluetoothProfile.STATE_CONNECTED) {  
   flag = a2dp;  
  } else if (headset == BluetoothProfile.STATE_CONNECTED) {  
   flag = headset;  
  } else if (health == BluetoothProfile.STATE_CONNECTED) {  
   flag = health;  
  }  
 
if (flag != -1) {  
    bluetoothAdapter.getProfileProxy(MainActivity.this, new ServiceListener() {  

        @Override  
        public void onServiceDisconnected(int profile) {  
            // TODO Auto-generated method stub  

        }  

        @Override  
        public void onServiceConnected(int profile, BluetoothProfile proxy) {  
            // TODO Auto-generated method stub  
            List<BluetoothDevice> mDevices = proxy.getConnectedDevices();  
            if (mDevices != null && mDevices.size() > 0) {  
                for (BluetoothDevice device : mDevices) {  
                    Log.i("W", "device name: " + device.getName());  
                }  
            } else {  
                Log.i("W", "mDevices is null");  
            }  
        }  
    }, flag);  
} 
```
## 监听手机本身蓝牙状态的广播
### 首先申请蓝牙的相关权限
```Java
<uses-permission android:name="android.permission.BLUETOOTH" />
```
### 接收广播
```Java
if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
            BluetoothAdapter.ERROR);
    switch (state) {
        case BluetoothAdapter.STATE_OFF:
            Log.d("aaa", "STATE_OFF 手机蓝牙关闭");
            break;
        case BluetoothAdapter.STATE_TURNING_OFF:
            Log.d("aaa", "STATE_TURNING_OFF 手机蓝牙正在关闭");
            break;
        case BluetoothAdapter.STATE_ON:
            Log.d("aaa", "STATE_ON 手机蓝牙开启");
            break;
        case BluetoothAdapter.STATE_TURNING_ON:
            Log.d("aaa", "STATE_TURNING_ON 手机蓝牙正在开启");
            break;
    }
}
```
### 监听蓝牙设备配对状态的广播
#### 注册action
```java
BluetoothDevice.ACTION_BOND_STATE_CHANGED
```
#### 监听代码
```Java 
if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    String name = device.getName();
    Log.d("aaa", "device name: " + name);
    int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
    switch (state) {
        case BluetoothDevice.BOND_NONE:
            Log.d("aaa", "BOND_NONE 删除配对");
            break;
        case BluetoothDevice.BOND_BONDING:
            Log.d("aaa", "BOND_BONDING 正在配对");
            break;
        case BluetoothDevice.BOND_BONDED:
            Log.d("aaa", "BOND_BONDED 配对成功");
            break;
    }
}
```
### 监听蓝牙设备连接和连接断开的广播
#### 注册action
```java
BluetoothDevice.ACTION_ACL_CONNECTED   
BluetoothDevice.ACTION_ACL_DISCONNECTED
```
#### 监听代码
```Java
if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    Log.d("aaa", device.getName() + " ACTION_ACL_CONNECTED");
} else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    Log.d("aaa", device.getName() + " ACTION_ACL_DISCONNECTED");
}
```
### 记录当前正在连接的所有蓝牙输入设备
```Java
public List<BluetoothDevice> connectedBluetoothDevices = new ArrayList<BluetoothDevice>();

if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    if (isInputDevice(device)) {
        List<BluetoothDevice> connectedBluetoothDevices = ((MyApplication) getApplication()).connectedBluetoothDevices;
        if (!connectedBluetoothDevices.contains(device)) {
            connectedBluetoothDevices.add(device);
        }
    }
} else if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    if (isInputDevice(device)) {
        List<BluetoothDevice> connectedBluetoothDevices = ((MyApplication) getApplication()).connectedBluetoothDevices;
        connectedBluetoothDevices.remove(device);
    }
}
```

### 判断蓝牙设备是否是输入设备,这里认为PERIPHERAL是输入设备
```java
private boolean isInputDevice(BluetoothDevice device) {
    int deviceMajorClass = device.getBluetoothClass().getMajorDeviceClass();
    if (deviceMajorClass == BluetoothClass.Device.Major.PERIPHERAL) {
        return true;
    }

    return false;
}
```
### 利用反射，拿到已连接蓝牙的mac
```Java
private void checkBluetoothState() {
    Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到TelephonyManager的Class对象
    try {//得到挂断电话的方法
        Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
        //打开权限
        method.setAccessible(true);
        int state = (int) method.invoke(mBluetoothAdapter, (Object[]) null);

        if (state == BluetoothAdapter.STATE_CONNECTED) {
            Logs.d("BluetoothAdapter.STATE_CONNECTED");
            Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
            Logs.d("devices:" + devices.size());

            for (BluetoothDevice device : devices) {
                Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                method.setAccessible(true);
                boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                Logs.d("isConnected:"+isConnected);
                if (isConnected) {
                    Logs.d("connected:" + device.getAddress());
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```














