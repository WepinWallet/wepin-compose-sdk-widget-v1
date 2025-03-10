import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wepin.cm.loginlib.types.FBToken
import com.wepin.cm.loginlib.types.LoginOauth2Params
import com.wepin.cm.loginlib.types.LoginResult
import com.wepin.cm.loginlib.types.LoginWithEmailParams
import com.wepin.cm.loginlib.types.OauthTokenType
import com.wepin.cm.loginlib.types.Providers
import com.wepin.cm.loginlib.types.WepinLoginOptions
import com.wepin.cm.loginlib.types.network.LoginOauthAccessTokenRequest
import com.wepin.cm.loginlib.types.network.LoginOauthIdTokenRequest
import com.wepin.cm.widgetlib.WepinWidgetSDK
import com.wepin.cm.widgetlib.types.Account
import com.wepin.cm.widgetlib.types.AccountBalanceInfo
import com.wepin.cm.widgetlib.types.LoginProvider
import com.wepin.cm.widgetlib.types.LoginWithUIParameter
import com.wepin.cm.widgetlib.types.SendData
import com.wepin.cm.widgetlib.types.TxData
import com.wepin.cm.widgetlib.types.WepinLifeCycle
import com.wepin.cm.widgetlib.types.WepinNFT
import com.wepin.cm.widgetlib.types.WidgetAttributes
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import utils.Platform
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class WidgetManager(context: Any) {
    var loginResult: LoginResult? by mutableStateOf(null)
    val _context = context
    val privateKey = "private_key"
    //    val privateKey = "185e092fbe9ca2b713519936c359f51fc48cc487f5c134fc7e067f7410d085a7"
    val appKey = if (Platform.isIOS()) {
        "your_ios_app_key"
    } else {
        "your_android_app_key"
    }

    val wepinWidgetSDK = WepinWidgetSDK(
        WepinLoginOptions(
            context = _context,
            appId = "your_app_id",
            appKey = appKey
        )
    )

    private suspend fun signupWithEmail(
        email: String,
        password: String,
        setResponse: (LoginResult?) -> Unit,
        setText: (String) -> Unit,
    ) {
        val loginOption = LoginWithEmailParams(email, password)
        try {
            val response = wepinWidgetSDK.login!!.signUpWithEmailAndPassword(loginOption)
            setResponse(response)
            setText("$response")
        } catch (e: Exception) {
            setText("fail - $e")
        }

    }

    private suspend fun loginWithEmail(
        email: String,
        password: String,
        setResponse: (LoginResult?) -> Unit,
        setText: (String) -> Unit,
    ) {
        val loginOption = LoginWithEmailParams(email, password)
        try {
            val response = wepinWidgetSDK.login!!.loginWithEmailAndPassword(loginOption)
            val result = wepinWidgetSDK.login!!.loginWepin(response)
            setResponse(response)
            setText("$result")
        } catch (e: Exception) {
            setText("fail - $e")
        }

    }

    private suspend fun loginOauth(
        provider: String,
        clientId: String,
        tokenType: OauthTokenType,
        setResponse: (LoginResult?) -> Unit,
        setText: (String) -> Unit,
    ) {
        val loginOption = LoginOauth2Params(provider, clientId)
        try {
            val loginResponse = wepinWidgetSDK.login!!.loginWithOauthProvider(loginOption)
            when (tokenType) {
                OauthTokenType.ID_TOKEN -> {
                    loginIdToken(
                        loginResponse.token,
                        setResponse,
                        setText,
                    )
                }

                OauthTokenType.ACCESS_TOKEN -> {
                    loginAccessToken(
                        loginResponse.provider,
                        loginResponse.token,
                        setResponse,
                        setText,
                    )
                }

                else -> {
                    setResponse(loginResponse as LoginResult)
                }
            }
        } catch (e: Exception) {
            setResponse(null)
            setText("fail - $e")
            println("fail - $e")
        }

    }

    private suspend fun loginIdToken(
        token: String,
        setResponse: (LoginResult?) -> Unit,
        setText: (String) -> Unit,
    ) {
        try {
            val loginOption = LoginOauthIdTokenRequest(idToken = token)
            val loginResponse = wepinWidgetSDK.login!!.loginWithIdToken(loginOption)
            val response = wepinWidgetSDK.login!!.loginWepin(loginResponse)
            setResponse(loginResponse)
            setText("$response")
        } catch (e: Exception) {
            setResponse(null)
            setText("fail - ${e.message}")
        }
    }

    private suspend fun loginAccessToken(
        provider: String,
        token: String,
        setResponse: (LoginResult?) -> Unit,
        setText: (String) -> Unit,
    ) {
        try {
            println("loginAccessToken")
            val loginOption = LoginOauthAccessTokenRequest(provider, token)
            val loginResponse = wepinWidgetSDK.login!!.loginWithAccessToken(loginOption)
            val response = wepinWidgetSDK.login!!.loginWepin(loginResponse)
            setResponse(loginResponse)
            setText("$response")
        } catch (e: Exception) {
            setResponse(null)
            setText("fail - ${e.message}")
        }
    }

    private fun loginWepin(coroutineScope: CoroutineScope, setText: (String) -> Unit) {
        coroutineScope.launch {
            try {
                val response = wepinWidgetSDK.login!!.loginWepin(loginResult!!)
                setText("$response")
            } catch (e: Exception) {
                setText("fail - ${e.message}")
            }
        }
    }

    private fun logoutWepin(coroutineScope: CoroutineScope, setText: (String) -> Unit) {
    }

    fun send(
        coroutineScope: CoroutineScope,
        data: SendData,
        setText: (String) -> Unit
    ) {
        coroutineScope.launch {
            try {
                val txId = wepinWidgetSDK.send(data)
                setText("txId: $txId")
            } catch (e: Exception) {
                setText("error: $e")
            }
        }
    }

    fun receive(
        coroutineScope: CoroutineScope,
        account: Account,
        setText: (String) -> Unit
    ) {
        coroutineScope.launch {
            try {
                val result = wepinWidgetSDK.receive(account)
                setText("result: $result")
            } catch (e: Exception) {
                setText("error: $e")
            }
        }
    }

    fun viewAccountDetail(
        coroutineScope: CoroutineScope,
        account: Account,
        setText: (String) -> Unit
    ) {
        coroutineScope.launch {
            try {
                val result = wepinWidgetSDK.viewAccountDetail(account)
                setText("result: $result")
            } catch (e: Exception) {
                setText("error: $e")
            }
        }
    }

    @Composable
    fun handleItemClick(
        item: String,
        accountsList: List<Account>,
        selectedAccounts: List<Account>,
        coroutineScope: CoroutineScope,
        setResponse: (LoginResult?) -> Unit,
        setItem: (String) -> Unit,
        setText: (String) -> Unit,
        setLoading: (Boolean) -> Unit,
        setAccountList: (ArrayList<Account>) -> Unit,
        setBalanceList: (ArrayList<AccountBalanceInfo>) -> Unit,
        setNFTList: (ArrayList<WepinNFT>) -> Unit,
        openDialog: (String) -> Unit,
        setUserStatus: (WepinLifeCycle) -> Unit
    ) {
        setItem(item)
        when (item) {
            "Init" -> {
                val attributes =
                    WidgetAttributes(defaultLanguage = "ko", defaultCurrency = "KRW")
                coroutineScope.launch {
                    try {
                        if (wepinWidgetSDK.isInitalized()) {
                            setText("It's already initalized")
                        } else {
                            setLoading(true)

                            val response = wepinWidgetSDK.init(attributes)
                            val wepinStatus = wepinWidgetSDK.getStatus()
                            setUserStatus(wepinStatus)
                            setText("$response")
                            setLoading(false)
                        }
                    } catch(e: Exception) {
                        setText("error: $e")
                    }
                }
            }
            "Login with UI" -> {
                coroutineScope.launch {
                    try {
                        val options = LoginWithUIParameter(
                            email = "email@address",
                            loginProviders = arrayOf(
                                LoginProvider(provider = "google", clientId = "google_client_id"),
                                LoginProvider(provider = "naver", clientId = "naver_client_id"),
                                LoginProvider(provider = "discord", clientId = "discord_client_id")
                            )
                        )
                        val response = wepinWidgetSDK.loginWithUI(options)

                        val wepinStatus = wepinWidgetSDK.getStatus()
                        setUserStatus(wepinStatus)
                        setText("response: $response")

                    } catch(e: Exception) {
                        setText("error: $e")
                    }
                }
            }

            "Login with Google" -> {
                val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                    println("Unhandled Exception: $throwable")
                }
                coroutineScope.launch {
                    try {
                        setLoading(true)
                        loginOauth(
                            provider = "google",
                            clientId = "google_client_id",
                            tokenType = OauthTokenType.ID_TOKEN,
                            setResponse = setResponse,
                            setText = setText,
                        )

                        val wepinStatus = wepinWidgetSDK.getStatus()
                        setUserStatus(wepinStatus)
                        setLoading(false)
                    } catch (e: Exception) {
                        println("e: $e")
                    }
                }

            }

            "Login with Apple" -> {
                coroutineScope.launch {
                    setLoading(true)
                    loginOauth(
                        provider = "apple",
                        clientId = "apple_client_id",
                        tokenType = OauthTokenType.ID_TOKEN,
                        setResponse = setResponse,
                        setText = setText,
                    )

                    val wepinStatus = wepinWidgetSDK.getStatus()
                    setUserStatus(wepinStatus)
                    setLoading(false)
                }
            }

            "Login with Discord" -> {
                coroutineScope.launch {
                    setLoading(true)
                    loginOauth(
                        provider = "discord",
                        clientId = "discord_client_id",
                        tokenType = OauthTokenType.ACCESS_TOKEN,
                        setResponse = setResponse,
                        setText = setText,
                    )

                    val wepinStatus = wepinWidgetSDK.getStatus()
                    setUserStatus(wepinStatus)
                    setLoading(false)
                }
            }

            "Login with Naver" -> {
                coroutineScope.launch {
                    setLoading(true)
                    loginOauth(
                        provider = "naver",
                        clientId = "naver_client_id",
                        tokenType = OauthTokenType.ACCESS_TOKEN,
                        setResponse = setResponse,
                        setText = setText,
                    )
                    val wepinStatus = wepinWidgetSDK.getStatus()
                    setUserStatus(wepinStatus)
                    setLoading(false)
                }
            }

            "SignUp with Email" -> {
                coroutineScope.launch {
                    setLoading(true)
                    signupWithEmail(
                        email = "email",
                        password = "password",
                        setResponse = setResponse,
                        setText = setText,
                    )

                    val wepinStatus = wepinWidgetSDK.getStatus()
                    setUserStatus(wepinStatus)
                    setLoading(false)
                }
            }

            "Login with Email" -> {
                coroutineScope.launch {
                    setLoading(true)
                    loginWithEmail(
//                        email = "js.hong@iotrust.kr",
//                        password = "doublebk13!@@",
                        email = "email",
                        password = "password",
                        setResponse = setResponse,
                        setText = setText,
                    )

                    val wepinStatus = wepinWidgetSDK.getStatus()
                    setUserStatus(wepinStatus)
                    setLoading(false)
                }
            }

            "Open Widget" -> {
                coroutineScope.launch {
                    wepinWidgetSDK.openWidget()
                }
            }

            "Pin Auth" -> {
                coroutineScope.launch {
                    try {
                        val result = wepinWidgetSDK.verifyPin()
                        setText("result: $result")
                    } catch(e: Exception) {
                        setText("error: ${e.toString()}")
                    }
                }
            }

            "Change Language" -> {
                openDialog("Attribute")
            }

            "Get Account" -> {
                coroutineScope.launch {
                    try {
                        setLoading(true)
                        val accountsList = wepinWidgetSDK.getAccounts()!!
                        val wepinStatus = wepinWidgetSDK.getStatus()
                        setUserStatus(wepinStatus)
                        setAccountList(accountsList)
                        setText("Success")
                    } catch (e: Exception) {
                        setText("fail - $e")
                    } finally {
                        setLoading(false)
                    }
                }
            }

            "Get NFTs" -> {
                coroutineScope.launch {
                    try {
                        setLoading(true)
                        val nftList = wepinWidgetSDK.getNFTs(refresh = false)
                        val wepinStatus = wepinWidgetSDK.getStatus()
                        setUserStatus(wepinStatus)
                        setNFTList(nftList)
                        setText("Success")
                        openDialog("NFT")
                    } catch (e: Exception) {
                        setText("fail - $e")
                    } finally {
                        setLoading(false)
                    }
                }
            }

            "Get NFTs(with refresh)" -> {
                coroutineScope.launch {
                    try {
                        setLoading(true)
                        val nftList = wepinWidgetSDK.getNFTs(refresh = true)
                        val wepinStatus = wepinWidgetSDK.getStatus()
                        setUserStatus(wepinStatus)
                        setNFTList(nftList)
                        setText("Success")
                        openDialog("NFT")
                    } catch (e: Exception) {
                        setText("fail - $e")
                    } finally {
                        setLoading(false)
                    }
                }
            }

            "Account List View" -> {
                openDialog("Account")
            }

            "Get Balance" -> {
                val balanceAccount = if (selectedAccounts.isEmpty()) {
                    accountsList
                } else {
                    selectedAccounts
                }
                coroutineScope.launch {
                    try {
                        setLoading(true)
                        val balanceList = wepinWidgetSDK.getBalance(ArrayList(balanceAccount))
                        setText("Success")
                        setBalanceList(balanceList!!)
                        openDialog("Balance")
                    } catch (e: Exception) {
                        setText("fail - $e")
                    } finally {
                        setLoading(false)
                    }
                }
            }

            "Send" -> {
                coroutineScope.launch {
                    openDialog("Send")
                }
            }

            "View Account Detail" -> {
                coroutineScope.launch {
                    openDialog("View Account Detail")
                }
            }

            "Logout" -> {
                coroutineScope.launch {
                    val response = wepinWidgetSDK.login!!.logoutWepin()
                    setText("respons: $response")
                    setUserStatus(WepinLifeCycle.INITIALIZED)
                    setAccountList(arrayListOf())
                    setNFTList(arrayListOf())
                    setBalanceList(arrayListOf())
                }
            }

            "Register" -> {
                coroutineScope.launch {
                    try {
                        val response = wepinWidgetSDK.register()
                        val wepinStatus = wepinWidgetSDK.getStatus()
                        setUserStatus(wepinStatus)
                        setText("$response")
                    } catch (e: Exception) {
                        setText("error: $e")
                    }
                }
            }

            "Receive" -> {
                coroutineScope.launch {
                    println("receive:sss")
                    openDialog("Receive")
                }
            }

            "Finalize" -> {
                wepinWidgetSDK.finalize()
                setUserStatus(WepinLifeCycle.NOT_INITIALIZED)
                setAccountList(arrayListOf())
                setNFTList(arrayListOf())
                setBalanceList(arrayListOf())
            }
        }
    }

    fun changeLanguage(attributes: WidgetAttributes, setText: (String) -> Unit) {
        try {
            wepinWidgetSDK.changeLanguage(options = attributes)
            setText("Success")
        } catch (e: Exception) {
            setText("fail - $e")
        }
    }
}

@Composable
fun App(context: Any) {
    var showDialog by remember { mutableStateOf(false) }
    var dialogType by remember { mutableStateOf("") }
    var accountList by remember { mutableStateOf(listOf<Account>()) }
    var selectedAccounts by remember { mutableStateOf(listOf<Account>()) }
    var balanceList by remember { mutableStateOf(listOf<AccountBalanceInfo>()) }
    var nftList by remember { mutableStateOf(listOf<WepinNFT>()) }
    val widgetManager = remember { WidgetManager(context) }
    var item by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("Your long text goes here...") }
    var userStatus by remember { mutableStateOf(WepinLifeCycle.NOT_INITIALIZED) }
    val coroutineScope = rememberCoroutineScope()
    var showResultDialog by remember { mutableStateOf(false) } // ResultAlert 표시 여부
    var resultText by remember { mutableStateOf("") } // ResultAlert의 텍스트
    var isLoading by remember { mutableStateOf(false) }
    MaterialTheme {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Header()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .shadow(
                        elevation = 6.dp, // CustomButton과 비슷한 그림자 높이
                        shape = RoundedCornerShape(12.dp), // CustomButton의 모양과 일치
                        clip = false
                    )
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(12.dp)
                    ) // CustomButton의 배경색과 동일
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE0E0E0), // CustomButton과 유사한 테두리 색상
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp), // 내부 패딩 설정
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically, // 수직 정렬을 중앙으로
                    horizontalArrangement = Arrangement.SpaceBetween, // 요소들 사이의 간격을 최대로
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 왼쪽 텍스트
                    Text("Wepin Status: $userStatus", color = Color.Black)

                    // 오른쪽에 로딩 아이콘
                    LoadingArrowIndicator(isLoading = isLoading)
                }
            }
            ScrollableContent(
                testItem = getMenuList(),
                onItemClicked = { selectedItem ->
                    widgetManager.handleItemClick(
                        item = selectedItem,
                        accountsList = accountList,
                        selectedAccounts = selectedAccounts,
                        coroutineScope = coroutineScope,
                        setResponse = { widgetManager.loginResult },
                        setItem = { item = it },
                        setText = {
                            text = it
                            resultText = it // ResultAlert에 표시할 텍스트
                            showResultDialog = true // ResultAlert 표시
                        },
                        setLoading = { isLoading = it },
                        setAccountList = {
                            accountList = it
                            if (it.isEmpty()) {
                                selectedAccounts = it
                            }
                        },
                        setBalanceList = { balanceList = it },
                        setNFTList = { nftList = it },
                        openDialog = {
                            showDialog = true
                            dialogType = it
                        },
                        setUserStatus = { userStatus = it }
                    )
                },
                accountExist = accountList.isNotEmpty(),
                userState = userStatus
            )
//            ResultBox(item = item, text = text)
        }

        // ResultAlert 다이얼로그 표시
        if (showResultDialog) {
            ResultAlert(
                text = resultText,
                onConfirm = { showResultDialog = false } // 다이얼로그 닫기
            )
        }
        if (showDialog) {
            when (dialogType) {
                "Account" -> FullScreenDialog(
                    items = accountList,
                    itemToString = { it.toString() },
                    onConfirm = { selectedAccounts = it },
                    onDismiss = { showDialog = false })

                "Balance" -> FullScreenDialog(
                    items = balanceList,
                    itemToString = { it.toString() },
                    onConfirm = null,
                    onDismiss = { showDialog = false }
                )

                "NFT" -> FullScreenDialog(
                    items = nftList,
                    itemToString = { it.toString() },
                    onConfirm = null,
                    onDismiss = { showDialog = false }
                )

                "Send" -> DropdownDialog(
                    title = "Send",
                    items = accountList.map { account ->
                        if (account.contract != null) {
                            "${account.network} - ${account.contract}"  // contract가 null이 아니면 contract 값을 추가
                        } else {
                            account.network  // contract가 null이면 network만 사용
                        }
                    },
                    onConfirm = { accountNetwork, to, amount ->
                        val accountInfo = accountNetwork.split(" ")
                        var sendAccount: Account? = null
                        if (accountInfo.size == 1) {
                            sendAccount =
                                accountList.find { account -> account.network == accountInfo[0] && account.contract == null }
                        } else {
                            sendAccount =
                                accountList.find { account -> account.network == accountInfo[0] && account.contract == accountInfo[2] }
                        }
                        widgetManager.send(
                            coroutineScope = coroutineScope,
                            data = SendData(
                                account = sendAccount!!,
                                txData = TxData(toAddress = to, amount = amount)
                            ),
                            setText = { text = it
                                resultText = it // ResultAlert에 표시할 텍스트
                                showResultDialog = true // ResultAlert 표시
                            }
                        )
                    },
                    onDismiss = { showDialog = false })
                "Receive" -> DropdownDialog(
                    title = "Receive",
                    items = accountList.map { account ->
                        if (account.contract != null) {
                            "${account.network} - ${account.contract}"  // contract가 null이 아니면 contract 값을 추가
                        } else {
                            account.network  // contract가 null이면 network만 사용
                        }
                    },
                    onConfirm = { accountNetwork, to, amount ->
                        val accountInfo = accountNetwork.split(" ")
                        var receiveAccount: Account? = null
                        if (accountInfo.size == 1) {
                            receiveAccount =
                                accountList.find { account -> account.network == accountInfo[0] && account.contract == null }
                        } else {
                            receiveAccount =
                                accountList.find { account -> account.network == accountInfo[0] && account.contract == accountInfo[2] }
                        }
                        widgetManager.receive(
                            coroutineScope = coroutineScope,
                            account = receiveAccount!!,
                            setText = { text = it
                                resultText = it // ResultAlert에 표시할 텍스트
                                showResultDialog = true // ResultAlert 표시
                            }
                        )
                    },
                    onDismiss = { showDialog = false })
                "View Account Detail" -> DropdownDialog(
                    title = "View Account Detail",
                    items = accountList.map { account ->
                        if (account.contract != null) {
                            "${account.network} - ${account.contract}"  // contract가 null이 아니면 contract 값을 추가
                        } else {
                            account.network  // contract가 null이면 network만 사용
                        }
                    },
                    onConfirm = { accountNetwork, to, amount ->
                        val accountInfo = accountNetwork.split(" ")
                        var receiveAccount: Account? = null
                        if (accountInfo.size == 1) {
                            receiveAccount =
                                accountList.find { account -> account.network == accountInfo[0] && account.contract == null }
                        } else {
                            receiveAccount =
                                accountList.find { account -> account.network == accountInfo[0] && account.contract == accountInfo[2] }
                        }
                        widgetManager.viewAccountDetail(
                            coroutineScope = coroutineScope,
                            account = receiveAccount!!,
                            setText = { text = it
                                resultText = it // ResultAlert에 표시할 텍스트
                                showResultDialog = true // ResultAlert 표시
                            }
                        )
                    },
                    onDismiss = { showDialog = false })

                "Attribute" -> AttributeDialog(onConfirm = { language, currency ->
                    widgetManager.changeLanguage(
                        attributes = WidgetAttributes(
                            defaultCurrency = currency,
                            defaultLanguage = language
                        ),
                        setText = { text = it
                            resultText = it // ResultAlert에 표시할 텍스트
                            showResultDialog = true // ResultAlert 표시
                        }
                    )
                }, onDismiss = { showDialog = false })
            }
        }
    }
}

@Composable
fun LoadingArrowIndicator(isLoading: Boolean) {
    // 애니메이션 회전 각도
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing), // 1초 동안 360도 회전
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(
        modifier = Modifier.size(24.dp).graphicsLayer {
            // 로딩 중일 때만 회전
            rotationZ = if (isLoading) rotation else 0f
        }
    ) {
        // 화살표 모양 그리기
        val strokeWidth = 4.dp.toPx()
        val radius = (size.minDimension / 2) - strokeWidth / 2
        val center = Offset(size.width / 2, size.height / 2)

        // 원형 경로 그리기 (270도만)
        drawArc(
            color = Color.Gray,
            startAngle = -90f,
            sweepAngle = 270f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth)
        )

        // 화살표 머리 부분 그리기 (삼각형)
        val arrowSize = 6.dp.toPx() // 화살표 크기
        val arrowAngleDegrees = -90f + 285f // 화살표의 각도 (원호의 끝 부분)
        val arrowAngleRadians = arrowAngleDegrees * (PI / 180f).toFloat()

        // 화살표 위치 계산 (원호 끝)
        val arrowX = (center.x + (radius) * cos(arrowAngleRadians))
        val arrowY = (center.y + (radius) * sin(arrowAngleRadians))

        val trianglePath = Path().apply {
            moveTo(arrowX, arrowY) // 화살표 꼭짓점 (원호의 끝)
            lineTo(arrowX - arrowSize, arrowY + arrowSize) // 왼쪽 아래
            lineTo(arrowX + arrowSize, arrowY + arrowSize) // 오른쪽 아래
            close()
        }

        drawPath(trianglePath, color = Color.Gray)
    }
}

@Composable
fun CustomButton(
    text: String,
    onClick: @Composable () -> Unit,
) {
    var clicked by remember { mutableStateOf(false) }
    Button(
        onClick = { clicked = true },
        colors =
        ButtonDefaults.outlinedButtonColors(
            backgroundColor = Color.White,
            contentColor = Color.Black,
        ),
        border = null,
        modifier = Modifier.fillMaxWidth()
            .padding(bottom = 10.dp, start = 12.dp, end = 12.dp)
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false,
            ) // 그림자와 모서리 둥글게
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = Color(0xFFE0E0E0),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.elevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text)
        }
    }

    if (clicked) {
        clicked = false
        onClick()
    }
}

@Composable
fun Header() {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Text(
            text = "Sample Widget Library",
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
fun ScrollableContent(
    testItem: Array<String>,
    onItemClicked: @Composable (String) -> Unit,
    accountExist: Boolean,
    userState: WepinLifeCycle
) {
    Column(
        modifier =
        Modifier
            .fillMaxWidth()
//            .height(400.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        testItem.forEach { menuItem ->
            var show: Boolean = true
            when (menuItem) {
                "Login with Google",
                "Login with Apple",
                "Login with Discord",
                "Login with Naver",
                "SignUp with Email",
                "Login with Email",
                "Login with UI"-> {
                    if (userState != WepinLifeCycle.INITIALIZED) {
                        show = false
                    }
                }

                "Register" -> {
                    if (userState !== WepinLifeCycle.LOGIN_BEFORE_REGISTER) {
                        show = false
                    }
                }

                "Open Widget",
                "Pin Auth",
                "Get Account" -> {
                    if (userState !== WepinLifeCycle.LOGIN) {
                        show = false
                    }
                }

                "Get NFTs",
                "Get NFTs(with refresh)",
                "Account List View",
                "Get Balance",
                "Send",
                "Receive",
                "View Account Detail" -> {
                    if (userState !== WepinLifeCycle.LOGIN || !accountExist) {
                        show = false
                    }
                }

                "Logout" -> {
                    if (userState == WepinLifeCycle.INITIALIZED || userState == WepinLifeCycle.NOT_INITIALIZED || userState == WepinLifeCycle.INITIALIZING) {
                        show = false
                    }
                }

                "Change Language",
                "Finalize" -> {
                    if (userState == WepinLifeCycle.NOT_INITIALIZED) {
                        show = false
                    }
                }
            }

            if (show) {
                CustomButton(onClick = { onItemClicked(menuItem) }, text = menuItem)
            }
        }
    }
}

@Composable
fun ResultBox(
    item: String,
    text: String,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Result",
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.Center),
        )
    }
    Text(
        text = "Item: $item\nResult: $text",
        modifier =
        Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(color = Color.LightGray),
    )
}

@Composable
fun <T> FullScreenDialog(
    items: List<T>,
    itemToString: (T) -> String,  // 아이템을 String으로 변환하는 함수
    onDismiss: () -> Unit,
    onConfirm: ((List<T>) -> Unit)? = null
) {
    val selectedItems = remember { mutableStateListOf<T>() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                onClick = onDismiss,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {}) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Select Items", fontSize = 20.sp, modifier = Modifier.padding(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    items.forEach { item ->
                        if (onConfirm != null) {
                            val isSelected = selectedItems.contains(item)
                            Button(
                                onClick = {
                                    if (isSelected) {
                                        selectedItems.remove(item)  // 이미 선택된 경우 해제
                                    } else {
                                        selectedItems.add(item)  // 선택되지 않은 경우 추가
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = if (isSelected) Color.Gray else Color.LightGray,
                                    contentColor = if (isSelected) Color.White else Color.Black
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(itemToString(item))  // 전달된 변환 함수 사용
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(itemToString(item), color = Color.Black)  // 아이템 텍스트로만 표시
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(onClick = {
                        if (onConfirm != null) {
                            onConfirm(selectedItems.toList())
                        }
                        onDismiss()
                    }) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownDialog(
    title: String,
    items: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit  // 선택된 항목과 두 개의 텍스트 입력 값 전달
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Choose an option") } // 선택된 항목 상태
    var textFieldValue1 by remember { mutableStateOf("") } // 첫 번째 텍스트 입력 상태
    var textFieldValue2 by remember { mutableStateOf("") } // 두 번째 텍스트 입력 상태

    // 키보드 제어를 위한 컨트롤러
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    AlertDialog(
        onDismissRequest = {
            // 다이얼로그 닫을 때 키보드도 닫음
            keyboardController?.hide()
            focusManager.clearFocus()
            onDismiss()
        },
        title = {
            Text(text = title)
        },
        text = {
            Column {
                // Dropdown menu within the dialog
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = selectedOption, style = MaterialTheme.typography.h6)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                if (expanded) {
                    items.forEach { item ->
                        Text(
                            text = item,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 32.dp, top = 8.dp, bottom = 8.dp)
                                .clickable {
                                    selectedOption = item // 선택된 항목 업데이트
                                    expanded = false // 선택 후 드롭다운 접기
                                },
                            color = Color.Gray
                        )
                    }
                }

                if (title == "Send") {
                    // 추가된 텍스트 입력 상자
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = textFieldValue1,
                        onValueChange = { textFieldValue1 = it },
                        label = { Text("To") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            // 완료 버튼 클릭 시 키보드 닫기
                            keyboardController?.show()
//                            keyboardController?.hide()
                            focusManager.clearFocus()
                        })
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = textFieldValue2,
                        onValueChange = { textFieldValue2 = it },
                        label = { Text("Amount") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            keyboardController?.hide()
                        })
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // 확인 버튼 클릭 시 선택된 옵션과 입력된 텍스트 값 전달
                    keyboardController?.hide() // 키보드 닫기
                    focusManager.clearFocus()
                    onConfirm(selectedOption, textFieldValue1, textFieldValue2)
                    onDismiss()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = {
                keyboardController?.hide() // 키보드 닫기
                focusManager.clearFocus()
                onDismiss()
            }) {
                Text("Close")
            }
        }
    )
}

@Composable
fun AttributeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit  // 선택된 항목과 두 개의 텍스트 입력 값 전달
) {
    var languageExpanded by remember { mutableStateOf(false) }
    var currencyExpanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("ko") }
    var selectedCurrency by remember { mutableStateOf("KRW") }
    val languageList = listOf("ko", "en", "ja")
    val currencyList = listOf("KRW", "USD", "JPY")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Change Language")
        },
        text = {
            Column {
                // Dropdown menu within the dialog
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { languageExpanded = !languageExpanded }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = selectedLanguage, style = MaterialTheme.typography.h6)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                if (languageExpanded) {
                    languageList.forEach { item ->
                        Text(
                            text = item,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 32.dp, top = 8.dp, bottom = 8.dp)
                                .clickable {
                                    selectedLanguage = item // 선택된 항목 업데이트
                                    languageExpanded = false // 선택 후 드롭다운 접기
                                },
                            color = Color.Gray
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { currencyExpanded = !currencyExpanded }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = selectedCurrency, style = MaterialTheme.typography.h6)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                if (currencyExpanded) {
                    currencyList.forEach { item ->
                        Text(
                            text = item,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 32.dp, top = 8.dp, bottom = 8.dp)
                                .clickable {
                                    selectedCurrency = item // 선택된 항목 업데이트
                                    currencyExpanded = false // 선택 후 드롭다운 접기
                                },
                            color = Color.Gray
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // 확인 버튼 클릭 시 선택된 옵션과 입력된 텍스트 값 전달
                    onConfirm(selectedLanguage, selectedCurrency)
                    onDismiss()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun ResultAlert(text: String, onConfirm: () -> Unit) {
    AlertDialog(
        title = {
            Text(text = "Result")
        },
        text = {
            Text(text = text)
        },
        onDismissRequest = onConfirm,
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Close")
            }
        }
    )
}
