package com.lost.domain.assistance.policy

/** Qué hace LOST ante una situación. Lo decide la cadena [AssistanceResponsePolicy]. */
enum class AssistanceAction {
    NONE,
    SOFT_ASK,
    VIBRATE_AND_ASK,
    SHOW_ALTERNATIVE_ROUTE,
    SHARE_LOCATION
}
