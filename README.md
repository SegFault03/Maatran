

# MUMTRAN-android-app

MUMTRAN is a web-app designed to help health providers and pregnant women, especially in rural communities.

## Install prerequisites

1.  Download the Android IDE: [Android Studio](http://developer.android.com/sdk/installing/studio.html) (you may need to install java by following the prompts)
    1. Optionally: Install the Android SDK: `brew install android-sdk`, Select the SDK that `brew` logged out back in the previous command
1.  Open this project with in Android Studio IDE
1.  The IDE will complain about "Gradle sync failed". Just do what it says.
1.  Once the IDE stops giving good suggestions go to **Tools** -> **Android** -> **SDK Manager** and do what the SDK Manager says.
1.  Once the SDK Manager stops giving good suggestions, use it to install the Google Repository and the Android Support Repository
1.  Your IDE should stop whining now.

## Using the emulator

### Setup

If you are using an Intel CPU, install the accelerator or emulation will be dog slow. If you are using Hyper-V on Windows, see the section below instead because Intel HAXM will refuse to install.

1.  Open the SDK Manager and download the Intel Emulator Accelerator, https://i.imgur.com/9viMhHQ.png https://i.imgur.com/Ds94V4a.png
1.  Go to your SDK_HOME and find the the correct executable that you just downloaded
    - macOS: `/usr/local/var/lib/android-sdk/extras/intel/Hardware_Accelerated_Execution_Manager/IntelHAXM_1.1.1_for_10_9_and_above.dmg`)
    - Windows: `C:\Users\<YOUR_USERNAME>\AppData\Local\Android\Sdk\extras\intel\Hardware_Accelerated_Execution_Manager\`
1.  Open the executable and install your new kernel extension

#### Emulator with Hyper-V installed on Windows 10

If you are using Hyper-V on Windows, follow the following steps derived from this post, https://blogs.msdn.microsoft.com/visualstudio/2018/05/08/hyper-v-android-emulator-support/

1.  Enable features **Hyper-V** and **Windows Hypervisor Platform**(you probably don't have this enabled already) in **Turn Windows features on or off** (requires a computer restart), https://i.imgur.com/99fnVrN.png
1.  Install Java Development Kit (JDK), http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
1.  Install Visual Studio Tools for Xamarin preview (15.8 Preview 1 or higher with Mobile development with .NET (Xamarin) workload), http://aka.ms/hyperv-emulator-dl
1.  In Visual Studio: [Tools -> Options -> Xamarin -> Android Settings](https://docs.microsoft.com/en-us/xamarin/android/troubleshooting/questions/android-sdk-location?tabs=vswin)
    - Android SDK location: `C:\Users\<YOUR_USRNAME>\AppData\Local\Android\Sdk`
1.  Create `C:\Users\<YOUR_USERNAME>\.android\advancedFeatures.ini` with the contents: `WindowsHypervisorPlatform = on`

### Running on a virtual device

2.  Open the AVD Manager (**Tools** -> **Android** -> **AVD Manager**), https://i.imgur.com/881HJHx.png
3.  Create a new Virtual Device. The size/model doesn't matter that much
    - Select a system image that has both a `x86_64` ABI and Google play services
4.  Finish and click play!

### Debugging the WebView

See https://developers.google.com/web/tools/chrome-devtools/remote-debugging/webviews


## FAQ

### `Emulator: emulator: ERROR: x86 emulation currently requires hardware acceleration!`

If you have Hyper-V enabled on Windows 10, the Intel HAXM accelerator will refuse to install. See the section above for using the emulator with Hyper-V installed on Windows 10

Make sure you have the **Windows Hypervisor Platform** feature enabled in **Turn Windows features on or off**

### Ignore quick boot saved state (cold boot, restoring)

See https://medium.com/@jughosta/quick-boot-for-android-emulator-8224f8c4ea01

1.  **Tools** -> **AVD Manager**
1.  Use the dropdown arrow for the given virtual device -> **Cold Boot Now**

![](https://i.imgur.com/Xu9AfD5.png)
