package com.wepin.cm.widgetlib.types

import kotlinx.serialization.Serializable

@Serializable
data class NFTContract(
    val coinId: Int,
    val name: String,
    val address: String,
    val scheme: Int,
    val description: String? = null,
    val network: String,
    val externalLink: String? = null,
    val imageUrl: String? = null
) {
    companion object {
        val schemeMapping = mapOf(
            1 to "ERC721",
            2 to "ERC1155",
            3 to "SBT",
            4 to "DNFT",
            5 to "SOLANA_SFA",
            6 to "KIP37",
            7 to "KIP17"
        )

        fun fromJson(json: Map<String, Any>): NFTContract {
            return NFTContract(
                coinId = json["coinId"] as Int,
                name = json["name"] as String,
                address = json["address"] as String,
                scheme = json["scheme"] as Int,
                description = json["description"] as String?,
                network = json["network"] as String,
                externalLink = json["externalLink"] as String?,
                imageUrl = json["imageUrl"] as String?
            )
        }
    }

    fun toJson(): Map<String, Any?> {
        return mapOf(
            "coinId" to coinId,
            "name" to name,
            "address" to address,
            "scheme" to scheme,
            "description" to description,
            "network" to network,
            "externalLink" to externalLink,
            "imageUrl" to imageUrl
        )
    }
}
@Serializable
data class AppNFT (
    val contract: NFTContract,
    val id: String,
    val accountId: String,
    val name: String,
    val description: String,
    val tokenId: String,
    val externalLink: String,
    val imageUrl: String,
    val contentUrl: String? = null,
    val quantity: Int? = null,
    val contentType: Int,
    val state: Int
) {
    companion object {
        val contentTypeMapping = mapOf(
            1 to "image",
            2 to "video",
        )
    }
}
@Serializable
data class GetNFTResponse(
    val nfts: ArrayList<AppNFT>
)