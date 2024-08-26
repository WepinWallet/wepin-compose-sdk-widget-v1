package com.wepin.cm.widgetlib.types

import kotlinx.serialization.Serializable

@Serializable
data class UpdateTermsAccepedResponse(
    val termsAccepted: ITermsAccepted
)
