package com.wepin.cm.widgetlib

import com.wepin.cm.loginlib.WepinLogin
import com.wepin.cm.loginlib.storage.StorageManager
import com.wepin.cm.loginlib.types.StorageDataType
import com.wepin.cm.loginlib.types.WepinLoginOptions
import com.wepin.cm.widgetlib.const.WidgetUrl
import com.wepin.cm.widgetlib.error.WepinError
import com.wepin.cm.widgetlib.storage.AppData
import com.wepin.cm.widgetlib.types.Account
import com.wepin.cm.widgetlib.types.AccountBalanceInfo
import com.wepin.cm.widgetlib.types.WepinLifeCycle
import com.wepin.cm.loginlib.types.WepinLoginStatus
import com.wepin.cm.loginlib.types.WepinUser
import com.wepin.cm.widgetlib.info.LocaleManager
import com.wepin.cm.widgetlib.network.WepinNetworkManager
import com.wepin.cm.widgetlib.types.AppNFT
import com.wepin.cm.widgetlib.types.DetailAccount
import com.wepin.cm.widgetlib.types.ErrorCode
import com.wepin.cm.widgetlib.types.GetAccountBalanceResponse
import com.wepin.cm.widgetlib.types.GetAccountListRequest
import com.wepin.cm.widgetlib.types.GetNFTRequest
import com.wepin.cm.widgetlib.types.GetNFTResponse
import com.wepin.cm.widgetlib.types.ITermsAccepted
import com.wepin.cm.widgetlib.types.JSRegisterRequestParameter
import com.wepin.cm.widgetlib.types.JSResponse
import com.wepin.cm.widgetlib.types.JSSendRequestParameter
import com.wepin.cm.widgetlib.types.NFTContract
import com.wepin.cm.widgetlib.types.RegisterRequest
import com.wepin.cm.widgetlib.types.SendData
import com.wepin.cm.widgetlib.types.SendResponse
import com.wepin.cm.widgetlib.types.TokenBalanceInfo
import com.wepin.cm.widgetlib.types.UpdateTermsAcceptedRequest
import com.wepin.cm.widgetlib.types.WepinNFT
import com.wepin.cm.widgetlib.types.WepinNFTContract
import com.wepin.cm.widgetlib.types.WidgetAttributes
import com.wepin.cm.widgetlib.utils.BigInteger
import com.wepin.cm.widgetlib.webview.SDKRequest
import com.wepin.cm.widgetlib.webview.WebViewManager
import com.wepin.cm.widgetlib.webview.WebViewResponseManager
import kotlinx.coroutines.CompletableDeferred

class WepinWidgetSDK(wepinOptions: WepinLoginOptions) {
    private val _context = wepinOptions.context
    private val _appId = wepinOptions.appId
    private val _appKey = wepinOptions.appKey

    var login: WepinLogin? = null
    private var _isInitialized: Boolean = false

    private var _webViewManager: WebViewManager? = null
    private var wepinLifeCycle: WepinLifeCycle? = null

    private var _userInfo: WepinUser? = null

    private var _wepinNetworkManager: WepinNetworkManager? = null

    private var _accountInfo: ArrayList<Account>? = null
    private var _detailAccounts: ArrayList<DetailAccount>? = null

    companion object {
        internal var _instance: WepinWidgetSDK? = null

        internal fun getInstance(): WepinWidgetSDK {
            return _instance as WepinWidgetSDK ?: throw WepinError.NOT_INITIALIZED_ERROR
        }
    }

    suspend fun init(attributes: WidgetAttributes): Boolean {
        if (_isInitialized) {
            throw WepinError(WepinError.ALREADY_INITIALIZED_ERROR)
        }
        AppData.setAppId(_appId)
        AppData.setAppKey(_appKey)
        AppData.setContext(_context)
        AppData.setAttributes(attributes)
        AppData.setWidgetUrl(_appKey)
        _webViewManager = WebViewManager.getInstance()
        _wepinNetworkManager = WepinNetworkManager()
        _accountInfo = null
        _detailAccounts = null
        _userInfo = null
        try {
            _isInitialized = false
            wepinLifeCycle = WepinLifeCycle.INITIALIZING
            val initOptions = WepinLoginOptions(
                context = _context,
                appId = _appId,
                appKey = _appKey
            )
            login = WepinLogin(initOptions)
            _isInitialized = login!!.init()
            _instance = this
        } catch (e: Exception) {
            if (e is WepinError) throw e
            throw WepinError.generalUnKnownEx("$e")
        }

        return _isInitialized
    }

    fun isInitalized(): Boolean {
        return _isInitialized
    }

    internal suspend fun _checkLoginStatusAndSetLifecycle() {
        if (!_isInitialized) {
            throw WepinError(WepinError.NOT_INITIALIZED_ERROR)
        }
        try {
            _userInfo = login!!.getCurrentWepinUser()
        } catch (error: Exception) {
            wepinLifeCycle = WepinLifeCycle.INITIALIZED
        }
        wepinLifeCycle = if (_userInfo !== null) {
            if (_userInfo!!.userStatus?.loginStatus!! != WepinLoginStatus.COMPLETE) {
                WepinLifeCycle.LOGIN_BEFORE_REGISTER
            } else {
                WepinLifeCycle.LOGIN
            }
        } else {
            WepinLifeCycle.INITIALIZED
        }
    }

    suspend fun openWidget() {
        _checkLoginStatusAndSetLifecycle()
        _open()
    }

    private fun _open() {
        _webViewManager!!.openWidget()
    }

    fun closeWidget() {
        _webViewManager!!.closeWidget()
    }

    suspend fun getAccounts(
        networks: List<String>? = null,
        withEoa: Boolean? = null
    ): ArrayList<Account>? {
        if (!_isInitialized) {
            throw WepinError.NOT_INITIALIZED_ERROR
        }
        if (getStatus() != WepinLifeCycle.LOGIN && _userInfo == null) {
            throw WepinError.generateExWithMessage(
                ErrorCode.INCORRECT_LIFECYCLE_EXCEPTION,
                "The LifeCycle of wepin SDK has to be login"
            )
        }

        val userId = _userInfo!!.userInfo?.userId
        val walletId = _userInfo!!.walletId
        val localeId = LocaleManager.getNumberFromLocale(AppData.getAttributes()!!.defaultLanguage)

        val accessToken = _userInfo!!.token!!.accessToken

        val accountList = _wepinNetworkManager!!.getAppAccountList(
            accessToken,
            GetAccountListRequest(
                walletId = walletId!!,
                userId = userId!!,
                localeId = localeId.toString()
            )
        )
        if (accountList.accounts.isEmpty()) {
            throw WepinError.generateExWithMessage(
                ErrorCode.ACCOUNT_NOT_FOUND,
                "Account List is empty"
            )
        }
        _detailAccounts = _filterAccountList(
            accounts = accountList.accounts,
            aaAccounts = accountList.aaAccounts,
            withEoa = withEoa ?: false
        )

        _accountInfo = _fromAppAccountList(_detailAccounts!!)
        if (!networks.isNullOrEmpty()) {
            return _accountInfo!!.filter { account -> networks.contains(account.network) }
                .toCollection(ArrayList())
        }
        return _accountInfo
    }

    private fun _fromAppAccountList(accountList: ArrayList<DetailAccount>): ArrayList<Account> {
        return accountList.map { account ->
            account.fromAppAccount()
        }.toCollection(ArrayList())
    }

    private fun _filterAccountList(
        accounts: ArrayList<DetailAccount>,
        aaAccounts: ArrayList<DetailAccount>? = null,
        withEoa: Boolean
    ): ArrayList<DetailAccount> {

        if (withEoa) {
            return if (aaAccounts != null) {
                arrayListOf<DetailAccount>().apply {
                    addAll(accounts)
                    addAll(aaAccounts)
                }
            } else {
                accounts
            }
        } else {
            return if (aaAccounts != null) {
                accounts.map { account ->
                    val aaAccount = aaAccounts.firstOrNull { aaAccount ->
                        aaAccount.coinId == account.coinId &&
                                aaAccount.contract == account.contract &&
                                aaAccount.eoaAddress == account.address
                    } ?: account
                    aaAccount
                }.toCollection(ArrayList())
            } else {
                accounts
            }
        }
    }

    suspend fun getStatus(): WepinLifeCycle {
        _checkLoginStatusAndSetLifecycle()
        return wepinLifeCycle!!
    }

    suspend fun getBalance(accounts: ArrayList<Account>): ArrayList<AccountBalanceInfo>? {
        if (!_isInitialized) {
            throw WepinError.NOT_INITIALIZED_ERROR
        }
        if (getStatus() != WepinLifeCycle.LOGIN && _userInfo == null) {
            throw WepinError(
                WepinError.INCORRECT_LIFECYCLE_EXCEPTION.getErrorCode(),
                "The LifeCycle of wepin SDK has to be login"
            )
        }

        val accessToken = _userInfo!!.token!!.accessToken

        getAccounts()

        if (_detailAccounts.isNullOrEmpty()) {
            throw WepinError.generateExWithMessage(
                ErrorCode.ACCOUNT_NOT_FOUND,
                "Account List is empty"
            )
        }

        val isAllAccounts = accounts.isEmpty()
        val balanceInfo = ArrayList<AccountBalanceInfo>()

        val filteredAccounts = if (isAllAccounts) {
            _detailAccounts!!
        } else {
            _detailAccounts!!.filter { dAccount ->
                accounts.any { acc ->
                    acc.network == dAccount.network && acc.address == dAccount.address && dAccount.contract == null
                }
            }
        }

        if (filteredAccounts.isEmpty()) {
            throw WepinError.generateExWithMessage(
                ErrorCode.ACCOUNT_NOT_FOUND,
                "No matching accounts found"
            )
        }

        // getAccountBalance Parallel processing without `futures`
        filteredAccounts.forEach { dAccount ->
            val balance = _wepinNetworkManager?.getAccountBalance(accessToken, dAccount.accountId)
            if (balance != null) {
                balanceInfo.add(_filterAccountBalance(_detailAccounts!!, dAccount, balance))
            }
        }

        if (balanceInfo.isEmpty()) {
            throw WepinError(
                WepinError.UNKNOWN_ERROR.getErrorCode(),
                "No balances found for the accounts"
            )
        }

        return balanceInfo
    }

    fun _filterAccountBalance(
        detailAccounts: List<DetailAccount>,
        dAccount: DetailAccount,
        balance: GetAccountBalanceResponse
    ): AccountBalanceInfo {

        val accTokens = detailAccounts.filter { acc ->
            acc.accountId == dAccount.accountId && acc.accountTokenId != null
        }

        val findTokens = if (balance.tokens.isNotEmpty()) {
            balance.tokens.filter { bal ->
                accTokens.any { t -> t.contract == bal.contract }
            }.map { x ->
                TokenBalanceInfo(
                    contract = x.contract,
                    balance = _getBalanceWithDecimal(x.balance, x.decimals),
                    symbol = x.symbol
                )
            }
        } else {
            emptyList()
        }

        return AccountBalanceInfo(
            network = dAccount.network,
            address = dAccount.address,
            balance = _getBalanceWithDecimal(balance.balance, balance.decimals),
            symbol = dAccount.symbol,
            tokens = ArrayList(findTokens) // Convert to ArrayList if needed
        )
    }

    private fun _getBalanceWithDecimal(balance: String, decimals: Int): String {
        if (decimals == 0 || balance.isEmpty()) return "0"

        val balanceValue = BigInteger(balance)
        val divisor = BigInteger("10").pow(decimals)

        val wholePart = balanceValue / divisor
        val fractionalPart = balanceValue % divisor

        val fractionalPartLength = divisor.toString().length - 1
        var fractionalPartString = fractionalPart.toString().padStart(fractionalPartLength, '0')
            .substring(0, fractionalPartLength)

        fractionalPartString = fractionalPartString.replace(Regex("0+$"), "")

        return if (fractionalPartString.isEmpty()) {
            wholePart.toString()
        } else {
            "$wholePart.$fractionalPartString"
        }
    }

    suspend fun getNFTs(refresh: Boolean, networks: List<String>? = null): ArrayList<WepinNFT> {
        if (!_isInitialized) {
            throw WepinError.NOT_INITIALIZED_ERROR
        }
        if (getStatus() != WepinLifeCycle.LOGIN && _userInfo == null) {
            throw WepinError.generateExWithMessage(
                ErrorCode.INCORRECT_LIFECYCLE_EXCEPTION,
                "The LifeCycle of wepin SDK has to be login"
            )
        }

        getAccounts()

        if (_detailAccounts.isNullOrEmpty()) {
            throw WepinError.generateExWithMessage(
                ErrorCode.ACCOUNT_NOT_FOUND,
                "Account list is empty"
            )
        }

        val userId = _userInfo!!.userInfo?.userId
        val walletId = _userInfo!!.walletId
        val accessToken = _userInfo!!.token!!.accessToken

        val detailNftList: GetNFTResponse? = if (refresh) {
            _wepinNetworkManager?.refreshNFTs(
                accessToken,
                GetNFTRequest(
                    walletId = walletId!!,
                    userId = userId!!
                )
            )
        } else {
            _wepinNetworkManager?.getNFTs(
                accessToken,
                GetNFTRequest(walletId = walletId!!, userId = userId!!)
            )
        }

        if (detailNftList == null) {
            throw WepinError.generateExWithMessage(
                ErrorCode.NFT_NOT_FOUND,
                "Nft list is empty"
            )
        }

        if (detailNftList.nfts.isEmpty()) {
            return ArrayList()
        }

        val allNetworks = networks.isNullOrEmpty()
        val nftList = ArrayList<WepinNFT>()
        val availableAccounts = _detailAccounts!!.filter { account ->
            allNetworks || networks!!.contains(account.network)
        }.toCollection(ArrayList())

        for (nft in detailNftList.nfts) {
            val filteredNft = _filterNft(nft, availableAccounts)
            if (filteredNft != null) {
                nftList.add(filteredNft)
            }
        }

        if (nftList.isEmpty()) {
            return ArrayList()
        }

        return nftList
    }

    private fun _filterNft(nft: AppNFT, dAccounts: ArrayList<DetailAccount>): WepinNFT? {
        val matchedAccount = dAccounts.firstOrNull { account ->
            nft.accountId == account.accountId
        }

        if (matchedAccount == null) {
            return null // 조건에 맞는 Account 없을 경우 null 반환
        }

        return WepinNFT(
            account = matchedAccount.fromAppAccount(),
            contract = WepinNFTContract(
                name = nft.contract.name,
                address = nft.contract.address,
//                scheme = NFTContract.schemeMapping[nft.contract.scheme] ?: nft.contract.scheme.toString(),
                scheme = NFTContract.schemeMapping[nft.contract.scheme]
                    ?: nft.contract.scheme.toString(),
                network = nft.contract.network,
                description = nft.contract.description,
                externalLink = nft.contract.externalLink,
                imageUrl = nft.contract.imageUrl
            ),
            name = nft.name,
            description = nft.description,
            externalLink = nft.externalLink,
            imageUrl = nft.imageUrl,
            contentType = AppNFT.contentTypeMapping[nft.contentType]!!,
            state = nft.state,
            contentUrl = nft.contentUrl,
            quantity = nft.quantity
        )
    }

    fun finalize() {
        _instance = null
        login!!.finalize()
        _isInitialized = false
    }

    suspend fun register(): WepinUser? {
        if (!_isInitialized) {
            throw WepinError(WepinError.NOT_INITIALIZED_ERROR)
        }
        if (getStatus() != WepinLifeCycle.LOGIN_BEFORE_REGISTER && _userInfo == null) {
            throw WepinError.generateExWithMessage(
                ErrorCode.INCORRECT_LIFECYCLE_EXCEPTION,
                "The LifeCycle of wepin SDK has to be login"
            )
        }

        val userStatus = _userInfo?.userStatus
        if (userStatus?.loginStatus == WepinLoginStatus.REGISTER_REQUIRED && userStatus.pinRequired != true) {
            val userId = _userInfo?.userInfo?.userId
            val walletId = _userInfo?.walletId
            val accessToken = _userInfo!!.token!!.accessToken


            // 비동기 API 호출을 suspend 함수로 실행
            _wepinNetworkManager?.register(
                accessToken,
                RegisterRequest(
                    appId = AppData.getAppId(),
                    userId = userId!!,
                    loginStatus = userStatus.loginStatus.value,
                    walletId = walletId!!
                )
            )
            _wepinNetworkManager?.updateTermsAccepted(
                accessToken,
                userId!!,
                UpdateTermsAcceptedRequest(
                    ITermsAccepted(
                        termsOfService = true,
                        privacyPolicy = true
                    )
                )
            )
            // 로컬 저장소 업데이트
            StorageManager.setStorage("user_status",
                StorageDataType.UserStatus(loginStatus = "complete", pinRequired = true)
            )

            // 로그인 상태 확인 및 라이프사이클 설정
            _checkLoginStatusAndSetLifecycle()

            return _userInfo!!
        } else {
            val parameter = JSRegisterRequestParameter(
                loginStatus = userStatus?.loginStatus!!,
                pinRequired = userStatus.pinRequired ?: false
            )

            val deferred = CompletableDeferred<JSResponse.JSResponseBody.JSRegisterResponseBodyData>()
            WebViewResponseManager.registerDeferred = deferred
            SDKRequest.setRequest(command = "register_wepin", parameter = parameter)
            _open()

            try {
                deferred.await()
            } catch (e: Exception) {
                throw WepinError.generateExWithMessage(ErrorCode.FAILED_REGISTER, e.toString())
            }
            return _userInfo!!
        }
    }


    fun changeLanguage(options: WidgetAttributes) {
        AppData.setAttributes(options)
    }

    suspend fun send(sendData: SendData): SendResponse {
        if (!_isInitialized) {
            throw WepinError.NOT_INITIALIZED_ERROR
        }
        if (getStatus() != WepinLifeCycle.LOGIN_BEFORE_REGISTER && _userInfo == null) {
            throw WepinError.generateExWithMessage(
                ErrorCode.INCORRECT_LIFECYCLE_EXCEPTION,
                "The LifeCycle of wepin SDK has to be login"
            )
        }
        getAccounts()

        if (_detailAccounts == null || _detailAccounts!!.isEmpty()) {
            throw WepinError.generateExWithMessage(
                ErrorCode.ACCOUNT_NOT_FOUND,
                "\"No Account Found"
            )
        }

        val filteredAccounts = _detailAccounts!!.filter { dAccount ->
            sendData.account.network == dAccount.network
                    && sendData.account.address == dAccount.address
                    && sendData.account.contract == null
        }

        if (filteredAccounts.isEmpty()) {
            throw WepinError(WepinError.ACCOUNT_NOT_FOUND)
        }

        sendData.txData.let {
            if (it.amount.isNotEmpty() && it.toAddress.isNotEmpty()) {
                it.amount = _normalizeAmount(it.amount)
            }
        }

        val deferred = CompletableDeferred<String>()
        WebViewResponseManager.sendDeferred = deferred
        SDKRequest.setRequest(
            command = "send_transaction_without_provider",
            parameter = JSSendRequestParameter(
                account = sendData.account,
                from = sendData.account.address,
                to = sendData.txData.toAddress,
                value = sendData.txData.amount
            )
        )
        _open()

        try {
            val response = deferred.await()
            return SendResponse(txId = response)
        } catch (e: Exception) {
            throw WepinError.generateExWithMessage(ErrorCode.FAILED_SEND, e.toString())
        }
    }

    private fun _normalizeAmount(amount: String): String {
        val regex = Regex("""^\d+(\.\d+)?$""")
        if (regex.matches(amount)) {
            return amount
        } else {
            throw WepinError.INVALID_PARAMETER
        }
    }
}
