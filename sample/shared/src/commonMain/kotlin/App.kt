import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wepin.cm.loginlib.types.*
import com.wepin.cm.loginlib.types.network.LoginOauthAccessTokenRequest
import com.wepin.cm.loginlib.types.network.LoginOauthIdTokenRequest
import com.wepin.cm.widgetlib.WepinWidgetSDK
import com.wepin.cm.widgetlib.types.Account
import com.wepin.cm.widgetlib.types.AccountBalanceInfo
import com.wepin.cm.widgetlib.types.SendData
import com.wepin.cm.widgetlib.types.TxData
import com.wepin.cm.widgetlib.types.WepinLifeCycle
import com.wepin.cm.widgetlib.types.WepinNFT
import com.wepin.cm.widgetlib.types.WidgetAttributes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class WidgetManager(context: Any) {
    var loginResult: LoginResult? by mutableStateOf(null)
    val _context = context
    val privateKey = "privateKey"
    val wepinWidgetSDK = WepinWidgetSDK(
        WepinLoginOptions(
            context = _context,
            appId = "appId",
            appKey = "appKey"
        )
    )
    var wepinStatus: WepinLifeCycle = WepinLifeCycle.NOT_INITIALIZED
    var nftList = arrayListOf<WepinNFT>()
    var accountsList = arrayListOf<Account>()
    var selectedAccounts = arrayListOf<Account>()
    var balanceList = arrayListOf<AccountBalanceInfo>()

    private fun signupWithEmail(
        email: String,
        password: String,
        coroutineScope: CoroutineScope,
        setResponse: (LoginResult?) -> Unit,
        setText: (String) -> Unit,
    ) {
        val loginOption = LoginWithEmailParams(email, password)
        coroutineScope.launch {
            try {
                val response = wepinWidgetSDK.login!!.signUpWithEmailAndPassword(loginOption)
                setResponse(response)
                setText("$response")
            } catch (e: Exception) {
                setText("fail - $e")
            }
        }
    }

    private fun loginWithEmail(
        email: String,
        password: String,
        coroutineScope: CoroutineScope,
        setResponse: (LoginResult?) -> Unit,
        setText: (String) -> Unit,
    ) {
        val loginOption = LoginWithEmailParams(email, password)
        coroutineScope.launch {
            try {
                val response = wepinWidgetSDK.login!!.loginWithEmailAndPassword(loginOption)
                val result = wepinWidgetSDK.login!!.loginWepin(response)
                setResponse(response)
                setText("$result")
            } catch (e: Exception) {
                setText("fail - $e")
            }
        }
    }

    private fun loginOauth(
        provider: String,
        clientId: String,
        tokenType: OauthTokenType,
        coroutineScope: CoroutineScope,
        setResponse: (LoginResult?) -> Unit,
        setText: (String) -> Unit,
    ) {
        val loginOption = LoginOauth2Params(provider, clientId)
        coroutineScope.launch {
            try {
                val loginResponse = wepinWidgetSDK.login!!.loginWithOauthProvider(loginOption)
                when (tokenType) {
                    OauthTokenType.ID_TOKEN -> {
                        loginIdToken(
                            loginResponse.token,
                            setResponse,
                            setText,
                            coroutineScope,
                        )
                    }

                    OauthTokenType.ACCESS_TOKEN -> {
                        loginAccessToken(
                            loginResponse.provider,
                            loginResponse.token,
                            setResponse,
                            setText,
                            coroutineScope,
                        )
                    }

                    else -> {
                        setResponse(loginResponse as LoginResult)
                    }
                }
            } catch (e: Exception) {
                setResponse(null)
                setText("fail - $e")
            }
        }
    }

    private fun loginIdToken(
        token: String,
        setResponse: (LoginResult?) -> Unit,
        setText: (String) -> Unit,
        coroutineScope: CoroutineScope,
    ) {
        coroutineScope.launch {
            try {
                val sign = wepinWidgetSDK.login!!.getSignForLogin(
                    privateKey,
                    token,
                )
                val loginOption = LoginOauthIdTokenRequest(idToken = token, sign = sign)
                val loginResponse = wepinWidgetSDK.login!!.loginWithIdToken(loginOption)
                val response = wepinWidgetSDK.login!!.loginWepin(loginResponse)
                setResponse(loginResponse)
                setText("$response")
            } catch (e: Exception) {
                setResponse(null)
                setText("fail - ${e.message}")
            }
        }
    }

    private fun loginAccessToken(
        provider: String,
        token: String,
        setResponse: (LoginResult?) -> Unit,
        setText: (String) -> Unit,
        coroutineScope: CoroutineScope,
    ) {
        coroutineScope.launch {
            try {
                val sign = wepinWidgetSDK.login!!.getSignForLogin(
                    privateKey,
                    token,
                )
                val loginOption = LoginOauthAccessTokenRequest(provider, token, sign)
                val loginResponse = wepinWidgetSDK.login!!.loginWithAccessToken(loginOption)

                val response = wepinWidgetSDK.login!!.loginWepin(loginResponse)
                setResponse(loginResponse)
                setText("$response")
            } catch (e: Exception) {
                setResponse(null)
                setText("fail - ${e.message}")
            }
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
        coroutineScope.launch {
            try {
                val response = wepinWidgetSDK.login!!.logoutWepin()
                setText("$response")
            } catch (e: Exception) {
                setText("fail - ${e.message}")
            }
        }
    }

    fun send(
        coroutineScope: CoroutineScope,
        data: SendData,
        setText: (String) -> Unit
    ) {
        coroutineScope.launch {
            try {
                val txId = wepinWidgetSDK.send(data)
                setText("$txId")
            } catch (e: Exception) {
                setText("$e")
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
        setAccountList: (ArrayList<Account>) -> Unit,
        setBalanceList: (ArrayList<AccountBalanceInfo>) -> Unit,
        setNFTList: (ArrayList<WepinNFT>) -> Unit,
        openDialog: (String) -> Unit,
        setUserStatus: (WepinLifeCycle) -> Unit
    ) {
        setItem(item)
        setText("Processing...")
        when (item) {
            "Init" -> {
                val attributes =
                    WidgetAttributes(defaultLanguage = "ko", defaultCurrency = "KRW")
                coroutineScope.launch {
                    if (wepinWidgetSDK.isInitalized()) {
                        setText("It's already initalized")
                    } else {
                        val response = wepinWidgetSDK.init(attributes)
                        val wepinStatus = wepinWidgetSDK.getStatus()
                        setUserStatus(wepinStatus)
                        setText("$response")
                    }
                }
            }

            "Login with Google" -> {
                loginOauth(
                    provider = "google",
                    clientId = "googleClientId",
                    tokenType = OauthTokenType.ID_TOKEN,
                    coroutineScope = coroutineScope,
                    setResponse = setResponse,
                    setText = setText,
                )
                coroutineScope.launch {
                    val wepinStatus = wepinWidgetSDK.getStatus()
                    setUserStatus(wepinStatus)
                }

            }

            "Login with Apple" -> {
                loginOauth(
                    provider = "apple",
                    clientId = "appleClientId",
                    tokenType = OauthTokenType.ID_TOKEN,
                    coroutineScope = coroutineScope,
                    setResponse = setResponse,
                    setText = setText,
                )
                coroutineScope.launch {
                    val wepinStatus = wepinWidgetSDK.getStatus()
                    setUserStatus(wepinStatus)
                }
            }

            "Login with Discord" -> {
                loginOauth(
                    provider = "discord",
                    clientId = "DiscordClientId",
                    tokenType = OauthTokenType.ACCESS_TOKEN,
                    coroutineScope = coroutineScope,
                    setResponse = setResponse,
                    setText = setText,
                )
                coroutineScope.launch {
                    val wepinStatus = wepinWidgetSDK.getStatus()
                    setUserStatus(wepinStatus)
                }
            }

            "Login with Naver" -> {
                loginOauth(
                    provider = "naver",
                    clientId = "DiscordClientId",
                    tokenType = OauthTokenType.ACCESS_TOKEN,
                    coroutineScope = coroutineScope,
                    setResponse = setResponse,
                    setText = setText,
                )
                coroutineScope.launch {
                    val wepinStatus = wepinWidgetSDK.getStatus()
                    setUserStatus(wepinStatus)
                }
            }

            "SignUp with Email" -> {
                signupWithEmail(
                    email = "email",
                    password = "password",
                    coroutineScope = coroutineScope,
                    setResponse = setResponse,
                    setText = setText,
                )
                coroutineScope.launch {
                    val wepinStatus = wepinWidgetSDK.getStatus()
                    setUserStatus(wepinStatus)
                }
            }

            "Login with Email" -> {
                loginWithEmail(
                    email = "email",
                    password = "password",
                    coroutineScope = coroutineScope,
                    setResponse = setResponse,
                    setText = setText,
                )
                coroutineScope.launch {
                    val wepinStatus = wepinWidgetSDK.getStatus()
                    setUserStatus(wepinStatus)
                }
            }

            "Open Widget" -> {
                coroutineScope.launch {
                    wepinWidgetSDK.openWidget()
                }
            }

            "Change Language" -> {
                openDialog("Attribute")
            }

            "Get Account" -> {
                coroutineScope.launch {
                    try {
                        val accountsList = wepinWidgetSDK.getAccounts()!!
                        val wepinStatus = wepinWidgetSDK.getStatus()
                        setUserStatus(wepinStatus)
                        setAccountList(accountsList)
                        setText("Success")
                    } catch (e: Exception) {
                        setText("fail - $e")
                    }
                }
            }

            "Get NFTs" -> {
                coroutineScope.launch {
                    try {
                        val nftList = wepinWidgetSDK.getNFTs(refresh = false)
                        val wepinStatus = wepinWidgetSDK.getStatus()
                        setUserStatus(wepinStatus)
                        setNFTList(nftList)
                        setText("Success")
                        openDialog("NFT")
                    } catch (e: Exception) {
                        setText("fail - $e")
                    }
                }
            }

            "Get NFTs(with refresh)" -> {
                coroutineScope.launch {
                    try {
                        val nftList = wepinWidgetSDK.getNFTs(refresh = true)
                        val wepinStatus = wepinWidgetSDK.getStatus()
                        setUserStatus(wepinStatus)
                        setNFTList(nftList)
                        setText("Success")
                        openDialog("NFT")
                    } catch (e: Exception) {
                        setText("fail - $e")
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
                    val balanceList = wepinWidgetSDK.getBalance(ArrayList(balanceAccount))
                    setText("Success")
                    setBalanceList(balanceList!!)
                    openDialog("Balance")
                }
            }

            "Send" -> {
                coroutineScope.launch {
                    openDialog("Send")
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
    MaterialTheme {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Header()
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
                        setText = { text = it },
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
            ResultBox(item = item, text = text)
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
                            setText = { text = it }
                        )
                    },
                    onDismiss = { showDialog = false })

                "Attribute" -> AttributeDialog(onConfirm = { language, currency ->
                    widgetManager.changeLanguage(
                        attributes = WidgetAttributes(
                            defaultCurrency = currency,
                            defaultLanguage = language
                        ),
                        setText = { text = it }
                    )
                }, onDismiss = { showDialog = false })
            }
        }
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
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
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
    Box(modifier = Modifier.fillMaxWidth()) {
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
            .height(400.dp)
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
                "Login with Email" -> {
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
                "Get Account" -> {
                    if (userState !== WepinLifeCycle.LOGIN) {
                        show = false
                    }
                }

                "Get NFTs",
                "Get NFTs(with refresh)",
                "Account List View",
                "Get Balance",
                "Send" -> {
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

    AlertDialog(
        onDismissRequest = onDismiss,
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

                // 추가된 텍스트 입력 상자
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = textFieldValue1,
                    onValueChange = { textFieldValue1 = it },
                    label = { Text("To") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = textFieldValue2,
                    onValueChange = { textFieldValue2 = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // 확인 버튼 클릭 시 선택된 옵션과 입력된 텍스트 값 전달
                    onConfirm(selectedOption, textFieldValue1, textFieldValue2)
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