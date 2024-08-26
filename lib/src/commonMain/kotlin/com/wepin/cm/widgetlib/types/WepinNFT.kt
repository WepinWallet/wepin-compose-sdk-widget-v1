package com.wepin.cm.widgetlib.types

data class WepinNFTContract(
    val name: String,
    val address: String,
    val scheme: String,
    val description: String? = "",
    val network: String,
    val externalLink: String? = "",
    val imageUrl: String? = ""
)

data class WepinNFT(
    val account: Account,
    val contract: WepinNFTContract,
    val name: String,
    val description: String,
    val externalLink: String,
    val imageUrl: String,
    val contentUrl: String? = "",
    val quantity: Int? = null,
    val contentType: String,
    val state: Int
)