<br/>

<p align="center">
  <a href="https://www.wepin.io/">
      <picture>
        <source media="(prefers-color-scheme: dark)">
        <img alt="wepin logo" src="https://github.com/WepinWallet/wepin-web-sdk-v1/blob/main/assets/wepin_logo_color.png?raw=true" width="250" height="auto">
      </picture>
</a>
</p>

<br>

# wepin-compose-sdk-widget-v1

[![platform - android](https://img.shields.io/badge/platform-Android-3ddc84.svg?logo=android&style=for-the-badge)](https://www.android.com/)
[![platform - ios](https://img.shields.io/badge/platform-iOS-000.svg?logo=apple&style=for-the-badge)](https://developer.apple.com/ios/)

Wepin Widget Library for ComposeMultiplatform. This package is exclusively available for use in Android and iOS environments.

## ⏩ Get App ID and Key
After signing up for [Wepin Workspace](https://workspace.wepin.io/), go to the development tools menu and enter the information for each app platform to receive your App ID and App Key.

## ⏩ Requirements
- Android API version 24 or newer is required.
- iOS 13+
- Swift 5.x

## ⏩ Installation
val commonMain by getting {
api("io.wepin:wepin-compose-sdk-widget-v1:0.0.1")
}

for iOS
add cocoapods plugin in build.gradle.kts
```
plugins {
    kotlin("native.cocoapods")
}

cocoapods {
  summary = "Some description for a Kotlin/Native module"
  homepage = "Link to a Kotlin/Native module homepage"
  ios.deploymentTarget = "13.0"
  version = "0.0.1"

  pod("AppAuth") {
    version = "~> 1.7.5"
  }

  pod("secp256k1") {
    version = "~> 0.1.0"
  }

  pod("JFBCrypt") {
    version = "~> 0.1"
  }
}
```

## ⏩ Setting PodFile
For iOS, You must add Podfile in iosApp Folder for install ios dependencies.
```swift
# Uncomment the next line to define a global platform for your project
platform :ios, '13.0'

target 'iosApp' do
  # Comment the next line if you don't want to use dynamic frameworks
  use_frameworks!

  # Pods for iosApp
  pod 'shared', :path => '../shared'
end

post_install do |installer|
    installer.generated_projects.each do |project|
        project.targets.each do |target|
            target.build_configurations.each do |config|
                config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = '13.0'
            end
        end
    end
end
```

After Sync Project with Gradle Files, Do pod install


## ⏩ Add Permission
Add the below line in your app's `AndroidManifest.xml` file

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />

```

## ⏩ Configure Deep Link
- Android
  Deep Link scheme format : `wepin. + Your Wepin App ID`

  When a custom scheme is used, WepinLogin Library can be easily configured to capture all redirects using this custom scheme through a manifest placeholder:

  Add the below lines in your app's build.gradle.kts file
  ```kotlin
  // For Deep Link => RedirectScheme Format : wepin. + Wepin App ID
  android.defaultConfig.manifestPlaceholders = [
    'appAuthRedirectScheme': 'wepin.{{YOUR_WEPIN_APPID}}'
  ]
  ```
  Add the below line in your app's AndroidManifest.xml file

  ```xml
    android:name="com.wepin.cm.loginlib.RedirectUriReceiverActivity"
    android:exported="true">
    <intent-filter>
       <action android:name="android.intent.action.VIEW" />
  
       <category android:name="android.intent.category.DEFAULT" />
       <category android:name="android.intent.category.BROWSABLE" />
       <data
        android:host="oauth2redirect"
        android:scheme="${appAuthRedirectScheme}" />
    </intent-filter>
  </activity>
  ```
- iOS
  You must add the app's URL scheme to the Info.plist file. This is necessary for redirection back to the app after the authentication process.
  The value of the URL scheme should be `'wepin.' + your Wepin app id`.
```xml
<key>CFBundleURLTypes</key>
<array>
    <dict>
        <key>CFBundleURLSchemes</key>
        <string>Editor</string>
  			<key>CFBundleURLName</key>
  			<string>unique name</string>
        <array>
            <string>wepin + your Wepin app id</string>
        </array>
    </dict>
</array>
```

## ⏩ Import
```kotlin
  import com.wepin.cm.widgetlib.WepinWidgetSDK
```

### ⏩ Initialization
Create instance of WepinSDK in shared code to use wepin and pass your activity(Android) or ViewController(iOS) as a parameter
This method is a suspend method, so you can call it within another suspend method or in a coroutine.

```kotlin
class WidgetManager(context: Any) {
    var loginResult: LoginResult? by mutableStateOf(null)
    val _context = context
    val privateKey = "private key"
    val wepinWidgetSDK = WepinWidgetSDK(
        WepinLoginOptions(
            context = _context,
            appId = "appId",
            appKey = "app key"
        )
    )
}
```

### init

```kotlin
suspend fun init(attributes: WidgetAttributes): Boolean
```

#### parameters
- `attributes` \<WidgetAttributes>
  - `defaultLanguage` \<String> - The language to be displayed on the widget (default: 'ko'). Currently, only 'ko', 'en', and 'ja' are supported.
  - `defaultCurrency` \<String> - The currency to be displayed on the widget (default: 'KRW'). Currently, only 'KRW', 'USD', and 'JPY' are supported.

#### Example
```kotlin
wepinWidgetSDK.init(WidgetAttributes(defaultLanguage = "ko", defaultCurrency = "KRW"))
```

#### Returns
- \<Boolean> - Returns 'true' if Wepin Widget SDK initialization is successful.


### isInitialized
```kotlin
fun isInitalized(): Boolean
```

#### parameters
  - void

#### Example
```kotlin
wepinWidgetSDK.isInitalized()
```

#### Returns
- \<Boolean> - Returns 'true' if Wepin Widget SDK is already initialized, otherwise false


### changeLanguage
```kotlin
fun changeLanguage(options: WidgetAttributes)
```

#### parameters
- `attributes` \<WidgetAttributes>
  - `defaultLanguage` \<String> - The language to be displayed on the widget (default: 'ko'). Currently, only 'ko', 'en', and 'ja' are supported.
  - `defaultCurrency` \<String> - The currency to be displayed on the widget (default: 'KRW'). Currently, only 'KRW', 'USD', and 'JPY' are supported.

#### Example
```kotlin
wepinWidgetSDK.changeLanguage(WidgetAttributes(defaultLanguage = "ko", defaultCurrency = "KRW"))
```

#### Returns
- void


### getStatus (suspend)
```kotlin
suspend fun getStatus(): WepinLifeCycle
```

#### parameters
- void

#### Example
```kotlin
wepinWidgetSDK.getStatus()
```

#### Returns
- \<WepinLifeCycle> - Returns the current lifecycle of the Wepin SDK, which is defined as follows:
  - `NOT_INITIALIZED`:  Wepin is not initialized.
  - `INITIALIZING`: Wepin is in the process of initializing.
  - `INITIALIZED`: Wepin is initialized.
  - `BEFORE_LOGIN`: Wepin is initialized but the user is not logged in.
  - `LOGIN`:The user is logged in.
  - `LOGIN_BEFORE_REGISTER`: The user is logged in but not registered in Wepin.

## ⏩ Method & Variable

Methods and Variables can be used after initialization of Wepin Widget SDK.

### login
The `login` variable is a Wepin login library that includes various authentication methods, allowing users to log in using different approaches. It supports email and password login, OAuth provider login, login using ID tokens or access tokens, and more. For detailed information on each method, please refer to the official library documentation at https://github.com/WepinWallet/wepin-compose-sdk-login-v1/blob/main/README.md

#### Available Methods
- `loginWithOauthProvider`
- `signUpWithEmailAndPassword`
- `loginWithEmailAndPassword`
- `loginWithIdToken`
- `loginWithAccessToken`
- `getRefreshFirebaseToken`
- `loginWepin`
- `getCurrentWepinUser`
- `logout`
- `getSignForLogin`

#### Example
```kotlin
// Login using an OAuth provider
val loginResponse = wepinWidgetSDK.login!!.loginWithOauthProvider(LoginOauth2Params(provider = "google", clientId = "google_client_id"))

// Sign up and log in using email and password
val signUpResult = wepinWidgetSDK.login!!.signUpWithEmailAndPassword(LoginWithEmailParams(email = "email", password = "password"))

// Log in using an ID token
val idTokenResult = wepinWidgetSDK.login!!.loginWithIdToken(LoginOauthIdTokenRequest(idToken = "token", sign = "sign"))

// Log in to Wepin
val wepinLoginResult = wepinWidgetSDK.login!!.loginWepin(idTokenResult)

// Get the currently logged-in user
val currentUser = wepnWidgetSDK.login!!.getCurrentWepinUser()

//Logout
wepinWidgetSDK.login!!.logout
```

For more details on each method and to see usage examples, please visit the official https://github.com/WepinWallet/wepin-compose-sdk-login-v1/blob/main/README.md


### openWidget
```kotlin
suspend fun openWidget()
```
#### Parameters
- void

#### Example
```kotlin
wepinWidgetSDK.openWidget()
```

#### Returns
- void


### closeWidget
```kotlin
fun closeWidget()
```
#### Parameters

- void
- 
#### Example
```kotlin
wepinWidgetSDK.closeWidget()
```

#### Returns
- void

### register
```kotlin
suspend fun register(): WepinUser?
```

The `register` method registers the user with Wepin. After joining and logging in, this method opens the Register page of the Wepin widget, allowing the user to complete registration (wipe and account creation) for the Wepin service.

This method is only available if the lifecycle of the WepinSDK is `WepinLifeCycle.loginBeforeRegister`. After calling the `loginWepin()` method in the `login` variable, if the `loginStatus` value in the userStatus is not 'complete', this method must be called.

#### Parameters
- void

#### Example
```kotlin
wepinWidgetSDK.register()
```

#### Returns
- \<WepinUser>
  - status \<'success'|'fail'>  - The login status.
  - userInfo \<UserInfo> __optional__ - The user's information, including:
    - userId \<String> - The user's ID.
    - email \<String> - The user's email.
    - provider \<'google'|'apple'|'naver'|'discord'|'email'|'external_token'> - The login provider.
    - use2FA \<Boolean> - Whether the user uses two-factor authentication.
  - walletId \<String> = The user's wallet ID.
  - userStatus: \<UserStatus> - The user's status of wepin login. including:
    - loginStats: \<'complete' | 'pinRequired' | 'registerRequired'> - If the user's loginStatus value is not complete, it must be registered in the wepin.
    - pinRequired?: \<Boolean>
  - token: \<Token> - The user's token of wepin.
    - accessToken: \<String>
    - refreshToken \<String>


### getAccounts
```kotlin
suspend fun getAccounts(
        networks: List<String>? = null,
        withEoa: Boolean? = null
): ArrayList<Account>?
```

#### Parameters
- networks: \<List\<String>> __optional__ A list of network names to filter the accounts.
- withEoa: \<bool> __optional__ Whether to include EOA accounts if AA accounts are included.

#### Example
```kotlin
//Search entire account
wepinWidgetSDK.getAccounts()

//Search Ethereum accounts only
wepinWidgetSDK.getAccounts(networks = listOf("ETHEREUM"), withEoa = true)
```

#### Returns
\<List\<Account>> -list of the user's accounts.
- address \<String>
- network \<String>
- contract \<String> __optional__ The token contract address.
- isAA \<bool> __optional__  Whether it is an AA account or not.

### getBalance

```kotlin
suspend fun getBalance(accounts: ArrayList<Account>): ArrayList<AccountBalanceInfo>?
```

#### Parameters
- accounts \<List\<Account>> __optional__ - A list of accounts for which to retrieve balance information.
  - network \<String> - The network associated with the account.
  - address \<String> - The address of the account.
  - isAA \<bool> __optional__ - Indicates whether the account is an AA (Account Abstraction) account.

#### Example
```kotlin
wepinWidgetSDK.getBalance(accountsList)
```

#### Returns
\<List\<AccountBalanceInfo>> - list of balance information for the specified accounts.
- network \<String> - The network associated with the account.
- address \<String> - The address of the account.
- symbol \<String> - The symbol of the account's balance.
- balance \<String> - The balance of the account.
- tokens \<List\<TokenBalanceInfo>> - A list of token balance information for the account.
  - symbol \<String> - The symbol of the token.
  - balance \<String> - The balance of the token.
  - contract \<String> - The contract address of the token.


### getNFTs
```kotlin
suspend fun getNFTs(refresh: Boolean, networks: List<String>? = null): ArrayList<WepinNFT> 
```

#### Parameters
- refresh \<bool> - A required parameter to indicate whether to refresh the NFT data.
- networks \<List\<String>> __optional__ - A list of network names to filter the NFTs.

#### Example
```kotlin
nftList = wepinWidgetSDK.getNFTs(refresh = true)

nftList = wepinWidgetSDK.getNFTs(refresh = false, networks = listOf("ETHEREUM"))
```

#### Returns
\<List\<WepinNFT>> -list of the user's NFTs.
- account \<Account>
  - address \<String> - The address of the account associated with the NFT.
  - network \<String> - The network associated with the NFT.
  - contract \<String> __optional__ The token contract address.
  - isAA \<bool> __optional__ Indicates whether the account is an AA (Account Abstraction) account.
- contract \<WepinNFTContract>
  - name \<String> - The name of the NFT contract.
  - address \<String> - The contract address of the NFT.
  - scheme \<String> - The scheme of the NFT.
  - description \<String> __optional__ - A description of the NFT contract.
  - network \<String> - The network associated with the NFT contract.
  - externalLink \<String> __optional__  - An external link associated with the NFT contract.
  - imageUrl \<String> __optional__ - An image URL associated with the NFT contract.
- name \<String> - The name of the NFT.
- description \<String> - A description of the NFT.
- externalLink \<String> - An external link associated with the NFT.
- imageUrl \<String> - An image URL associated with the NFT.
- contentUrl \<String> __optional__ - A URL pointing to the content associated with the NFT.
- quantity \<int> - The quantity of the NFT.
- contentType \<String> - The content type of the NFT.
- state \<int> - The state of the NFT.

### send
```kotlin
suspend fun send(sendData: SendData): SendResponse
```

#### Parameters
- sendData \<SendData>
  - account \ <Account>
  - txData \ <TxData>

#### Example
```kotlin
wepinWidgetSDK.send(SendData(account!!,txData = TxData(amount = "0.00001",toAddress = "0x422aB3e817c212C98B1E6D7686ec7b017a2fdb42")))
```

#### Returns
- \<SendResponse> - response containing the transaction ID.
  - txId \<String> - The ID of the sent transaction.


### finalize
```kotlin
fun finalize()
```
The `finalize()` method finalizes the Wepin SDK, releasing any resources or connections it has established.

#### Parameters
- void

#### Example
```kotlin
wepinWidgetSDK.finalize()
```

#### Returns
- void

### WepinError

| Error Code                      | Error Message                     | Error Description                                                                                   |
|---------------------------------|-----------------------------------|-----------------------------------------------------------------------------------------------------|
| `INVALID_APP_KEY`               | "Invalid app key"                 | The Wepin app key is invalid.                                                                       |
| `INVALID_PARAMETER` `           | "Invalid parameter"               | One or more parameters provided are invalid or missing.                                             |
| `INVALID_LOGIN_PROVIDER`        | "Invalid login provider"          | The login provider specified is not supported or is invalid.                                        |
| `INVALID_TOKEN`                 | "Token does not exist"            | The token does not exist.                                                                           |
| `INVALID_LOGIN_SESSION`         | "Invalid Login Session"           | The login session information does not exist.                                                                           |
| `NOT_INITIALIZED_ERROR`         | "Not initialized error"           | The WepinLoginLibrary has not been properly initialized.                                            |
| `ALREADY_INITIALIZED_ERROR`     | "Already initialized"             | The WepinLoginLibrary is already initialized, so the logout operation cannot be performed again.    |
| `NOT_ACTIVITY`                  | "Context is not activity"         | The Context is not an activity                                                                      |
| `USER_CANCELLED`                | "User cancelled"                  | The user has cancelled the operation.                                                               |
| `UNKNOWN_ERROR`                 | "An unknown error occurred"       | An unknown error has occurred, and the cause is not identified.                                     |
| `NOT_CONNECTED_INTERNET`        | "No internet connection"          | The system is unable to detect an active internet connection.                                       |
| `FAILED_LOGIN`                  | "Failed to Oauth log in"          | The login attempt has failed due to incorrect credentials or other issues.                          |
| `ALREADY_LOGOUT`                | "Already Logout"                  | The user is already logged out, so the logout operation cannot be performed again.                  |
| `INVALID_EMAIL_DOMAIN`          | "Invalid email domain"            | The provided email address's domain is not allowed or recognized by the system.                     |
| `FAILED_SEND_EMAIL`             | "Failed to send email"            | The system encountered an error while sending an email. This is because the email address is invalid or we sent verification emails too often. Please change your email or try again after 1 minute.                 |
| `REQUIRED_EMAIL_VERIFIED`       | "Email verification required"     | Email verification is required to proceed with the requested operation.                             |
| `INCORRECT_EMAIL_FORM`          | "Incorrect email format"          | The provided email address does not match the expected format.                                      |
| `INCORRECT_PASSWORD_FORM`       | "Incorrect password format"       | The provided password does not meet the required format or criteria.                                |
| `NOT_INITIALIZED_NETWORK`       | "Network Manager not initialized" | The network or connection required for the operation has not been properly initialized.             |
| `REQUIRED_SIGNUP_EMAIL`         | "Email sign-up required."         | The user needs to sign up with an email address to proceed.                                         |
| `FAILED_EMAIL_VERIFIED`         | "Failed to verify email."         | The WepinLoginLibrary encountered an issue while attempting to verify the provided email address.   |
| `FAILED_PASSWORD_SETTING`       | "Failed to set password."         | The WepinLoginLibrary failed to set the password.                                                   |
| `EXISTED_EMAIL`                 | "Email already exists."           | The provided email address is already registered in Wepin.                                          |
| `INCORRECT_LIFECYCLE_EXCEPTION` | "Incorrect Lifecycle Exception"   | The lifecycle of the Wepin SDK is incorrect for the requested operation. Ensure that the SDK is in the correct state (e.g., `initialized` and `login`) before proceeding.                                        |
| `ACCOUNT_NOT_FOUND`             | "Account Not Found"               | The specified account was not found. This error is returned when attempting to access an account that does not exist in the Wepin. |
| `FAILED_REGISTER`               | "Failed Register"                 | Failed to register the user. This can occur due to issues with the provided registration details or internal errors during the registration process.                                            |
| `FAILED_SEND`                   | "Failed Send"                     | Failed to send the required data or request. This error could be due to network issues, incorrect data, or internal server errors.    |
| `NFT_NOT_FOUND`                 | "NFT not Found"                   | The specified NFT was not found. This error occurs when the requested NFT does not exist or is not accessible within the user's account. |