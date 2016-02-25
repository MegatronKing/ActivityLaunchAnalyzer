# ActivityLaunchAnalyzer

A tiny tool to analyze the launch time of Activity on Android.
___

Why would I need this?
----------------------

Smooth is the user experience.

When using this tool in the apps, we could know how much time was cost in the follow steps:
layout, draw and biz.

And a level toast shown after the page was launched.

Usage
-----

**Add the follow code in Application:**

```java
ActivityLaunchTimeAnalyzer.install(this);
```

**It needs the log permission:**

```java
adb shell pm grant [package-name] android.permission.READ_LOGS
```

Limitations
-----------

It works on Android 4.1+, and it needs the android.permission.READ_LOGS

Examples
-----------

![enter image description here](/screenshot/device-2016-02-25-163808.png)
![enter image description here](/screenshot/device-2016-02-25-163903.png)
![enter image description here](/screenshot/device-2016-02-25-163945.png)
![enter image description here](/screenshot/device-2016-02-25-163958.png)

License
--------

    Copyright (C) 2016, Megatron King

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.