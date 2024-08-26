package com.wepin.cm.widgetlib.types

import kotlinx.serialization.Serializable

@Serializable
data class ITermsAccepted(
    val termsOfService: Boolean,
    val privacyPolicy: Boolean
)
@Serializable
data class UpdateTermsAcceptedRequest(
    val termsAccepted: ITermsAccepted
)